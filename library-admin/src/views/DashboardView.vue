<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import axios from "axios";
import request from "../utils/request";

interface BookRow {
  id: number;
  title: string;
  author?: string;
  categoryName?: string;
  categoryId?: number;
  status?: number;
  coverUrl?: string;
}

const router = useRouter();
const loading = ref(false);
const stats = ref<any>({});
const latestBooks = ref<BookRow[]>([]);
const serverLoadInfo = ref<any>(null);

function extractLoad(value: any): number | null {
  if (value === undefined || value === null || value === "") return null;
  const n = Number(value);
  if (Number.isNaN(n)) return null;
  return Math.max(0, Math.min(100, n));
}

const statCards = computed(() => {
  const totalUsers = Number(stats.value.totalUsers || 0);
  const todayVisits = Number(stats.value.todayVisits || 0);
  const totalBooks = Number(stats.value.totalBooks || 0);
  const dashboardLoad = extractLoad(stats.value.serverLoad);
  const systemLoad =
    extractLoad(serverLoadInfo.value?.serverLoad) ??
    extractLoad(serverLoadInfo.value?.cpuUsage) ??
    extractLoad(serverLoadInfo.value?.load) ??
    extractLoad(serverLoadInfo.value?.value);
  const finalLoad = dashboardLoad ?? systemLoad;
  const serverLoadValue = finalLoad === null ? "--" : `${finalLoad}%`;
  const loadSource = dashboardLoad !== null ? "仪表盘接口" : systemLoad !== null ? "系统设置接口" : "未接入";
  return [
    { label: "总注册用户", value: totalUsers.toLocaleString(), extra: "+12.5%" },
    { label: "今日阅读活跃", value: todayVisits.toLocaleString(), extra: "实时" },
    { label: "库存书籍总数", value: totalBooks.toLocaleString(), extra: "在库" },
    { label: "服务器负载", value: serverLoadValue, extra: loadSource }
  ];
});

const tableSkeletonRows = Array.from({ length: 5 }, (_, idx) => idx);

async function loadData() {
  loading.value = true;
  try {
    const [dashboardRes, latestRes, serverLoadRes] = await Promise.all([
      request.get("/admin/dashboard"),
      request.get("/admin/book/list", {
        params: { page: 1, size: 6, sortBy: "create_time", order: "desc" }
      }),
      request.get("/admin/system/server-load").catch(() => ({ data: null }))
    ]);
    stats.value = dashboardRes.data || {};
    latestBooks.value = Array.isArray(latestRes.data?.records) ? latestRes.data.records : [];
    serverLoadInfo.value = serverLoadRes?.data || null;
  } finally {
    loading.value = false;
  }
}

function goBooks() {
  router.push("/books");
}

