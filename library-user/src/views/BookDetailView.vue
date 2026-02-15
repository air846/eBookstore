<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const route = useRoute();
const router = useRouter();
const detail = ref<any>({});
const loading = ref(false);

async function loadDetail() {
  loading.value = true;
  try {
    const res = await request.get(`/book/detail/${route.params.id}`);
    detail.value = res.data || {};
  } finally {
    loading.value = false;
  }
}

async function favorite() {
  await request.post(`/book/favorite/${route.params.id}`);
  ElMessage.success("已加入书架");
}

function readBook() {
  router.push(`/reader/${route.params.id}`);
}

onMounted(loadDetail);
</script>

<template>
  <div class="page-shell detail" v-loading="loading">
    <el-card class="detail-card">
      <img :src="detail.coverUrl" class="cover" />
      <h1 class="page-title title">{{ detail.title || "书籍详情" }}</h1>

      <div class="meta-grid">
        <p><span class="label">作者</span>{{ detail.author || "-" }}</p>
        <p><span class="label">出版社</span>{{ detail.publisher || "-" }}</p>
        <p><span class="label">ISBN</span>{{ detail.isbn || "-" }}</p>
        <p><span class="label">文件类型</span>{{ detail.fileType || "-" }}</p>
      </div>

      <p class="desc">{{ detail.description || "暂无简介" }}</p>
    </el-card>

    <div class="action-bar">
      <el-button size="large" @click="favorite">收藏</el-button>
      <el-button type="primary" size="large" @click="readBook">立即阅读</el-button>
    </div>
  </div>
</template>

<style scoped>
.detail {
  display: grid;
  gap: 12px;
  padding-bottom: 12px;
}

.detail-card {
  padding: 10px;
}

.cover {
  width: 100%;
  max-width: 220px;
  margin: 2px auto 14px;
  display: block;
  aspect-ratio: 3 / 4;
  border-radius: 14px;
  object-fit: cover;
  box-shadow: var(--shadow-light);
}

.title {
  margin-bottom: 14px;
  font-size: 24px;
}

.meta-grid p {
  margin: 0 0 10px;
  color: #4a3f34;
}

.label {
  display: inline-block;
  width: 74px;
  color: var(--text-muted);
}

.desc {
  margin: 18px 0 6px;
  line-height: 1.8;
  color: #4f4337;
}

.action-bar {
  position: sticky;
  bottom: 76px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  padding: 10px;
  border-radius: 14px;
  background: rgba(255, 251, 245, 0.9);
  border: 1px solid rgba(125, 102, 78, 0.12);
}

.action-bar :deep(.el-button) {
  margin: 0;
}
</style>
