<script setup lang="ts">
// 数据概览：仪表盘统计
import * as echarts from "echarts";
import { onMounted, ref } from "vue";
import request from "../utils/request";

const stats = ref<any>({});
const hotChartRef = ref<HTMLElement | null>(null);
const countChartRef = ref<HTMLElement | null>(null);

async function loadData() {
  const res = await request.get("/admin/dashboard");
  stats.value = res.data;
  renderCharts();
}

function renderCharts() {
  if (hotChartRef.value) {
    const hotChart = echarts.init(hotChartRef.value);
    hotChart.setOption({
      tooltip: {},
      grid: { left: 34, right: 14, top: 22, bottom: 34 },
      xAxis: {
        type: "category",
        data: (stats.value.hotBooks || []).map((item: any) => item.title),
        axisLabel: { color: "#7f7265" }
      },
      yAxis: { type: "value", axisLabel: { color: "#7f7265" } },
      series: [
        {
          type: "bar",
          data: (stats.value.hotBooks || []).map((item: any) => item.visitCount || 0),
          itemStyle: { color: "#9a7f62", borderRadius: [4, 4, 0, 0] },
          barWidth: 30
        }
      ]
    });
  }

  if (countChartRef.value) {
    const countChart = echarts.init(countChartRef.value);
    countChart.setOption({
      tooltip: {},
      series: [
        {
          type: "pie",
          radius: ["44%", "72%"],
          label: { color: "#6f5f51" },
          data: [
            { name: "用户数", value: stats.value.totalUsers || 0 },
            { name: "书籍数", value: stats.value.totalBooks || 0 },
            { name: "今日访问", value: stats.value.todayVisits || 0 }
          ],
          color: ["#9a7f62", "#c0a487", "#dfc9b0"]
        }
      ]
    });
  }
}

onMounted(loadData);
</script>

<template>
  <div class="dashboard">
    <h1 class="title">数据概览</h1>
    <p class="subtitle">掌握书城核心运营指标，快速了解访问与内容表现。</p>

    <el-row :gutter="18">
      <el-col :xs="24" :md="8">
        <el-card class="stat-card"><h3>总用户数</h3><p>{{ stats.totalUsers || 0 }}</p></el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="stat-card"><h3>总书籍数</h3><p>{{ stats.totalBooks || 0 }}</p></el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="stat-card"><h3>今日访问量</h3><p>{{ stats.todayVisits || 0 }}</p></el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <h3 class="panel-title">热门书籍排行</h3>
          <div ref="hotChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <h3 class="panel-title">系统总览</h3>
          <div ref="countChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.title {
  margin: 0 0 6px;
  font-size: 28px;
  line-height: 1.25;
}

.subtitle {
  margin: 0 0 20px;
  color: var(--admin-muted);
}

.stat-card h3 {
  margin: 0 0 10px;
  color: #736252;
  font-size: 15px;
  font-weight: 500;
}

.stat-card p {
  margin: 0;
  font-size: 36px;
  line-height: 1.2;
  font-weight: 600;
  color: #3f3227;
}

.chart-row {
  margin-top: 18px;
}

.panel-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 600;
}

.chart {
  height: 330px;
}
</style>
