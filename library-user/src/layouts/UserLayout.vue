<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { House, Collection, Notebook, User } from "@element-plus/icons-vue";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const activePath = computed(() => {
  if (route.path.startsWith("/reader/")) return "/reader";
  if (route.path.startsWith("/books")) return "/books";
  if (route.path.startsWith("/bookshelf")) return "/bookshelf";
  if (route.path.startsWith("/profile")) return "/profile";
  return "/";
});

const isReaderPage = computed(() => activePath.value === "/reader");

const tabs = [
  { path: "/", label: "首页", icon: House },
  { path: "/books", label: "书库", icon: Collection },
  { path: "/bookshelf", label: "书架", icon: Notebook },
  { path: "/profile", label: "我的", icon: User }
];

function to(path: string) {
  router.push(path);
}

function logout() {
  authStore.logout();
  router.push("/login");
}

function onTabClick(path: string, event: MouseEvent) {
  const host = event.currentTarget as HTMLElement | null;
  if (host) {
    const rect = host.getBoundingClientRect();
    const ripple = document.createElement("span");
    ripple.className = "tab-ripple";
    ripple.style.left = `${event.clientX - rect.left}px`;
    ripple.style.top = `${event.clientY - rect.top}px`;
    host.appendChild(ripple);
    window.setTimeout(() => ripple.remove(), 420);
  }
  if ("vibrate" in navigator) {
    navigator.vibrate(12);
  }
  to(path);
}
</script>

<template>
  <div :class="['layout', { reader: isReaderPage }]">
    <header v-if="!isReaderPage" class="header glass-topbar">
      <div class="brand" @click="to('/')">
        <div class="brand-title">云笺书馆</div>
        <div class="brand-sub">Cloud Reading</div>
      </div>
      <nav class="nav">
        <el-button v-if="!authStore.isLogin" size="small" type="primary" @click="to('/login')">登录</el-button>
        <el-button v-else size="small" @click="logout">退出</el-button>
      </nav>
    </header>

    <main class="main">
      <router-view />
    </main>

    <nav v-if="!isReaderPage" class="tabbar">
      <button
        v-for="item in tabs"
        :key="item.path"
        :class="{ active: activePath === item.path }"
        @click="onTabClick(item.path, $event)"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </button>
    </nav>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  max-width: 460px;
  margin: 0 auto;
  background: rgba(255, 251, 245, 0.5);
  box-shadow: 0 0 0 1px rgba(125, 102, 78, 0.08);
  display: flex;
  flex-direction: column;
}

.layout.reader {
  max-width: none;
  box-shadow: none;
  background: transparent;
}

.header {
  position: sticky;
  top: 0;
  z-index: 50;
  height: 66px;
  padding: 0 14px;
  border-bottom: 1px solid rgba(125, 102, 78, 0.14);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.glass-topbar {
  background: rgba(250, 245, 238, 0.66);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.brand {
  cursor: pointer;
  user-select: none;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #392f24;
  line-height: 1.2;
  letter-spacing: 1px;
}

.brand-sub {
  margin-top: 2px;
  font-size: 11px;
  letter-spacing: 0.6px;
  color: #8d7d6d;
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav :deep(.el-button) {
  padding-inline: 12px;
}

.nav :deep(.el-button.active) {
  color: #7f654c;
  background: rgba(154, 127, 98, 0.12);
}

.main {
  flex: 1;
  width: 100%;
  margin: 0 auto;
  padding: 0 0 90px;
  overflow-y: visible;
}

.layout.reader .main {
  padding-bottom: 0;
}

.tabbar {
  position: fixed;
  left: 50%;
  bottom: 10px;
  transform: translateX(-50%);
  width: min(440px, calc(100vw - 16px));
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 8px;
  border-radius: 14px;
  border: 1px solid rgba(125, 102, 78, 0.16);
  background: rgba(255, 251, 245, 0.92);
  backdrop-filter: blur(12px);
  z-index: 70;
}

.tabbar button {
  border: none;
  background: none;
  color: #8f8376;
  font-size: 11px;
  padding: 6px 0;
  display: grid;
  justify-items: center;
  gap: 2px;
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  transition: transform 0.18s ease, color 0.18s ease, background-color 0.18s ease;
}

.tabbar button :deep(svg) {
  width: 16px;
  height: 16px;
}

.tabbar button.active {
  color: #7f654c;
  background: rgba(154, 127, 98, 0.14);
  font-weight: 700;
  transform: translateY(-1px);
}

:deep(.tab-ripple) {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: rgba(154, 127, 98, 0.24);
  transform: translate(-50%, -50%) scale(0);
  pointer-events: none;
  animation: tab-ripple 0.42s ease-out;
}

@keyframes tab-ripple {
  to {
    transform: translate(-50%, -50%) scale(12);
    opacity: 0;
  }
}
</style>
