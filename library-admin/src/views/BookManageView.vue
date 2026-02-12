<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from "../utils/request";

const loading = ref(false);
const books = ref<any[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const query = reactive({
  page: 1,
  size: 10,
  keyword: ""
});
const form = reactive({
  title: "",
  author: "",
  publisher: "",
  isbn: "",
  categoryId: 1,
  coverUrl: "",
  description: "",
  fileUrl: "",
  fileType: "PDF",
  status: 1
});

async function loadBooks() {
  loading.value = true;
  try {
    const res = await request.get("/admin/book/list", { params: query });
    books.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, {
    title: "",
    author: "",
    publisher: "",
    isbn: "",
    categoryId: 1,
    coverUrl: "",
    description: "",
    fileUrl: "",
    fileType: "PDF",
    status: 1
  });
  dialogVisible.value = true;
}

function openEdit(row: any) {
  editingId.value = row.id;
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function submit() {
  if (editingId.value) {
    await request.put(`/admin/book/${editingId.value}`, form);
  } else {
    await request.post("/admin/book", form);
  }
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  await loadBooks();
}

async function toggleStatus(row: any) {
  const next = row.status === 1 ? 0 : 1;
  await request.put(`/admin/book/${row.id}/status`, null, { params: { status: next } });
  ElMessage.success("状态已更新");
  await loadBooks();
}

async function removeBook(row: any) {
  await request.delete(`/admin/book/${row.id}`);
  ElMessage.success("已删除");
  await loadBooks();
}

onMounted(loadBooks);
</script>

<template>
  <el-card>
    <el-space>
      <el-input v-model="query.keyword" placeholder="搜索书名或作者" clearable />
      <el-button type="primary" @click="query.page = 1; loadBooks()">查询</el-button>
      <el-button @click="openCreate">新增书籍</el-button>
    </el-space>
  </el-card>

  <el-card style="margin-top: 12px">
    <el-table v-loading="loading" :data="books">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="150" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="fileType" label="文件类型" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "上架" : "下架" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-space>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="toggleStatus(row)">{{ row.status === 1 ? "下架" : "上架" }}</el-button>
            <el-button size="small" type="danger" @click="removeBook(row)">删除</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadBooks"
      />
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="editingId ? '编辑书籍' : '新增书籍'" width="720px">
    <el-form label-position="top">
      <el-row :gutter="12">
        <el-col :span="12"><el-form-item label="书名"><el-input v-model="form.title" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="作者"><el-input v-model="form.author" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="1" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="文件类型"><el-select v-model="form.fileType"><el-option value="PDF" /><el-option value="EPUB" /><el-option value="TXT" /></el-select></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="封面地址"><el-input v-model="form.coverUrl" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="文件地址"><el-input v-model="form.fileUrl" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="简介"><el-input v-model="form.description" type="textarea" rows="4" /></el-form-item></el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
