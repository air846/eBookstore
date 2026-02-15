<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAdminAuthStore } from "../stores/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAdminAuthStore();

const active = computed(() => {
  const path = route.path;
  if (path.startsWith("/books")) return "/books";
  if (path.startsWith("/categories")) return "/categories";
  if (path.startsWith("/users")) return "/users";
  if (path.startsWith("/interactions")) return "/interactions";
  if (path.startsWith("/system")) return "/system";
  return "/";
});

function go(path: string) {
  router.push(path);
}

function logout() {
  authStore.logout();
  router.push("/login");
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <div class="title">云笺管理台</div>
        <div class="sub">Elegant Console</div>
      </div>

      <el-menu :default-active="active" class="menu">
        <el-menu-item index="/" @click="go('/')">控制台</el-menu-item>
        <el-menu-item index="/books" @click="go('/books')">书籍管理</el-menu-item>
        <el-menu-item index="/categories" @click="go('/categories')">分类管理</el-menu-item>
        <el-menu-item index="/users" @click="go('/users')">用户管理</el-menu-item>
        <el-menu-item index="/interactions" @click="go('/interactions')">互动管理</el-menu-item>
        <el-menu-item index="/system" @click="go('/system')">系统设置</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">后台管理</div>
        <div class="header-right">
          <span>欢迎，{{ authStore.admin?.nickname || "管理员" }}</span>
          <el-button @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  padding: 20px 16px;
  border-right: 1px solid rgba(127, 114, 101, 0.16);
  background: rgba(252, 246, 238, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.logo {
  margin-bottom: 20px;
  padding: 10px 8px 14px;
  border-bottom: 1px solid rgba(127, 114, 101, 0.16);
}

.title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
  color: #392f24;
}

.sub {
  margin-top: 3px;
  font-size: 12px;
  color: #918170;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 6px;
  color: #4e4033;
}

.menu :deep(.is-active) {
  color: #7f654c;
  background: rgba(154, 127, 98, 0.12);
}

.header {
  height: 74px;
  padding: 0 28px;
  border-bottom: 1px solid rgba(127, 114, 101, 0.16);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(252, 246, 238, 0.66);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.4px;
  color: #3d3127;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #736456;
}

.main {
  padding: 24px;
}

@media (max-width: 1024px) {
  .aside {
    width: 200px !important;
    padding: 14px 10px;
  }

  .header {
    padding: 0 14px;
  }

  .main {
    padding: 14px;
  }
}
</style>
