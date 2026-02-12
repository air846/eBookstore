<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();

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
    <el-header class="header">
      <div class="logo" @click="to('/')">书城阅读</div>
      <div class="nav">
        <el-button text @click="to('/books')">图书</el-button>
        <el-button text @click="to('/profile')">个人中心</el-button>
        <el-button v-if="!authStore.isLogin" type="primary" @click="to('/login')">登录</el-button>
        <el-button v-else @click="logout">退出</el-button>
      </div>
    </el-header>

    <el-main class="main">
      <router-view />
    </el-main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  height: 60px;
  background: #1f2937;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}
.logo {
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
}
.nav {
  display: flex;
  gap: 8px;
  align-items: center;
}
.main {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>
