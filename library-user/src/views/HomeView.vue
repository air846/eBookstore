<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import request from "../utils/request";

interface BookItem {
  id: number;
  title: string;
  author?: string;
  coverUrl?: string;
}

interface CarouselItem {
  id: number;
  imageUrl: string;
  title?: string;
  subtitle?: string;
}

interface HistoryItem {
  bookId: number;
  chapter?: string;
  progress?: string;
}

const router = useRouter();
const loading = ref(false);
const carouselList = ref<CarouselItem[]>([]);
const hotBooks = ref<BookItem[]>([]);
const historyList = ref<HistoryItem[]>([]);

const allBooks = computed(() => {
  const map = new Map<number, BookItem>();
  hotBooks.value.forEach((item) => map.set(item.id, item));
  return Array.from(map.values());
});

const featureBanner = computed(() => carouselList.value[0] || null);

const continueRead = computed(() => {
  const latest = historyList.value[0];
  if (!latest) return null;
  const matched = allBooks.value.find((item) => item.id === Number(latest.bookId));
  return {
    bookId: Number(latest.bookId),
    title: matched?.title || "继续阅读",
    author: matched?.author || "",
    coverUrl: matched?.coverUrl || "",
    chapter: latest.chapter || "继续阅读",
    progress: latest.progress || "0%"
  };
});

function parseProgress(value?: string) {
  const n = Number(String(value || "").replace("%", ""));
  if (Number.isNaN(n)) return 0;
  return Math.max(0, Math.min(100, n));
}

function showRipple(event: MouseEvent) {
  const host = event.currentTarget as HTMLElement | null;
  if (!host) return;
  const rect = host.getBoundingClientRect();
  const ripple = document.createElement("span");
  ripple.className = "tap-ripple";
  ripple.style.left = `${event.clientX - rect.left}px`;
  ripple.style.top = `${event.clientY - rect.top}px`;
  host.appendChild(ripple);
  window.setTimeout(() => ripple.remove(), 520);
}

async function loadData() {
  loading.value = true;
  try {
    const [carouselRes, hotRes, historyRes] = await Promise.all([
      request.get("/carousel/list"),
      request.get("/book/list", { params: { page: 1, size: 8, sortBy: "visit_count", order: "desc" } }),
      request.get("/book/history/list")
    ]);
    carouselList.value = Array.isArray(carouselRes.data) ? carouselRes.data : [];
    hotBooks.value = Array.isArray(hotRes.data?.records) ? hotRes.data.records : [];
    historyList.value = Array.isArray(historyRes.data) ? historyRes.data : [];
  } finally {
    loading.value = false;
  }
}

function openDetail(bookId: number, event?: MouseEvent) {
  if (event) showRipple(event);
  router.push(`/book/${bookId}`);
}

function openBooks(event?: MouseEvent) {
  if (event) showRipple(event);
  router.push("/books");
}

function startRead(event?: MouseEvent) {
  if (event) showRipple(event);
  if (!continueRead.value) {
    router.push("/books");
    return;
  }
  router.push({
    path: `/reader/${continueRead.value.bookId}`,
    query: { chapter: continueRead.value.chapter }
  });
}

onMounted(loadData);
</script>

