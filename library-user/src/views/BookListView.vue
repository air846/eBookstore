<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import request from "../utils/request";

interface CategoryNode {
  id: number;
  name: string;
  children?: CategoryNode[];
}

interface CategoryOption {
  id: number;
  name: string;
  level: number;
}

const router = useRouter();
const flatCategories = ref<CategoryOption[]>([]);
const books = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const query = reactive({
  page: 1,
  size: 10,
  keyword: "",
  categoryId: undefined as number | undefined,
  sortBy: "create_time",
  order: "desc"
});

function flattenCategoryTree(nodes: CategoryNode[], level = 0): CategoryOption[] {
  return nodes.flatMap((item) => {
    const current: CategoryOption = {
      id: item.id,
      name: item.name,
      level
    };
    const children = Array.isArray(item.children) ? flattenCategoryTree(item.children, level + 1) : [];
    return [current, ...children];
  });
}

async function loadCategories() {
  const res = await request.get("/category/tree");
  flatCategories.value = flattenCategoryTree(Array.isArray(res.data) ? res.data : []);
}

async function loadBooks() {
  loading.value = true;
  try {
    const res = await request.get("/book/list", { params: query });
    books.value = Array.isArray(res.data?.records) ? res.data.records : [];
    total.value = Number(res.data?.total || 0);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadCategories();
  await loadBooks();
});
</script>

<template>
  <div class="page-shell books-app">
    <h1 class="page-title">书库</h1>
    <p class="page-subtitle">发现下一本要读的书</p>

    <el-card class="search-card">
      <el-input v-model="query.keyword" placeholder="输入书名或作者" clearable />
      <div class="search-row">
        <el-select v-model="query.sortBy" @change="query.page = 1; loadBooks()">
          <el-option label="最新上架" value="create_time" />
          <el-option label="最多阅读" value="visit_count" />
        </el-select>
        <el-button type="primary" @click="query.page = 1; loadBooks()">搜索</el-button>
      </div>
      <div class="category-scroll">
        <button
          :class="{ active: query.categoryId === undefined }"
          @click="query.categoryId = undefined; query.page = 1; loadBooks()"
        >
          全部
        </button>
        <button
          v-for="item in flatCategories"
          :key="item.id"
          :class="{ active: query.categoryId === item.id }"
          @click="query.categoryId = item.id; query.page = 1; loadBooks()"
        >
          {{ item.name }}
        </button>
      </div>
    </el-card>

    <div v-if="loading" class="grid">
      <el-skeleton v-for="n in 6" :key="n" animated class="book-skeleton">
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; aspect-ratio: 3 / 4; border-radius: 10px" />
          <el-skeleton-item variant="text" style="margin-top: 8px; width: 80%" />
          <el-skeleton-item variant="text" style="margin-top: 4px; width: 54%" />
        </template>
      </el-skeleton>
    </div>

    <div v-else class="grid">
      <el-card v-for="book in books" :key="book.id" class="book-card" @click="router.push(`/book/${book.id}`)">
        <img :src="book.coverUrl" class="cover" />
        <div class="title">{{ book.title }}</div>
        <div class="meta">{{ book.author }}</div>
      </el-card>
    </div>

    <el-empty v-if="!loading && books.length === 0" description="暂无书籍" />

    <div class="pager">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        layout="prev, pager, next"
        :total="total"
        @current-change="loadBooks"
      />
    </div>
  </div>
</template>

<style scoped>
.books-app {
  display: grid;
  gap: 14px;
}

.search-card {
  padding: 12px;
}

.search-row {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}

.category-scroll {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.category-scroll button {
  border: 1px solid rgba(125, 102, 78, 0.16);
  border-radius: 999px;
  background: #fff;
  color: #6b5a49;
  font-size: 12px;
  white-space: nowrap;
  padding: 6px 12px;
}

.category-scroll button.active {
  color: #fff;
  background: #9a7f62;
  border-color: #9a7f62;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.book-card {
  cursor: pointer;
  border-radius: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 24px rgba(84, 63, 43, 0.12);
}

.cover {
  width: 100%;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  border-radius: 10px;
}

.title {
  margin-top: 10px;
  font-weight: 600;
  line-height: 1.45;
}

.meta {
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-muted);
}

.pager {
  display: flex;
  justify-content: center;
  margin: 10px 0 0;
}

.book-skeleton {
  width: 100%;
}
</style>
