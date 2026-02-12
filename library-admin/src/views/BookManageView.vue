<script setup lang="ts">
// 书籍管理：新增/编辑/上架与章节维护入口
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "../utils/request";

interface BookRow {
  id: number;
  title: string;
  author: string;
  publisher?: string;
  isbn?: string;
  categoryId: number;
  coverUrl?: string;
  description?: string;
  fileUrl: string;
  fileType: string;
  status: number;
}

interface ChapterRow {
  id: number;
  bookId: number;
  title: string;
  content: string;
  sortOrder: number;
  createTime?: string;
  updateTime?: string;
}

interface CategoryNode {
  id: number;
  name: string;
  parentId: number;
  children?: CategoryNode[];
}

interface CategoryOption {
  id: number;
  name: string;
  level: number;
}

const loading = ref(false);
const books = ref<BookRow[]>([]);
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
  categoryId: 0,
  coverUrl: "",
  description: "",
  fileUrl: "",
  fileType: "PDF",
  status: 0
});

const chapterDialogVisible = ref(false);
const chapterFormDialogVisible = ref(false);
const chapterLoading = ref(false);
const chapterBookId = ref<number | null>(null);
const chapterBookTitle = ref("");
const chapterEditingId = ref<number | null>(null);
const chapters = ref<ChapterRow[]>([]);
const chapterForm = reactive({
  title: "",
  content: "",
  sortOrder: 1
});

const categoryTree = ref<CategoryNode[]>([]);
const categoryOptions = computed(() => flattenCategoryTree(categoryTree.value));
const categoryNameMap = computed(() => {
  const map = new Map<number, string>();
  categoryOptions.value.forEach((item) => map.set(item.id, item.name));
  return map;
});

function flattenCategoryTree(nodes: CategoryNode[], level = 0): CategoryOption[] {
  return nodes.flatMap((item) => {
    const current: CategoryOption = { id: item.id, name: item.name, level };
    const children = Array.isArray(item.children) ? flattenCategoryTree(item.children, level + 1) : [];
    return [current, ...children];
  });
}

function defaultCategoryId() {
  return categoryOptions.value[0]?.id ?? 0;
}

function getCategoryName(categoryId: number) {
  return categoryNameMap.value.get(categoryId) ?? `ID:${categoryId}`;
}

async function loadCategories() {
  const res = await request.get("/admin/category/tree");
  categoryTree.value = Array.isArray(res.data) ? res.data : [];
  if (!editingId.value && form.categoryId === 0) {
    form.categoryId = defaultCategoryId();
  }
}

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
    categoryId: defaultCategoryId(),
    coverUrl: "",
    description: "",
    fileUrl: "",
    fileType: "PDF",
    status: 0
  });
  dialogVisible.value = true;
}

function openEdit(row: BookRow) {
  editingId.value = row.id;
  Object.assign(form, {
    title: row.title,
    author: row.author,
    publisher: row.publisher || "",
    isbn: row.isbn || "",
    categoryId: row.categoryId || defaultCategoryId(),
    coverUrl: row.coverUrl || "",
    description: row.description || "",
    fileUrl: row.fileUrl || "",
    fileType: row.fileType || "PDF",
    status: row.status ?? 0
  });
  dialogVisible.value = true;
}

async function submit() {
  if (!form.categoryId) {
    ElMessage.warning("请选择分类");
    return;
  }
  if (editingId.value) {
    await request.put(`/admin/book/${editingId.value}`, form);
  } else {
    await request.post("/admin/book", form);
  }
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  await loadBooks();
}

async function toggleStatus(row: BookRow) {
  const next = row.status === 1 ? 0 : 1;
  await request.put(`/admin/book/${row.id}/status`, null, { params: { status: next } });
  ElMessage.success("状态已更新");
  await loadBooks();
}

async function removeBook(row: BookRow) {
  await ElMessageBox.confirm(`确认删除《${row.title}》吗？`, "提示", { type: "warning" });
  await request.delete(`/admin/book/${row.id}`);
  ElMessage.success("删除成功");
  await loadBooks();
}

function resetChapterForm() {
  Object.assign(chapterForm, {
    title: "",
    content: "",
    sortOrder: chapters.value.length + 1
  });
}

async function loadChapters() {
  if (!chapterBookId.value) return;
  chapterLoading.value = true;
  try {
    const res = await request.get(`/admin/book/${chapterBookId.value}/chapters`);
    chapters.value = res.data;
  } finally {
    chapterLoading.value = false;
  }
}

async function openChapterManage(row: BookRow) {
  chapterBookId.value = row.id;
  chapterBookTitle.value = row.title;
  chapterDialogVisible.value = true;
  chapterFormDialogVisible.value = false;
  await loadChapters();
}

