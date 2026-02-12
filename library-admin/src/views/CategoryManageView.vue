<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const categories = ref<any[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  name: "",
  parentId: 0,
  sortOrder: 0
});

async function loadCategories() {
  const res = await request.get("/admin/category/tree");
  categories.value = res.data;
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, { name: "", parentId: 0, sortOrder: 0 });
  dialogVisible.value = true;
}

function openEdit(row: any) {
  editingId.value = row.id;
  Object.assign(form, { name: row.name, parentId: row.parentId, sortOrder: row.sortOrder });
  dialogVisible.value = true;
}

async function submit() {
  if (editingId.value) {
    await request.put(`/admin/category/${editingId.value}`, form);
  } else {
    await request.post("/admin/category", form);
  }
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  await loadCategories();
}

async function removeCategory(row: any) {
  await request.delete(`/admin/category/${row.id}`);
  ElMessage.success("已删除");
  await loadCategories();
}

onMounted(loadCategories);
</script>

<template>
  <el-card>
    <el-button type="primary" @click="openCreate">新增分类</el-button>
  </el-card>

  <el-card style="margin-top: 12px">
    <el-table :data="categories" row-key="id" default-expand-all>
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-space>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeCategory(row)">删除</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="460px">
    <el-form label-position="top">
      <el-form-item label="分类名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="父级ID"><el-input-number v-model="form.parentId" :min="0" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>
