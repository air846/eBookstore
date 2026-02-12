<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAdminAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAdminAuthStore();
const active = ref(window.location.pathname || "/");

function go(path: string) {
  active.value = path;
  router.push(path);
}

function logout() {
  authStore.logout();
  router.push("/login");
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">书城管理后台</div>
      <el-menu :default-active="active">
        <el-menu-item index="/" @click="go('/')">仪表盘</el-menu-item>
        <el-menu-item index="/books" @click="go('/books')">书籍管理</el-menu-item>
        <el-menu-item index="/categories" @click="go('/categories')">分类管理</el-menu-item>
        <el-menu-item index="/users" @click="go('/users')">用户管理</el-menu-item>
        <el-menu-item index="/system" @click="go('/system')">系统设置</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>欢迎，{{ authStore.admin?.nickname || "管理员" }}</span>
        <el-button @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
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
  background: #111827;
  color: #fff;
}
.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  font-weight: 700;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
}
</style>