function openCreateChapter() {
  chapterEditingId.value = null;
  resetChapterForm();
  chapterFormDialogVisible.value = true;
}

function openEditChapter(row: ChapterRow) {
  chapterEditingId.value = row.id;
  Object.assign(chapterForm, {
    title: row.title,
    content: row.content,
    sortOrder: row.sortOrder
  });
  chapterFormDialogVisible.value = true;
}

async function submitChapter() {
  if (!chapterBookId.value) return;
  const payload = {
    title: chapterForm.title,
    content: chapterForm.content,
    sortOrder: chapterForm.sortOrder
  };
  if (chapterEditingId.value) {
    await request.put(`/admin/book/${chapterBookId.value}/chapters/${chapterEditingId.value}`, payload);
  } else {
    await request.post(`/admin/book/${chapterBookId.value}/chapters`, payload);
  }
  ElMessage.success("章节保存成功");
  chapterFormDialogVisible.value = false;
  await loadChapters();
}

async function removeChapter(row: ChapterRow) {
  if (!chapterBookId.value) return;
  await ElMessageBox.confirm(`确认删除章节「${row.title}」吗？`, "提示", { type: "warning" });
  await request.delete(`/admin/book/${chapterBookId.value}/chapters/${row.id}`);
  ElMessage.success("章节已删除");
  await loadChapters();
}

function closeChapterDialog() {
  chapterBookId.value = null;
  chapterBookTitle.value = "";
  chapters.value = [];
  chapterEditingId.value = null;
  chapterFormDialogVisible.value = false;
}

function previewContent(content: string) {
  if (!content) return "";
  return content.length > 80 ? `${content.slice(0, 80)}...` : content;
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadBooks()]);
});
</script>

<template>
  <div class="page">
    <h1 class="title">书籍管理</h1>
    <p class="subtitle">分类支持按名称选择，不再需要记分类数字 ID。</p>

    <el-card>
      <el-space wrap>
        <el-input v-model="query.keyword" placeholder="搜索书名或作者" clearable style="width: 280px" />
        <el-button type="primary" @click="query.page = 1; loadBooks()">查询</el-button>
        <el-button @click="openCreate">新增书籍</el-button>
      </el-space>
    </el-card>

    <el-card class="spaced">
      <el-table v-loading="loading" :data="books">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" width="140" />
        <el-table-column label="分类" min-width="120">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="文件类型" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "上架" : "下架" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350">
          <template #default="{ row }">
            <el-space>
              <el-button size="small" type="primary" plain @click="openChapterManage(row)">章节</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑书籍' : '新增书籍'" width="760px">
      <el-form label-position="top">
        <el-row :gutter="14">
          <el-col :span="12"><el-form-item label="书名"><el-input v-model="form.title" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="作者"><el-input v-model="form.author" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="请选择分类" filterable>
                <el-option
                  v-for="option in categoryOptions"
                  :key="option.id"
                  :label="`${option.level > 0 ? `${'　'.repeat(option.level)}└ ` : ''}${option.name}`"
                  :value="option.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文件类型">
              <el-select v-model="form.fileType">
                <el-option value="PDF" />
                <el-option value="EPUB" />
                <el-option value="TXT" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="封面地址"><el-input v-model="form.coverUrl" /></el-form-item></el-col>
          <el-col :span="24">
            <el-form-item label="文件地址">
              <el-input v-model="form.fileUrl" placeholder="可为空；使用章节模式时可不填" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="简介"><el-input v-model="form.description" type="textarea" rows="4" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="chapterDialogVisible"
      :title="`章节管理 - ${chapterBookTitle}`"
      width="1020px"
      @closed="closeChapterDialog"
    >
      <div class="chapter-toolbar">
        <el-space>
          <el-button type="primary" @click="openCreateChapter">新增章节</el-button>
          <el-button @click="loadChapters">刷新</el-button>
        </el-space>
      </div>
      <el-table v-loading="chapterLoading" :data="chapters">
        <el-table-column prop="sortOrder" label="排序" width="90" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column label="内容预览" min-width="360">
          <template #default="{ row }">
            <span>{{ previewContent(row.content) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-space>
              <el-button size="small" @click="openEditChapter(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeChapter(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="chapterFormDialogVisible" :title="chapterEditingId ? '编辑章节' : '新增章节'" width="760px" append-to-body>
      <el-form label-position="top">
        <el-form-item label="章节标题">
          <el-input v-model="chapterForm.title" placeholder="请输入章节标题" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="chapterForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="章节内容">
          <el-input v-model="chapterForm.content" type="textarea" :rows="12" placeholder="请输入章节正文内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chapterFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChapter">保存</el-button>
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

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.chapter-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}
</style>
