<script setup lang="ts">
// 书架页面：收藏列表、搜索与继续阅读
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "../utils/request";

interface BookItem {
  id: number;
  title: string;
  author?: string;
  coverUrl?: string;
  description?: string;
  fileType?: string;
}

const router = useRouter();
const loading = ref(false);
const keyword = ref("");
const items = ref<BookItem[]>([]);
const lastChapterMap = ref<Record<number, string>>({});

const filteredItems = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) return items.value;
  return items.value.filter((item) => {
    const title = item.title?.toLowerCase() || "";
    const author = item.author?.toLowerCase() || "";
    return title.includes(key) || author.includes(key);
  });
});

async function loadFavorites() {
  loading.value = true;
  try {
    const [favoriteRes, historyRes] = await Promise.all([
      request.get("/book/favorite/list"),
      request.get("/book/history/list")
    ]);
    items.value = Array.isArray(favoriteRes.data) ? favoriteRes.data : [];
    const historyList = Array.isArray(historyRes.data) ? historyRes.data : [];
    const mapping: Record<number, string> = {};
    historyList.forEach((item: any) => {
      const bookId = Number(item.bookId);
      if (!Number.isNaN(bookId) && item.chapter && !mapping[bookId]) {
        mapping[bookId] = item.chapter;
      }
    });
    lastChapterMap.value = mapping;
  } finally {
    loading.value = false;
  }
}

function openDetail(book: BookItem) {
  router.push(`/book/${book.id}`);
}

function openReader(book: BookItem) {
  const chapter = lastChapterMap.value[book.id];
  if (chapter) {
    router.push({ path: `/reader/${book.id}`, query: { chapter } });
    return;
  }
  router.push(`/reader/${book.id}`);
}

async function removeFavorite(book: BookItem) {
  await ElMessageBox.confirm(`确认移除《${book.title}》吗？`, "提示", { type: "warning" });
  await request.delete(`/book/favorite/${book.id}`);
  ElMessage.success("已从书架移除");
  await loadFavorites();
}

onMounted(loadFavorites);
</script>

<template>
  <div class="page-shell bookshelf" v-loading="loading">
    <div class="header">
      <div>
        <h1 class="page-title">我的书架</h1>
        <p class="page-subtitle">收藏的书籍会在这里沉淀，随时继续阅读。</p>
      </div>
      <el-input v-model="keyword" placeholder="搜索书名或作者" clearable class="search" />
    </div>

    <el-empty v-if="!filteredItems.length" description="书架还没有内容" />

    <el-row v-else :gutter="20" class="grid">
      <el-col v-for="book in filteredItems" :key="book.id" :xs="24" :sm="12" :md="8">
        <el-card class="book-card" shadow="never">
          <div class="card-body">
            <img :src="book.coverUrl" class="cover" @click="openDetail(book)" />
            <div class="info">
              <div class="title" @click="openDetail(book)">{{ book.title }}</div>
              <div class="meta">{{ book.author || "未知作者" }}</div>
              <div class="desc">{{ book.description || "暂无简介" }}</div>
              <div class="actions">
                <el-button size="small" type="primary" @click="openReader(book)">继续阅读</el-button>
                <el-button size="small" @click="openDetail(book)">详情</el-button>
                <el-button size="small" type="danger" plain @click="removeFavorite(book)">移除</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.bookshelf {
  display: grid;
  gap: 18px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.search {
  width: min(320px, 100%);
}

.grid {
  margin-top: 6px;
}

.book-card {
  border-radius: 16px;
}

.card-body {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 16px;
}

.cover {
  width: 120px;
  height: 160px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: var(--shadow-light);
  cursor: pointer;
}

.info {
  display: grid;
  gap: 6px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.meta {
  font-size: 13px;
  color: var(--text-muted);
}

.desc {
  font-size: 13px;
  color: #5a4d41;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 6px;
}

@media (max-width: 900px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-body {
    grid-template-columns: 100px 1fr;
  }

  .cover {
    width: 100px;
    height: 140px;
  }
}
</style>
