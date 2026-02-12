<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import request from "../utils/request";

const router = useRouter();
const loading = ref(false);
const carouselList = ref<any[]>([]);
const hotBooks = ref<any[]>([]);
const newBooks = ref<any[]>([]);

async function loadData() {
  loading.value = true;
  try {
    const [carouselRes, booksRes] = await Promise.all([
      request.get("/carousel/list"),
      request.get("/book/list", { params: { page: 1, size: 8, sortBy: "visit_count", order: "desc" } })
    ]);
    carouselList.value = carouselRes.data;
    hotBooks.value = booksRes.data.records.slice(0, 4);
    newBooks.value = booksRes.data.records.slice(4);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>

<template>
  <div v-loading="loading" class="home">
    <el-carousel v-if="carouselList.length" height="260px">
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <img :src="item.imageUrl" class="banner" />
      </el-carousel-item>
    </el-carousel>

    <el-card class="section">
      <template #header>热门书籍</template>
      <el-row :gutter="16">
        <el-col v-for="item in hotBooks" :key="item.id" :span="6">
          <el-card shadow="hover" class="book" @click="router.push(`/book/${item.id}`)">
            <img :src="item.coverUrl" class="cover" />
            <div class="title">{{ item.title }}</div>
            <div class="meta">{{ item.author }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section">
      <template #header>新书上架</template>
      <el-row :gutter="16">
        <el-col v-for="item in newBooks" :key="item.id" :span="6">
          <el-card shadow="hover" class="book" @click="router.push(`/book/${item.id}`)">
            <img :src="item.coverUrl" class="cover" />
            <div class="title">{{ item.title }}</div>
            <div class="meta">{{ item.author }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  gap: 16px;
}
.banner {
  width: 100%;
  height: 260px;
  object-fit: cover;
}
.section {
  margin-top: 12px;
}
.book {
  cursor: pointer;
}
.cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 4px;
}
.title {
  margin-top: 8px;
  font-weight: 600;
}
.meta {
  color: #6b7280;
  font-size: 12px;
}
</style>
