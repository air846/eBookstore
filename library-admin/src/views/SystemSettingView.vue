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

const serverLoad = ref<any>(null);
const loadingServerInfo = ref(false);

async function loadList() {
  const res = await request.get("/admin/system/carousel/list");
  list.value = res.data;
}

async function loadServerLoad() {
  loadingServerInfo.value = true;
  try {
    const res = await request.get("/admin/system/server-load");
    console.log("服务器负载数据:", res.data);
    serverLoad.value = res.data;
  } catch (error) {
    console.error("获取服务器负载失败:", error);
  } finally {
    loadingServerInfo.value = false;
  }
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

function formatUptime(seconds: number): string {
  const days = Math.floor(seconds / 86400);
  const hours = Math.floor((seconds % 86400) / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return `${days}天 ${hours}小时 ${minutes}分钟`;
}

onMounted(() => {
  loadList();
  loadServerLoad();
});
</script>

<template>
  <div class="page">
    <h1 class="title">系统设置</h1>
    <p class="subtitle">管理首页轮播图，支持排序、跳转链接与启停。</p>

    <!-- 服务器负载监控 -->
    <el-card v-loading="loadingServerInfo">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: 600;">服务器负载监控</span>
          <el-button size="small" @click="loadServerLoad">刷新</el-button>
        </div>
      </template>
      <div v-if="serverLoad" class="server-info">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <div class="info-label">CPU 使用率</div>
              <el-progress
                :percentage="serverLoad.cpuUsage"
                :color="serverLoad.cpuUsage > 80 ? '#F56C6C' : serverLoad.cpuUsage > 60 ? '#E6A23C' : '#67C23A'"
              />
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <div class="info-label">内存使用率</div>
              <el-progress
                :percentage="serverLoad.memoryUsage"
                :color="serverLoad.memoryUsage > 80 ? '#F56C6C' : serverLoad.memoryUsage > 60 ? '#E6A23C' : '#67C23A'"
              />
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">系统内存</div>
              <div class="info-value">{{ serverLoad.usedMemory }} MB / {{ serverLoad.totalMemory }} MB</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">JVM 内存</div>
              <div class="info-value">{{ serverLoad.jvmUsedMemory }} MB / {{ serverLoad.jvmMaxMemory }} MB</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">运行时间</div>
              <div class="info-value">{{ formatUptime(serverLoad.uptime) }}</div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">操作系统</div>
              <div class="info-value">{{ serverLoad.osName }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">系统架构</div>
              <div class="info-value">{{ serverLoad.osArch }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <div class="info-label">处理器数量</div>
              <div class="info-value">{{ serverLoad.availableProcessors }} 核</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card class="spaced">
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

.server-info {
  padding: 10px 0;
}

.info-item {
  margin-bottom: 10px;
}

.info-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.info-value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}
</style>
