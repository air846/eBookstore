<script setup lang="ts">
// 管理端登录页
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAdminAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAdminAuthStore();
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
  <div class="login-page">
    <el-card class="card">
      <h1 class="title">后台登录</h1>
      <p class="subtitle">优雅管理你的书城内容与用户数据</p>
      <el-form label-position="top">
        <el-form-item label="管理员账号">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <el-button type="primary" class="full" :loading="loading.value" @click="submit">登录</el-button>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 20px 12px;
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
  color: var(--admin-muted);
}

.full {
  width: 100%;
}
</style>
