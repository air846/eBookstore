<script setup lang="ts">
// 分类管理：名称维护与层级显示
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "../utils/request";

interface CategoryNode {
  id: number;
  name: string;
  parentId: number;
  sortOrder: number;
  children?: CategoryNode[];
}

interface CategoryOption {
  id: number;
  name: string;
  parentId: number;
  level: number;
}

const categories = ref<CategoryNode[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  name: "",
  parentId: 0,
  sortOrder: 0
});

const categoryOptions = computed(() => flattenCategoryTree(categories.value));
const categoryNameMap = computed(() => {
  const map = new Map<number, string>();
  categoryOptions.value.forEach((item) => map.set(item.id, item.name));
  return map;
});

function flattenCategoryTree(nodes: CategoryNode[], level = 0): CategoryOption[] {
  return nodes.flatMap((item) => {
    const current: CategoryOption = {
      id: item.id,
      name: item.name,
      parentId: item.parentId,
      level
    };
    const children = Array.isArray(item.children) ? flattenCategoryTree(item.children, level + 1) : [];
    return [current, ...children];
  });
}

function formatParentName(parentId: number) {
  if (!parentId) return "顶级分类";
  return categoryNameMap.value.get(parentId) || `ID:${parentId}`;
}

async function loadCategories() {
  const res = await request.get("/admin/category/tree");
  categories.value = Array.isArray(res.data) ? res.data : [];
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, { name: "", parentId: 0, sortOrder: 0 });
  dialogVisible.value = true;
}

function openEdit(row: CategoryNode) {
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

async function removeCategory(row: CategoryNode) {
  await ElMessageBox.confirm(`确认删除分类「${row.name}」吗？`, "提示", { type: "warning" });
  await request.delete(`/admin/category/${row.id}`);
  ElMessage.success("删除成功");
  await loadCategories();
}

onMounted(loadCategories);
</script>

<template>
  <div class="page">
    <h1 class="title">分类管理</h1>
    <p class="subtitle">父级分类改为按名称选择，不再手动记数字 ID。</p>

    <el-card>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </el-card>

    <el-card class="spaced">
      <el-table :data="categories" row-key="id" default-expand-all>
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="父级分类" min-width="180">
          <template #default="{ row }">
            {{ formatParentName(row.parentId) }}
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="520px">
      <el-form label-position="top">
        <el-form-item label="分类名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父级分类">
          <el-select v-model="form.parentId" style="width: 100%" filterable>
            <el-option :value="0" label="顶级分类" />
            <el-option
              v-for="option in categoryOptions"
              :key="option.id"
              :label="`${option.level > 0 ? `${'　'.repeat(option.level)}└ ` : ''}${option.name}`"
              :value="option.id"
              :disabled="editingId === option.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
