<script setup lang="ts">
// 首页：轮播与推荐入口
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
    const [carouselRes, hotRes, newRes] = await Promise.all([
      request.get("/carousel/list"),
      request.get("/book/list", { params: { page: 1, size: 4, sortBy: "visit_count", order: "desc" } }),
      request.get("/book/list", { params: { page: 1, size: 4, sortBy: "create_time", order: "desc" } })
    ]);
    carouselList.value = carouselRes.data;
    hotBooks.value = hotRes.data.records || [];
    newBooks.value = newRes.data.records || [];
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>

<template>
  <div v-loading="loading" class="page-shell home">
    <section class="hero el-card">
      <h1 class="page-title">在温润留白中，回归专注阅读</h1>
      <p class="page-subtitle">精选电子书 · 极简阅读器 · 章节化沉浸体验</p>
    </section>

    <el-carousel v-if="carouselList.length" height="320px" class="hero-carousel">
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <img :src="item.imageUrl" class="banner" />
      </el-carousel-item>
    </el-carousel>

    <section class="section el-card">
      <h2 class="section-title">热门书籍</h2>
      <el-row :gutter="20">
        <el-col v-for="item in hotBooks" :key="item.id" :xs="24" :sm="12" :md="6">
          <article class="book" @click="router.push(`/book/${item.id}`)">
            <img :src="item.coverUrl" class="cover" />
            <div class="title">{{ item.title }}</div>
            <div class="meta">{{ item.author }}</div>
          </article>
        </el-col>
      </el-row>
    </section>

    <section class="section el-card">
      <h2 class="section-title">新书上架</h2>
      <el-row :gutter="20">
        <el-col v-for="item in newBooks" :key="item.id" :xs="24" :sm="12" :md="6">
          <article class="book" @click="router.push(`/book/${item.id}`)">
            <img :src="item.coverUrl" class="cover" />
            <div class="title">{{ item.title }}</div>
            <div class="meta">{{ item.author }}</div>
          </article>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  gap: 24px;
}

.hero {
  padding: 30px 34px;
}

.hero-carousel {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: var(--shadow-light);
}

.banner {
  width: 100%;
  height: 320px;
  object-fit: cover;
}

.section {
  padding: 22px 24px 6px;
}

.section-title {
  margin: 0 0 20px;
  font-size: 20px;
  line-height: 1.3;
  font-weight: 600;
}

.book {
  cursor: pointer;
  margin-bottom: 18px;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.book:hover {
  transform: translateY(-3px);
}

.cover {
  width: 100%;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: var(--shadow-light);
}

.title {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.45;
}

.meta {
  margin-top: 4px;
  color: var(--text-muted);
  font-size: 13px;
}
</style>
