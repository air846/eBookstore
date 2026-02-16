import axios from "axios";
import { ElMessage } from "element-plus";

// 用户端请求封装：统一 baseURL、超时、鉴权与错误提示
const apiServer = import.meta.env.VITE_API_SERVER || "http://localhost";
const apiPort = import.meta.env.VITE_API_PORT || "8080";
const apiPrefix = import.meta.env.VITE_API_PREFIX || "/api";
const normalizedApiPrefix = apiPrefix.startsWith("/") ? apiPrefix : `/${apiPrefix}`;
const apiBaseUrl =
  import.meta.env.VITE_API_BASE_URL || `${apiServer}:${apiPort}${normalizedApiPrefix}`;

const request = axios.create({
  baseURL: apiBaseUrl,
  timeout: 60000
});

request.interceptors.request.use((config) => {
  // 自动注入登录令牌
  const token = localStorage.getItem("ebookstore_user_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    // 统一处理业务码
    const payload = response.data;
    if (payload.code !== 200) {
      ElMessage.error(payload.message || "请求失败");
      return Promise.reject(payload);
    }
    return payload;
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || error.message || "网络异常");
    return Promise.reject(error);
  }
);

export default request;
