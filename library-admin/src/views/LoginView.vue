<script setup lang="ts">
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
    // 错误提示由 request 拦截器统一处理，这里避免未处理 Promise 异常。
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="card">
      <template #header>后台登录</template>
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
  background: #f3f4f6;
}
.card {
  width: 420px;
}
.full {
  width: 100%;
}
</style>
