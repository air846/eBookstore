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
  favorites.value = favoriteRes.data;
  history.value = historyRes.data;
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
    xAxis: {
      type: "category",
      data: preferenceStats.value.map((item) => item.name)
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        type: "bar",
        data: preferenceStats.value.map((item) => item.value),
        itemStyle: {
          color: "#9a7f62"
        },
        barWidth: 34
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
  <div class="page-shell">
    <h1 class="page-title">个人中心</h1>
    <p class="page-subtitle">完善资料与安全设置，查看你的阅读偏好。</p>

    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <el-card>
          <h3 class="panel-title">个人信息</h3>
          <el-form label-width="70px">
            <el-form-item label="用户名">
              <el-input :model-value="authStore.user?.username || ''" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" maxlength="100" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" maxlength="100" />
            </el-form-item>
            <el-form-item label="头像URL">
              <el-input v-model="profileForm.avatar" maxlength="255" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="profileSaving" @click="submitProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="spaced">
          <h3 class="panel-title">账户安全</h3>
          <el-form label-width="88px">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordSaving" @click="submitPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="spaced">
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
                  ｜《{{ item.bookTitle || "未知书籍" }}》
                  <span v-if="item.chapterTitle">· {{ item.chapterTitle }}</span>
                  · 段落 {{ (item.paragraphIndex ?? 0) + 1 }}
                </span>
                <div class="notice-actions">
                  <el-button size="small" text @click="goToNotice(item)">查看</el-button>
                  <el-button size="small" text @click="markRead(item.id)">{{ item.readFlag === 1 ? "已读" : "标为已读" }}</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="spaced">
          <h3 class="panel-title">我的书架</h3>
          <el-empty v-if="favorites.length === 0" description="暂无收藏" />
          <el-tag v-for="item in favorites" :key="item.id" class="tag">{{ item.title }}</el-tag>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="16">
        <el-card>
          <h3 class="panel-title">阅读偏好统计</h3>
          <el-empty v-if="preferenceStats.length === 0" description="暂无阅读数据" />
          <div v-else ref="chartRef" class="chart"></div>
        </el-card>
        <el-card class="spaced">
          <h3 class="panel-title">阅读历史</h3>
          <el-table :data="history">
            <el-table-column prop="bookId" label="书籍ID" width="100" />
            <el-table-column prop="chapter" label="章节" />
            <el-table-column prop="progress" label="进度" width="120" />
            <el-table-column prop="readTime" label="最近阅读时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.panel-title {
  margin: 0 0 14px;
  font-size: 17px;
  font-weight: 600;
}

.chart {
  width: 100%;
  height: 320px;
}

.tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.spaced {
  margin-top: 20px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  gap: 8px;
  flex-shrink: 0;
}
</style>
