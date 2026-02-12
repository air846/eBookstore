import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      component: () => import("../views/LoginView.vue")
    },
    {
      path: "/register",
      component: () => import("../views/RegisterView.vue")
    },
    {
      path: "/",
      component: () => import("../layouts/UserLayout.vue"),
      children: [
        { path: "", component: () => import("../views/HomeView.vue") },
        { path: "books", component: () => import("../views/BookListView.vue") },
        { path: "book/:id", component: () => import("../views/BookDetailView.vue") },
        { path: "reader/:id", component: () => import("../views/ReaderView.vue"), meta: { requiresAuth: true } },
        { path: "profile", component: () => import("../views/ProfileView.vue"), meta: { requiresAuth: true } }
      ]
    }
  ]
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  if (to.meta.requiresAuth && !auth.token) {
    return "/login";
  }
  return true;
});

export default router;
