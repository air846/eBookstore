<script setup lang="ts">
// 系统设置：轮播与站点配置
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "../utils/request";

const list = ref<any[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  imageUrl: "",
  link: "",
  sortOrder: 0,
  status: 1
});

async function loadList() {
  const res = await request.get("/admin/system/carousel/list");
  list.value = res.data;
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, { imageUrl: "", link: "", sortOrder: 0, status: 1 });
  dialogVisible.value = true;
}

function openEdit(row: any) {
  editingId.value = row.id;
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function submit() {
  if (editingId.value) {
    await request.put(`/admin/system/carousel/${editingId.value}`, form);
  } else {
    await request.post("/admin/system/carousel", form);
  }
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  await loadList();
}

async function removeItem(row: any) {
  await ElMessageBox.confirm("确认删除该轮播图吗？", "提示", { type: "warning" });
  await request.delete(`/admin/system/carousel/${row.id}`);
  ElMessage.success("删除成功");
  await loadList();
}

onMounted(loadList);
</script>

<template>
  <div class="page">
    <h1 class="title">系统设置</h1>
    <p class="subtitle">管理首页轮播图，支持排序、跳转链接与启停。</p>

    <el-card>
      <el-button type="primary" @click="openCreate">新增轮播图</el-button>
    </el-card>

    <el-card class="spaced">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="imageUrl" label="图片地址" min-width="260" />
        <el-table-column prop="link" label="跳转链接" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "停用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-space>
              <el-button size="small" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeItem(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑轮播图' : '新增轮播图'" width="520px">
      <el-form label-position="top">
        <el-form-item label="图片地址"><el-input v-model="form.imageUrl" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.link" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
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
</style>
