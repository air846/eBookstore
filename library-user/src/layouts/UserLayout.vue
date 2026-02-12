<script setup lang="ts">
// 用户端整体布局：顶部导航 + 路由内容区
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const activePath = computed(() => {
  if (route.path.startsWith("/books")) return "/books";
  if (route.path.startsWith("/bookshelf")) return "/bookshelf";
  if (route.path.startsWith("/profile")) return "/profile";
  return "/";
});

function to(path: string) {
  router.push(path);
}

function logout() {
  authStore.logout();
  router.push("/login");
}
</script>

<template>
  <div class="layout">
    <header class="header glass-topbar">
      <div class="brand" @click="to('/')">
        <div class="brand-title">云笺书馆</div>
        <div class="brand-sub">Elegant Reading Experience</div>
      </div>
      <nav class="nav">
        <el-button :class="{ active: activePath === '/' }" text @click="to('/')">首页</el-button>
        <el-button :class="{ active: activePath === '/books' }" text @click="to('/books')">书库</el-button>
        <el-button :class="{ active: activePath === '/bookshelf' }" text @click="to('/bookshelf')">书架</el-button>
        <el-button :class="{ active: activePath === '/profile' }" text @click="to('/profile')">我的</el-button>
        <el-button v-if="!authStore.isLogin" type="primary" @click="to('/login')">登录</el-button>
        <el-button v-else @click="logout">退出</el-button>
      </nav>
    </header>

    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  position: sticky;
  top: 0;
  z-index: 50;
  height: 78px;
  padding: 0 34px;
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
  font-size: 21px;
  font-weight: 700;
  color: #392f24;
  line-height: 1.2;
  letter-spacing: 1px;
}

.brand-sub {
  margin-top: 2px;
  font-size: 12px;
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
  width: min(1240px, calc(100% - 64px));
  margin: 28px auto 44px;
}

@media (max-width: 900px) {
  .header {
    height: auto;
    padding: 14px 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .main {
    width: calc(100% - 24px);
    margin: 18px auto 28px;
  }
}
</style>
