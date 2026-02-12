import axios from "axios";
import { ElMessage } from "element-plus";

// 管理端请求封装：统一鉴权与错误提示
const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000
});

request.interceptors.request.use((config) => {
  // 自动携带管理员令牌
  const token = localStorage.getItem("ebookstore_admin_token");
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
