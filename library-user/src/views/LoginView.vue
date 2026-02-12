<script setup lang="ts">
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
    // 错误提示由 request 拦截器统一处理，这里吞掉异常避免控制台未处理告警。
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="card">
      <template #header>用户登录</template>
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
