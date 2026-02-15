<script setup lang="ts">
// 登录页面：账号登录与跳转
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const loading = reactive({ value: false });
const form = reactive({
  username: "",
  password: ""
});

async function submit() {
  loading.value = true;
  try {
    await authStore.login(form);
    ElMessage.success("登录成功");
    router.push("/");
  } catch {
    // error message handled by request interceptor
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="card">
      <h1 class="title">欢迎回来</h1>
      <p class="subtitle">登录后继续你的沉浸式阅读旅程</p>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <el-button class="w-full" type="primary" :loading="loading.value" @click="submit">登录</el-button>
      <div class="tip">
        没有账号？
        <el-link type="primary" @click="$router.push('/register')">去注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 20px 12px;
}

.card {
  width: min(420px, 100%);
  padding: 8px 4px;
}

.title {
  margin: 2px 0 6px;
  font-size: 28px;
  font-weight: 600;
}

.subtitle {
  margin: 0 0 18px;
  color: var(--text-muted);
}

.w-full {
  width: 100%;
  margin-top: 4px;
}

.tip {
  margin-top: 14px;
  text-align: right;
}
</style>
