<script setup lang="ts">
import * as echarts from "echarts";
import { onMounted, ref } from "vue";
import request from "../utils/request";
import { useAuthStore } from "../stores/auth";

const authStore = useAuthStore();
const chartRef = ref<HTMLElement | null>(null);
const favorites = ref<any[]>([]);
const history = ref<any[]>([]);

async function loadData() {
  await authStore.fetchUserInfo();
  const [favoriteRes, historyRes] = await Promise.all([
    request.get("/book/favorite/list"),
    request.get("/book/history/list")
  ]);
  favorites.value = favoriteRes.data;
  history.value = historyRes.data;
  renderChart();
}

function renderChart() {
  if (!chartRef.value) return;
  const chart = echarts.init(chartRef.value);
  chart.setOption({
    tooltip: {},
    xAxis: {
      type: "category",
      data: ["文学", "科幻", "技术", "其他"]
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        type: "bar",
        data: [4, 6, 3, 2],
        itemStyle: {
          color: "#409eff"
        }
      }
    ]
  });
}

onMounted(loadData);
</script>

<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <el-card>
        <template #header>个人信息</template>
        <p>用户名：{{ authStore.user?.username }}</p>
        <p>昵称：{{ authStore.user?.nickname }}</p>
      </el-card>
      <el-card style="margin-top: 12px">
        <template #header>我的书架</template>
        <el-empty v-if="favorites.length === 0" description="暂无收藏" />
        <el-tag v-for="item in favorites" :key="item.id" class="tag">{{ item.title }}</el-tag>
      </el-card>
    </el-col>
    <el-col :span="16">
      <el-card>
        <template #header>阅读偏好统计</template>
        <div ref="chartRef" class="chart"></div>
      </el-card>
      <el-card style="margin-top: 12px">
        <template #header>阅读历史</template>
        <el-table :data="history">
          <el-table-column prop="bookId" label="书籍ID" width="100" />
          <el-table-column prop="chapter" label="章节" />
          <el-table-column prop="progress" label="进度" width="120" />
          <el-table-column prop="readTime" label="最近阅读时间" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.chart {
  width: 100%;
  height: 320px;
}
.tag {
  margin-right: 8px;
  margin-bottom: 8px;
}
</style>
