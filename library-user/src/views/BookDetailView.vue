<script setup lang="ts">
// 书籍详情：展示简介、章节入口与收藏操作
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
    detail.value = res.data;
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
      <el-row :gutter="30">
        <el-col :xs="24" :md="8">
          <img :src="detail.coverUrl" class="cover" />
        </el-col>
        <el-col :xs="24" :md="16">
          <h1 class="page-title title">{{ detail.title }}</h1>
          <div class="meta-grid">
            <p><span class="label">作者</span>{{ detail.author || "-" }}</p>
            <p><span class="label">出版社</span>{{ detail.publisher || "-" }}</p>
            <p><span class="label">ISBN</span>{{ detail.isbn || "-" }}</p>
            <p><span class="label">文件类型</span>{{ detail.fileType || "-" }}</p>
          </div>
          <p class="desc">{{ detail.description || "暂无简介" }}</p>
          <el-space size="large">
            <el-button type="primary" size="large" @click="readBook">立即阅读</el-button>
            <el-button size="large" @click="favorite">收藏</el-button>
          </el-space>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.detail {
  padding-top: 8px;
}

.detail-card {
  padding: 10px;
}

.cover {
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: 14px;
  object-fit: cover;
  box-shadow: var(--shadow-light);
}

.title {
  margin-bottom: 14px;
}

.meta-grid p {
  margin: 0 0 10px;
  color: #4a3f34;
}

.label {
  display: inline-block;
  width: 88px;
  color: var(--text-muted);
}

.desc {
  margin: 18px 0 24px;
  line-height: 1.95;
  color: #4f4337;
}
</style>
