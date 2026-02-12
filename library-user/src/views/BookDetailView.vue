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
  <el-card v-loading="loading">
    <el-row :gutter="24">
      <el-col :span="8">
        <img :src="detail.coverUrl" class="cover" />
      </el-col>
      <el-col :span="16">
        <h2>{{ detail.title }}</h2>
        <p>作者：{{ detail.author }}</p>
        <p>出版社：{{ detail.publisher }}</p>
        <p>ISBN：{{ detail.isbn }}</p>
        <p>{{ detail.description }}</p>
        <el-space>
          <el-button type="primary" @click="readBook">立即阅读</el-button>
          <el-button @click="favorite">收藏</el-button>
        </el-space>
      </el-col>
    </el-row>
  </el-card>
</template>

<style scoped>
.cover {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 8px;
}
</style>
