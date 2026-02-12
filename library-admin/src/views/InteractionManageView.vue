<script setup lang="ts">
// 互动管理：段评列表与催更统计
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const activeTab = ref("comments");
const commentLoading = ref(false);
const comments = ref<any[]>([]);
const commentTotal = ref(0);
const commentQuery = reactive({
  page: 1,
  size: 10
});

const urgeLoading = ref(false);
const urgeStats = ref<any[]>([]);

async function loadComments() {
  commentLoading.value = true;
  try {
    const res = await request.get("/admin/comments", {
      params: {
        page: commentQuery.page,
        size: commentQuery.size
      }
    });
    comments.value = res.data.records;
    commentTotal.value = res.data.total;
  } finally {
    commentLoading.value = false;
  }
}

async function deleteComment(id: number) {
  await request.delete(`/admin/comments/${id}`);
  ElMessage.success("已删除");
  await loadComments();
}

async function loadUrgeStats() {
  urgeLoading.value = true;
  try {
    const res = await request.get("/admin/urge/stats");
    urgeStats.value = Array.isArray(res.data) ? res.data : [];
  } finally {
    urgeLoading.value = false;
  }
}

onMounted(async () => {
  await Promise.all([loadComments(), loadUrgeStats()]);
});
</script>

<template>
  <div class="page">
    <h1 class="title">互动管理</h1>
    <p class="subtitle">段评审核与催更统计集中管理。</p>

    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="段评管理" name="comments">
          <div class="toolbar">
            <el-button @click="loadComments">刷新</el-button>
          </div>

          <el-table v-loading="commentLoading" :data="comments">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="bookId" label="书籍ID" width="100" />
            <el-table-column prop="chapterId" label="章节ID" width="100" />
            <el-table-column prop="paragraphIndex" label="段落" width="80" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="content" label="内容" min-width="240" />
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-space>
                  <el-button size="small" type="danger" @click="deleteComment(row.id)">删除</el-button>
                </el-space>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination
              v-model:current-page="commentQuery.page"
              v-model:page-size="commentQuery.size"
              :total="commentTotal"
              layout="total, prev, pager, next"
              @current-change="loadComments"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="催更统计" name="urge">
          <el-table v-loading="urgeLoading" :data="urgeStats">
            <el-table-column prop="bookTitle" label="书名" min-width="200" />
            <el-table-column prop="bookId" label="书籍ID" width="120" />
            <el-table-column prop="count" label="催更次数" width="140" />
            <el-table-column prop="latestTime" label="最近催更时间" min-width="200" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.title {
  margin: 0 0 6px;
  font-size: 28px;
}

.subtitle {
  margin: 0 0 18px;
  color: var(--admin-muted);
}

.toolbar {
  margin-bottom: 12px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