async function exportReport() {
  const token = localStorage.getItem("ebookstore_admin_token");
  const baseURL = request.defaults.baseURL || "http://localhost:8080/api";
  try {
    const response = await axios.get(`${baseURL}/admin/dashboard/export`, {
      responseType: "blob",
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });

    const contentType = String(response.headers["content-type"] || "");
    if (contentType.includes("application/json")) {
      const text = await response.data.text();
      let message = "导出失败";
      try {
        const json = JSON.parse(text);
        message = json.message || message;
      } catch {
        // ignore JSON parse error
      }
      throw new Error(message);
    }

    const blob = response.data as Blob;
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `电子书店数据报表_${Date.now()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    ElMessage.success("报表导出成功");
  } catch (error: any) {
    const raw = `${error?.message || ""} ${error?.toString?.() || ""}`;
    if (raw.includes("ERR_BLOCKED_BY_CLIENT")) {
      ElMessage.error("导出请求被浏览器插件拦截，请关闭广告拦截插件或将 localhost 加入白名单");
      return;
    }
    const msg = error?.message || error?.response?.data?.message || "导出失败，请稍后重试";
    ElMessage.error(msg);
  }
}

function formatCategory(row: BookRow) {
  if (row.categoryName) return row.categoryName;
  if (row.categoryId) return `分类 #${row.categoryId}`;
  return "未分类";
}

onMounted(loadData);
</script>

<template>
  <div class="dashboard-page">
    <header class="top-card">
      <h2>控制台</h2>
      <div class="actions">
        <el-button @click="exportReport">导出报表</el-button>
        <el-button type="primary" @click="goBooks">新增书籍</el-button>
      </div>
    </header>

    <section class="stats-grid">
      <template v-if="loading">
        <article v-for="n in 4" :key="n" class="stat-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 90px; height: 14px" />
              <el-skeleton-item variant="text" style="margin-top: 12px; width: 120px; height: 36px" />
            </template>
          </el-skeleton>
        </article>
      </template>
      <article v-else v-for="item in statCards" :key="item.label" class="stat-card">
        <p class="label">{{ item.label }}</p>
        <div class="row">
          <h3>{{ item.value }}</h3>
          <span>{{ item.extra }}</span>
        </div>
      </article>
    </section>

    <section class="table-panel">
      <div class="panel-head">
        <h4>最新新增书籍</h4>
        <el-button text @click="goBooks">查看全部记录</el-button>
      </div>

      <div v-if="loading" class="table-skeleton">
        <div v-for="row in tableSkeletonRows" :key="row" class="table-skeleton-row">
          <el-skeleton animated>
            <template #template>
              <div class="table-skeleton-cell">
                <el-skeleton-item variant="image" style="width: 32px; height: 42px; border-radius: 6px" />
                <el-skeleton-item variant="text" style="width: 120px; margin-left: 10px" />
              </div>
              <el-skeleton-item variant="text" style="width: 80px" />
              <el-skeleton-item variant="text" style="width: 96px" />
              <el-skeleton-item variant="text" style="width: 72px" />
            </template>
          </el-skeleton>
        </div>
      </div>

      <el-empty v-else-if="!latestBooks.length" description="暂无新增数据" />

      <el-table v-else :data="latestBooks" stripe>
        <el-table-column label="书籍名称" min-width="220">
          <template #default="scope">
            <div class="book-cell">
              <img v-if="scope.row.coverUrl" :src="scope.row.coverUrl" alt="cover" />
              <div v-else class="placeholder">书</div>
              <span>{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="120" />
        <el-table-column label="分类" min-width="120">
          <template #default="scope">
            <el-tag effect="plain">{{ formatCategory(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? "已上架" : "已下架" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" text @click="router.push('/books')">编辑</el-button>
            <el-button size="small" text type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 18px;
}

.top-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px;
  border-radius: 16px;
  border: 1px solid var(--admin-line);
  background: #fff;
  box-shadow: var(--admin-shadow);
}

.top-card h2 {
  margin: 0;
  font-size: 24px;
}

.actions {
  display: flex;
  gap: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  padding: 18px;
  border-radius: 14px;
  border: 1px solid var(--admin-line);
  background: rgba(255, 255, 255, 0.9);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 30px rgba(85, 63, 43, 0.1);
}

.label {
  margin: 0 0 8px;
  color: var(--admin-muted);
  font-size: 13px;
}

.row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.row h3 {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
}

.row span {
  color: #56a66f;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
}

.table-panel {
  padding: 14px;
  border-radius: 16px;
  border: 1px solid var(--admin-line);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: var(--admin-shadow);
  transition: box-shadow 0.2s ease;
}

.table-panel:hover {
  box-shadow: 0 18px 34px rgba(85, 63, 43, 0.12);
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 6px 8px;
}

.panel-head h4 {
  margin: 0;
  font-size: 18px;
}

.book-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.book-cell img,
.placeholder {
  width: 32px;
  height: 42px;
  border-radius: 6px;
}

.book-cell img {
  object-fit: cover;
}

.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ece6df;
  color: #8b745d;
}

.table-skeleton {
  display: grid;
  gap: 10px;
  padding: 8px;
}

.table-skeleton-row {
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(249, 245, 240, 0.75);
}

.table-skeleton-cell {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .top-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
