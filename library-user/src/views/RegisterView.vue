<script setup lang="ts">
// 注册页面：表单校验与账号创建
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const router = useRouter();
const loading = reactive({ value: false });
const form = reactive({
  username: "",
  nickname: "",
  email: "",
  password: ""
});

async function submit() {
  loading.value = true;
  try {
    await request.post("/user/register", form);
    ElMessage.success("注册成功，请登录");
    router.push("/login");
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
      <h1 class="title">创建账号</h1>
      <p class="subtitle">只需一步，开始你的优雅阅读体验</p>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <el-button class="w-full" type="primary" :loading="loading.value" @click="submit">注册</el-button>
      <div class="tip">
        已有账号？
        <el-link type="primary" @click="$router.push('/login')">去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 160px);
  display: grid;
  place-items: center;
  padding: 20px 12px 30px;
}

.card {
  width: min(460px, 100%);
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