<template>
  <div class="home-mobile">
    <header class="home-header">
      <div>
        <p class="welcome">WELCOME TO</p>
        <h1 class="brand">云笺书馆</h1>
      </div>
      <el-button circle plain @click="openBooks($event)">搜</el-button>
    </header>

    <section v-if="featureBanner" class="hero ripple-host" @click="openBooks($event)">
      <img :src="featureBanner.imageUrl" alt="banner" />
      <div class="hero-mask">
        <span>本周主打</span>
        <h2>{{ featureBanner.title || "探索你的下一本好书" }}</h2>
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <h3>热门书籍</h3>
        <button type="button" @click="openBooks($event)">查看全部</button>
      </div>
      <div v-if="loading" class="book-row">
        <el-skeleton v-for="n in 4" :key="n" animated class="book-skeleton">
          <template #template>
            <el-skeleton-item variant="image" style="width: 112px; height: 150px; border-radius: 10px" />
            <el-skeleton-item variant="text" style="margin-top: 8px; width: 90px" />
            <el-skeleton-item variant="text" style="margin-top: 4px; width: 64px" />
          </template>
        </el-skeleton>
      </div>
      <div v-else-if="hotBooks.length" class="book-row">
        <article v-for="item in hotBooks" :key="item.id" class="book-card ripple-host" @click="openDetail(item.id, $event)">
          <img :src="item.coverUrl" :alt="item.title" />
          <p class="title">{{ item.title }}</p>
          <p class="author">{{ item.author || "未知作者" }}</p>
        </article>
      </div>
      <el-empty v-else description="暂无热门书籍" :image-size="72" />
    </section>

    <section v-if="continueRead" class="continue-card ripple-host" @click="startRead($event)">
      <img v-if="continueRead.coverUrl" :src="continueRead.coverUrl" :alt="continueRead.title" class="continue-cover" />
      <div v-else class="continue-cover placeholder">读</div>
      <div class="continue-main">
        <h4>{{ continueRead.title }}</h4>
        <p class="chapter">{{ continueRead.chapter }}</p>
        <div class="bar">
          <span :style="{ width: `${parseProgress(continueRead.progress)}%` }"></span>
        </div>
        <p class="progress">已读 {{ continueRead.progress }}</p>
      </div>
      <el-button type="primary" circle>读</el-button>
    </section>

  </div>
</template>

<style scoped>
.home-mobile {
  display: grid;
  gap: 16px;
  max-width: 430px;
  margin: 0 auto;
  padding: 10px 8px 14px;
}

.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
}

.welcome {
  margin: 0;
  font-size: 11px;
  letter-spacing: 2px;
  color: #8d6e63;
  font-weight: 700;
}

.brand {
  margin: 4px 0 0;
  font-size: 28px;
  line-height: 1.2;
}

.hero {
  position: relative;
  height: 192px;
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: var(--shadow-soft);
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.hero:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 34px rgba(84, 63, 43, 0.14);
}

.hero img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-mask {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 14px;
  color: #fff;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.68), transparent 60%);
}

.hero-mask span {
  font-size: 12px;
  opacity: 0.82;
}

.hero-mask h2 {
  margin: 4px 0 0;
  font-size: 20px;
}

.section {
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 251, 245, 0.76);
  border: 1px solid rgba(141, 110, 99, 0.14);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-head h3 {
  margin: 0;
  padding-left: 8px;
  border-left: 3px solid #9a7f62;
  font-size: 20px;
}

.section-head button {
  border: none;
  background: none;
  color: #9a7f62;
  cursor: pointer;
}

.book-row {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.book-card {
  min-width: 112px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
}

.book-card img {
  width: 112px;
  aspect-ratio: 3 / 4;
  border-radius: 10px;
  object-fit: cover;
  box-shadow: var(--shadow-light);
}

.title {
  margin: 8px 0 0;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--text-muted);
}

.continue-card {
  display: grid;
  grid-template-columns: 56px 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid rgba(141, 110, 99, 0.14);
  box-shadow: var(--shadow-light);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.continue-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 28px rgba(84, 63, 43, 0.12);
}

.continue-cover {
  width: 56px;
  height: 74px;
  border-radius: 8px;
  object-fit: cover;
}

.continue-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: #c3aa8f;
}

.continue-main h4 {
  margin: 0;
  font-size: 15px;
}

.chapter {
  margin: 4px 0;
  font-size: 12px;
  color: var(--text-muted);
}

.bar {
  height: 4px;
  border-radius: 99px;
  overflow: hidden;
  background: #eee5dc;
}

.bar span {
  display: block;
  height: 100%;
  background: #9a7f62;
}

.progress {
  margin: 4px 0 0;
  font-size: 11px;
  color: #9a7f62;
}

.book-skeleton {
  min-width: 112px;
}

.ripple-host {
  position: relative;
  overflow: hidden;
}

:deep(.tap-ripple) {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 999px;
  background: rgba(154, 127, 98, 0.25);
  transform: translate(-50%, -50%) scale(0);
  pointer-events: none;
  animation: home-ripple 0.52s ease-out;
}

@keyframes home-ripple {
  to {
    transform: translate(-50%, -50%) scale(16);
    opacity: 0;
  }
}
</style>
