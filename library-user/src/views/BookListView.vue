<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import request from "../utils/request";

const router = useRouter();
const categories = ref<any[]>([]);
const books = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const query = reactive({
  page: 1,
  size: 12,
  keyword: "",
  categoryId: undefined as number | undefined
});

async function loadCategories() {
  const res = await request.get("/category/tree");
  categories.value = res.data;
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
  <el-row :gutter="16">
    <el-col :span="5">
      <el-card>
        <template #header>分类筛选</template>
        <el-menu>
          <el-menu-item @click="query.categoryId = undefined; query.page = 1; loadBooks()">全部</el-menu-item>
          <el-menu-item
            v-for="item in categories"
            :key="item.id"
            @click="query.categoryId = item.id; query.page = 1; loadBooks()"
          >
            {{ item.name }}
          </el-menu-item>
        </el-menu>
      </el-card>
    </el-col>
    <el-col :span="19">
      <el-card>
        <el-space>
          <el-input v-model="query.keyword" placeholder="输入书名或作者" clearable />
          <el-button type="primary" @click="query.page = 1; loadBooks()">搜索</el-button>
        </el-space>
      </el-card>
      <el-row v-loading="loading" :gutter="16" class="grid">
        <el-col v-for="book in books" :key="book.id" :span="8">
          <el-card class="book-card" shadow="hover" @click="router.push(`/book/${book.id}`)">
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
</template>

<style scoped>
.grid {
  margin-top: 12px;
}
.book-card {
  margin-bottom: 12px;
  cursor: pointer;
}
.cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.title {
  margin-top: 8px;
  font-weight: 600;
}
.meta {
  font-size: 12px;
  color: #6b7280;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin: 12px 0;
}
</style>
