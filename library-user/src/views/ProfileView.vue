<script setup lang="ts">
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import { onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import request from "../utils/request";

const authStore = useAuthStore();
const router = useRouter();
const chartRef = ref<HTMLElement | null>(null);
let preferenceChart: echarts.ECharts | null = null;

const favorites = ref<any[]>([]);
const history = ref<any[]>([]);
const preferenceStats = ref<Array<{ name: string; value: number }>>([]);
const noticeLoading = ref(false);
const noticeList = ref<any[]>([]);
const noticeQuery = reactive({
  page: 1,
  size: 5
});

const profileSaving = ref(false);
const profileForm = reactive({
  nickname: "",
  email: "",
  avatar: ""
});

const passwordSaving = ref(false);
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

async function loadData() {
  await authStore.fetchUserInfo();
  syncProfileForm();

  const [favoriteRes, historyRes, preferenceRes] = await Promise.all([
    request.get("/book/favorite/list"),
    request.get("/book/history/list"),
    request.get("/book/history/preference")
  ]);
  favorites.value = Array.isArray(favoriteRes.data) ? favoriteRes.data : [];
  history.value = Array.isArray(historyRes.data) ? historyRes.data : [];
  preferenceStats.value = preferenceRes.data || [];

  await loadNotices();
  renderChart();
}

function syncProfileForm() {
  profileForm.nickname = authStore.user?.nickname || "";
  profileForm.email = authStore.user?.email || "";
  profileForm.avatar = authStore.user?.avatar || "";
}

function renderChart() {
  if (!chartRef.value || preferenceStats.value.length === 0) return;
  if (!preferenceChart) {
    preferenceChart = echarts.init(chartRef.value);
  }
  preferenceChart.setOption({
    tooltip: {},
    grid: { left: 26, right: 12, top: 20, bottom: 28 },
    xAxis: {
      type: "category",
      data: preferenceStats.value.map((item) => item.name),
      axisLabel: { fontSize: 11 }
    },
    yAxis: {
      type: "value",
      axisLabel: { fontSize: 11 }
    },
    series: [
      {
        type: "bar",
        data: preferenceStats.value.map((item) => item.value),
        itemStyle: {
          color: "#9a7f62"
        },
        barWidth: 20
      }
    ]
  });
}

onMounted(loadData);

onBeforeUnmount(() => {
  if (preferenceChart) {
    preferenceChart.dispose();
    preferenceChart = null;
  }
});

async function loadNotices() {
  noticeLoading.value = true;
  try {
    const res = await request.get("/book/notice/list", { params: noticeQuery });
    noticeList.value = res.data.records || [];
  } finally {
    noticeLoading.value = false;
  }
}

async function markRead(id: number) {
  await request.post(`/book/notice/read/${id}`);
  await loadNotices();
}

async function markAllRead() {
  await request.post("/book/notice/read/all");
  await loadNotices();
}

function goToNotice(item: any) {
  router.push({
    path: `/reader/${item.bookId}`,
    query: {
      chapterId: item.chapterId,
      paragraphIndex: item.paragraphIndex
    }
  });
}

async function submitProfile() {
  profileSaving.value = true;
  try {
    const res = await request.put("/user/info", profileForm);
    authStore.setUserInfo(res.data);
    ElMessage.success("个人资料已更新");
  } finally {
    profileSaving.value = false;
  }
}

async function submitPassword() {
  passwordSaving.value = true;
  try {
    await request.put("/user/password", passwordForm);
    passwordForm.oldPassword = "";
    passwordForm.newPassword = "";
    passwordForm.confirmPassword = "";
    ElMessage.success("密码修改成功");
  } finally {
    passwordSaving.value = false;
  }
}
</script>

<template>
  <div class="page-shell profile-app">
    <h1 class="page-title">我的</h1>
    <p class="page-subtitle">管理资料、通知与阅读记录</p>

    <el-card>
      <h3 class="panel-title">个人信息</h3>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input :model-value="authStore.user?.username || ''" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" maxlength="100" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" maxlength="100" />
        </el-form-item>
        <el-form-item label="头像 URL">
          <el-input v-model="profileForm.avatar" maxlength="255" />
        </el-form-item>
        <el-button type="primary" :loading="profileSaving" @click="submitProfile">保存资料</el-button>
      </el-form>
    </el-card>

    <el-card>
      <h3 class="panel-title">账户安全</h3>
      <el-form label-position="top">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-button type="primary" :loading="passwordSaving" @click="submitPassword">修改密码</el-button>
      </el-form>
    </el-card>

    <el-card>
      <div class="notice-header">
        <h3 class="panel-title">评论通知</h3>
        <el-button size="small" text @click="markAllRead">全部已读</el-button>
      </div>
      <div v-loading="noticeLoading">
        <el-empty v-if="noticeList.length === 0" description="暂无通知" />
        <div v-else class="notice-list">
          <div v-for="item in noticeList" :key="item.id" class="notice-item">
            <span class="notice-text">
              {{ item.type === 1 ? "有人点赞了你的评论" : "有人点踩了你的评论" }}
              《{{ item.bookTitle || "未知书籍" }}》
            </span>
            <div class="notice-actions">
              <el-button size="small" text @click="goToNotice(item)">查看</el-button>
              <el-button size="small" text @click="markRead(item.id)">{{ item.readFlag === 1 ? "已读" : "标记" }}</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card>
      <h3 class="panel-title">阅读偏好统计</h3>
      <el-empty v-if="preferenceStats.length === 0" description="暂无阅读数据" />
      <div v-else ref="chartRef" class="chart"></div>
    </el-card>

    <el-card>
      <h3 class="panel-title">最近阅读</h3>
      <el-empty v-if="history.length === 0" description="暂无阅读记录" />
      <div v-else class="history-list">
        <div v-for="item in history.slice(0, 8)" :key="`${item.bookId}-${item.readTime}`" class="history-item">
          <p>书籍 {{ item.bookId }} · {{ item.chapter || "未知章节" }}</p>
          <span>{{ item.progress || "0%" }}</span>
        </div>
      </div>
    </el-card>

    <el-card>
      <h3 class="panel-title">我的收藏</h3>
      <el-empty v-if="favorites.length === 0" description="暂无收藏" />
      <div v-else class="favorite-tags">
        <el-tag v-for="item in favorites" :key="item.id" class="tag">{{ item.title }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.profile-app {
  display: grid;
  gap: 12px;
}

.panel-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.chart {
  width: 100%;
  height: 240px;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-list {
  display: grid;
  gap: 8px;
}

.notice-item {
  display: grid;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 10px;
  background: rgba(250, 244, 236, 0.6);
}

.notice-text {
  font-size: 13px;
  color: #5a4d41;
}

.notice-actions {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
}

.history-list {
  display: grid;
  gap: 8px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 10px;
  padding: 8px 10px;
  background: rgba(250, 244, 236, 0.6);
}

.history-item p {
  margin: 0;
  font-size: 13px;
  color: #4f4337;
}

.history-item span {
  color: #7f654c;
  font-size: 12px;
  font-weight: 600;
}

.favorite-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  margin: 0;
}
</style>
