<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";
import request from "../utils/request";

const route = useRoute();
const fileUrl = ref("");
const setting = reactive({
  fontSize: 18,
  dark: false
});

async function loadReadInfo() {
  const res = await request.get(`/book/read/${route.params.id}`);
  fileUrl.value = res.data;
  await request.post("/book/history", {
    bookId: Number(route.params.id),
    chapter: "第1章",
    progress: "0%"
  });
}

onMounted(loadReadInfo);
</script>

<template>
  <div :class="['reader', { dark: setting.dark }]">
    <div class="toolbar">
      <el-space>
        <span>字号</span>
        <el-slider v-model="setting.fontSize" :min="14" :max="28" style="width: 200px" />
        <el-switch v-model="setting.dark" inline-prompt active-text="夜间" inactive-text="白天" />
      </el-space>
    </div>
    <div class="content" :style="{ fontSize: `${setting.fontSize}px` }">
      <p>当前为阅读器占位内容，后续可接入 epub.js 或 pdf.js。</p>
      <p>电子书资源地址：{{ fileUrl }}</p>
    </div>
  </div>
</template>

<style scoped>
.reader {
  min-height: calc(100vh - 120px);
  background: #ffffff;
  border-radius: 8px;
}
.reader.dark {
  background: #111827;
  color: #f9fafb;
}
.toolbar {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
}
.content {
  padding: 24px;
  line-height: 1.9;
}
</style>
