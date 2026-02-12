<script setup lang="ts">
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
      xAxis: {
        type: "category",
        data: (stats.value.hotBooks || []).map((item: any) => item.title)
      },
      yAxis: { type: "value" },
      series: [{ type: "bar", data: (stats.value.hotBooks || []).map((item: any) => item.visitCount || 0) }]
    });
  }

  if (countChartRef.value) {
    const countChart = echarts.init(countChartRef.value);
    countChart.setOption({
      tooltip: {},
      series: [
        {
          type: "pie",
          radius: "62%",
          data: [
            { name: "用户数", value: stats.value.totalUsers || 0 },
            { name: "书籍数", value: stats.value.totalBooks || 0 },
            { name: "今日访问", value: stats.value.todayVisits || 0 }
          ]
        }
      ]
    });
  }
}

onMounted(loadData);
</script>

<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <el-card><h3>总用户数</h3><p>{{ stats.totalUsers || 0 }}</p></el-card>
    </el-col>
    <el-col :span="8">
      <el-card><h3>总书籍数</h3><p>{{ stats.totalBooks || 0 }}</p></el-card>
    </el-col>
    <el-col :span="8">
      <el-card><h3>今日访问量</h3><p>{{ stats.todayVisits || 0 }}</p></el-card>
    </el-col>
  </el-row>
  <el-row :gutter="16" style="margin-top: 16px">
    <el-col :span="12">
      <el-card>
        <template #header>热门书籍排行</template>
        <div ref="hotChartRef" class="chart"></div>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <template #header>系统总览</template>
        <div ref="countChartRef" class="chart"></div>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.chart {
  height: 320px;
}
</style>
