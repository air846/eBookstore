import { createRouter, createWebHistory } from "vue-router";
import { useAdminAuthStore } from "../stores/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      component: () => import("../views/LoginView.vue")
    },
    {
      path: "/",
      component: () => import("../layouts/AdminLayout.vue"),
      meta: { requiresAuth: true },
      children: [
        { path: "", component: () => import("../views/DashboardView.vue") },
        { path: "books", component: () => import("../views/BookManageView.vue") },
        { path: "categories", component: () => import("../views/CategoryManageView.vue") },
        { path: "users", component: () => import("../views/UserManageView.vue") },
        { path: "system", component: () => import("../views/SystemSettingView.vue") }
      ]
    }
  ]
});

router.beforeEach((to) => {
  const auth = useAdminAuthStore();
  if (to.meta.requiresAuth && !auth.token) {
    return "/login";
  }
  if (to.path === "/login" && auth.token) {
    return "/";
  }
  return true;
});

export default router;
