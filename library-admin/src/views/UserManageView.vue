<script setup lang="ts">
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
  <el-card>
    <el-space>
      <el-input v-model="query.keyword" placeholder="搜索用户名或昵称" clearable />
      <el-button type="primary" @click="query.page = 1; loadUsers()">查询</el-button>
    </el-space>
  </el-card>
  <el-card style="margin-top: 12px">
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
</template>

<style scoped>
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
