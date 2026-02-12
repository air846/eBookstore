import { defineStore } from "pinia";
import { computed, ref } from "vue";
import request from "../utils/request";

interface LoginForm {
  username: string;
  password: string;
}

interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  role: number;
  avatar?: string;
}

const TOKEN_KEY = "ebookstore_user_token";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || "");
  const user = ref<UserInfo | null>(null);
  const isLogin = computed(() => !!token.value);

  function setToken(value: string) {
    token.value = value;
    if (value) {
      localStorage.setItem(TOKEN_KEY, value);
    } else {
      localStorage.removeItem(TOKEN_KEY);
    }
  }

  async function login(form: LoginForm) {
    const res = await request.post("/user/login", form);
    setToken(res.data.token);
    user.value = res.data;
  }

  async function fetchUserInfo() {
    if (!token.value) return;
    const res = await request.get("/user/info");
    user.value = res.data;
  }

  function logout() {
    user.value = null;
    setToken("");
  }

  return { token, user, isLogin, login, fetchUserInfo, logout };
});
