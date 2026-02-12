<script setup lang="ts">
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
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="card">
      <template #header>用户注册</template>
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
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #f3f4f6;
}
.card {
  width: 420px;
}
.w-full {
  width: 100%;
}
.tip {
  margin-top: 12px;
  text-align: right;
}
</style>
