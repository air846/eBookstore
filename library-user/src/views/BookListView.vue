<script setup lang="ts">
// 书库列表：分类筛选、搜索与分页
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
  size: 12,
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
    books.value = res.data.records;
    total.value = res.data.total;
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
  <div class="page-shell">
    <h1 class="page-title">书库检索</h1>
    <p class="page-subtitle">按分类与关键词快速筛选你想读的书。</p>

    <el-row :gutter="20">
      <el-col :xs="24" :md="6">
        <el-card class="filter-card">
          <h3 class="panel-title">分类筛选</h3>
          <el-menu class="menu">
            <el-menu-item @click="query.categoryId = undefined; query.page = 1; loadBooks()">全部</el-menu-item>
            <el-menu-item
              v-for="item in flatCategories"
              :key="item.id"
              @click="query.categoryId = item.id; query.page = 1; loadBooks()"
            >
              <span class="category-name" :style="{ paddingLeft: `${item.level * 14}px` }">
                {{ item.level > 0 ? "└ " : "" }}{{ item.name }}
              </span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="18">
        <el-card class="search-card">
          <el-space wrap>
            <el-input v-model="query.keyword" placeholder="输入书名或作者" clearable style="width: 280px" />
            <el-select v-model="query.sortBy" style="width: 160px" @change="query.page = 1; loadBooks()">
              <el-option label="最新上架" value="create_time" />
              <el-option label="最多阅读" value="visit_count" />
            </el-select>
            <el-button type="primary" @click="query.page = 1; loadBooks()">搜索</el-button>
          </el-space>
        </el-card>

        <el-row v-loading="loading" :gutter="20" class="grid">
          <el-col v-for="book in books" :key="book.id" :xs="24" :sm="12" :md="8">
            <el-card class="book-card" @click="router.push(`/book/${book.id}`)">
              <img :src="book.coverUrl" class="cover" />
              <div class="title">{{ book.title }}</div>
              <div class="meta">{{ book.author }}</div>
            </el-card>
          </el-col>
        </el-row>

        <div class="pager">
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.size"
            layout="total, prev, pager, next"
            :total="total"
            @current-change="loadBooks"
          />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.filter-card,
.search-card {
  padding: 8px;
}

.panel-title {
  margin: 6px 0 10px;
  font-size: 16px;
  font-weight: 600;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu-item) {
  border-radius: 8px;
  margin-bottom: 4px;
}

.category-name {
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.grid {
  margin-top: 18px;
}

.book-card {
  cursor: pointer;
  margin-bottom: 18px;
  border-radius: 14px;
  transition: transform 0.2s ease;
}

.book-card:hover {
  transform: translateY(-2px);
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
  justify-content: flex-end;
  margin: 10px 0 0;
}
</style>
