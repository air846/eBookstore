import { defineStore } from "pinia";
import { computed, ref } from "vue";
import request from "../utils/request";

const TOKEN_KEY = "ebookstore_admin_token";

export const useAdminAuthStore = defineStore("admin-auth", () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || "");
  const admin = ref<any>(null);
  const isLogin = computed(() => !!token.value);

  async function login(form: { username: string; password: string }) {
    const res = await request.post("/admin/login", form);
    token.value = res.data.token;
    admin.value = res.data;
    localStorage.setItem(TOKEN_KEY, token.value);
  }

  function logout() {
    token.value = "";
    admin.value = null;
    localStorage.removeItem(TOKEN_KEY);
  }

  return { token, admin, isLogin, login, logout };
});
