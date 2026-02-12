<script setup lang="ts">
// 用户管理：列表、状态更新与搜索
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const users = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const query = reactive({
  page: 1,
  size: 10,
  keyword: ""
});

async function loadUsers() {
  loading.value = true;
  try {
    const res = await request.get("/admin/user/list", { params: query });
    users.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

async function toggleStatus(row: any) {
  const next = row.status === 1 ? 0 : 1;
  await request.put(`/admin/user/${row.id}/status`, { status: next });
  ElMessage.success("状态已更新");
  await loadUsers();
}

onMounted(loadUsers);
</script>

<template>
  <div class="page">
    <h1 class="title">用户管理</h1>
    <p class="subtitle">查看用户账号状态，按需启用或禁用账号。</p>

    <el-card>
      <el-space wrap>
        <el-input v-model="query.keyword" placeholder="搜索用户名或昵称" clearable style="width: 280px" />
        <el-button type="primary" @click="query.page = 1; loadUsers()">查询</el-button>
      </el-space>
    </el-card>

    <el-card class="spaced">
      <el-table v-loading="loading" :data="users">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="nickname" label="昵称" width="180" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? "正常" : "禁用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="toggleStatus(row)">{{ row.status === 1 ? "禁用" : "启用" }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadUsers"
        />
      </div>
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

.spaced {
  margin-top: 14px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
