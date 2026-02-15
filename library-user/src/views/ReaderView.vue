<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "../utils/request";

interface ChapterRow {
  id: number;
  bookId: number;
  title: string;
  content: string;
  sortOrder: number;
}

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const contentRef = ref<HTMLElement | null>(null);
const fileUrl = ref("");
const chapters = ref<ChapterRow[]>([]);
const currentIndex = ref(0);
const chapterDrawerVisible = ref(false);
const settingVisible = ref(false);
const paragraphMode = ref<1 | 2 | 3>(1);
const lineWidthMode = ref<"comfort" | "focus">("comfort");
const setting = reactive({
  fontSize: 18,
  dark: false
});
const commentDrawerVisible = ref(false);
const commentLoading = ref(false);
const commentSubmitting = ref(false);
const selectedParagraphIndex = ref<number | null>(null);
const commentCounts = ref<Record<number, number>>({});
const comments = ref<any[]>([]);
const commentInput = ref("");
const commentSort = ref<"time" | "hot">("time");
const replyTarget = ref<{ id: number; userId: number } | null>(null);

const currentChapter = computed(() => chapters.value[currentIndex.value] || null);
const isLastChapter = computed(() => chapters.value.length > 0 && currentIndex.value === chapters.value.length - 1);
const progressText = computed(() => {
  if (!chapters.value.length) return "0%";
  return `${Math.round(((currentIndex.value + 1) * 100) / chapters.value.length)}%`;
});
const progressNumber = computed(() => Number(progressText.value.replace("%", "")) || 0);
const currentParagraphs = computed(() => splitParagraphs(currentChapter.value?.content || ""));

function splitParagraphs(content: string): string[] {
  const normalized = content.replace(/\r\n/g, "\n").trim();
  if (!normalized) return [];

  const paragraphs = normalized
    .split(/\n{2,}/)
    .map((item) => item.trim())
    .filter(Boolean);
  if (paragraphs.length > 1) return paragraphs;

  const sentenceParts = normalized
    .split(/(?<=[。！？.!?；;])\s*/g)
    .map((item) => item.trim())
    .filter(Boolean);
  if (sentenceParts.length <= 1) return [normalized];

  const merged: string[] = [];
  let buffer = "";
  sentenceParts.forEach((sentence) => {
    if ((buffer + sentence).length > 110) {
      if (buffer) merged.push(buffer);
      buffer = sentence;
      return;
    }
    buffer += sentence;
  });
  if (buffer) merged.push(buffer);
  return merged.length ? merged : [normalized];
}

function buildTxtChapters(rawContent: string, bookId: number): ChapterRow[] {
  const content = rawContent.replace(/\r\n/g, "\n").trim();
  if (!content) return [];

  const lines = content.split("\n");
  const titlePattern = /^(第[0-9一二三四五六七八九十百千万零两]+[章节卷篇部回].*|(?:chapter|chap\.?)\s*\d+.*)$/i;
  const titleIndexes: number[] = [];

  lines.forEach((line, idx) => {
    const text = line.trim();
    if (!text || text.length > 60) return;
    if (titlePattern.test(text)) {
      titleIndexes.push(idx);
    }
  });

  const chapterRows: ChapterRow[] = [];
  const buildChapter = (title: string, body: string) => {
    const normalizedBody = body.trim();
    if (!normalizedBody) return;
    chapterRows.push({
      id: -(chapterRows.length + 1),
      bookId,
      title,
      content: normalizedBody,
      sortOrder: chapterRows.length + 1
    });
  };

  if (titleIndexes.length > 0) {
    const firstTitleIndex = titleIndexes[0];
    const preface = lines.slice(0, firstTitleIndex).join("\n").trim();
    if (preface) {
      buildChapter("前言", preface);
    }
    for (let i = 0; i < titleIndexes.length; i += 1) {
      const start = titleIndexes[i];
      const end = i === titleIndexes.length - 1 ? lines.length : titleIndexes[i + 1];
      const title = lines[start].trim();
      const body = lines.slice(start + 1, end).join("\n");
      buildChapter(title || `第${i + 1}章`, body);
    }
  } else {
    const maxChars = 8000;
    let buffer = "";
    let index = 1;
    const paragraphs = content.split(/\n{2,}/).map((item) => item.trim()).filter(Boolean);
    paragraphs.forEach((paragraph) => {
      if ((buffer + "\n\n" + paragraph).length > maxChars && buffer) {
        buildChapter(`第${index}节`, buffer);
        index += 1;
        buffer = paragraph;
      } else {
        buffer = buffer ? `${buffer}\n\n${paragraph}` : paragraph;
      }
    });
    if (buffer) {
      buildChapter(`第${index}节`, buffer);
    }
  }

  return chapterRows;
}

async function saveHistory() {
  const chapterName = currentChapter.value?.title || "开始阅读";
  await request.post("/book/history", {
    bookId: Number(route.params.id),
    chapter: chapterName,
    progress: progressText.value
  });
}

async function loadReadInfo() {
  loading.value = true;
  try {
    const [readRes, chapterRes] = await Promise.all([
      request.get(`/book/read/${route.params.id}`),
      request.get(`/book/${route.params.id}/chapters`)
    ]);
    fileUrl.value = readRes.data || "";
    chapters.value = Array.isArray(chapterRes.data) ? chapterRes.data : [];
    if (chapters.value.length === 0 && fileUrl.value && /\.txt$/i.test(String(fileUrl.value).trim().replace(/^\"|\"$/g, ""))) {
      const textRes = await request.get(`/book/read-text/${route.params.id}`);
      const content = (textRes.data || "").toString();
      if (content.trim()) {
        const parsed = buildTxtChapters(content, Number(route.params.id));
        chapters.value = parsed.length
          ? parsed
          : [
              {
                id: -1,
                bookId: Number(route.params.id),
                title: "全文",
                content,
                sortOrder: 1
              }
            ];
      }
    }
    if (route.query.chapterId) {
      const chapterId = Number(route.query.chapterId);
      const idx = chapters.value.findIndex((item) => item.id === chapterId);
      currentIndex.value = idx >= 0 ? idx : 0;
    } else if (route.query.chapter) {
      const chapterText = String(route.query.chapter).trim();
      const idx = chapters.value.findIndex((item) => item.title === chapterText);
      currentIndex.value = idx >= 0 ? idx : 0;
    } else {
      currentIndex.value = 0;
    }
    await loadCommentCounts();
    await saveHistory();
    if (route.query.paragraphIndex !== undefined && currentChapter.value) {
      const paragraphIndex = Number(route.query.paragraphIndex);
      if (!Number.isNaN(paragraphIndex)) {
        await openComments(paragraphIndex);
      }
    }
  } finally {
    loading.value = false;
  }
}

async function selectChapter(index: number) {
  if (index < 0 || index >= chapters.value.length || index === currentIndex.value) return;
  currentIndex.value = index;
  chapterDrawerVisible.value = false;
  await nextTick();
  resetReadPosition();
  await saveHistory();
  await loadCommentCounts();
}

async function prevChapter() {
  await selectChapter(currentIndex.value - 1);
}

async function nextChapter() {
  await selectChapter(currentIndex.value + 1);
}

async function openComments(index: number) {
  if (!currentChapter.value || currentChapter.value.id <= 0) return;
  selectedParagraphIndex.value = index;
  commentDrawerVisible.value = true;
  replyTarget.value = null;
  await loadComments();
}

async function loadComments() {
  if (!currentChapter.value || currentChapter.value.id <= 0 || selectedParagraphIndex.value === null) return;
  commentLoading.value = true;
  try {
    const res = await request.get(`/book/${route.params.id}/chapter/${currentChapter.value.id}/comments`, {
      params: { paragraphIndex: selectedParagraphIndex.value, sortBy: commentSort.value }
    });
    comments.value = Array.isArray(res.data) ? res.data : [];
  } finally {
    commentLoading.value = false;
  }
}

async function loadCommentCounts() {
  if (!currentChapter.value || currentChapter.value.id <= 0) {
    commentCounts.value = {};
    return;
  }
  const res = await request.get(`/book/${route.params.id}/chapter/${currentChapter.value.id}/comment-counts`);
  const data = res.data || {};
  const normalized: Record<number, number> = {};
  Object.keys(data).forEach((key) => {
    const idx = Number(key);
    if (!Number.isNaN(idx)) {
      normalized[idx] = Number(data[key]) || 0;
    }
  });
  commentCounts.value = normalized;
}

async function submitComment() {
  if (!currentChapter.value || currentChapter.value.id <= 0 || selectedParagraphIndex.value === null) return;
  const content = commentInput.value.trim();
  if (!content) return;
  commentSubmitting.value = true;
  try {
    await request.post(`/book/${route.params.id}/chapter/${currentChapter.value.id}/comments`, {
      paragraphIndex: selectedParagraphIndex.value,
      content,
      parentId: replyTarget.value?.id || null
    });
    commentInput.value = "";
    replyTarget.value = null;
    await loadComments();
    await loadCommentCounts();
  } finally {
    commentSubmitting.value = false;
  }
}

async function react(commentId: number, value: number) {
  await request.post(`/book/comments/${commentId}/reaction`, { value });
  await loadComments();
}

async function hideComment(commentId: number) {
  await request.post(`/book/comments/${commentId}/hide`);
  await loadComments();
}

async function unhideComment(commentId: number) {
  await request.delete(`/book/comments/${commentId}/hide`);
  await loadComments();
}

function startReply(item: any) {
  replyTarget.value = { id: item.id, userId: item.userId };
}

function cancelReply() {
  replyTarget.value = null;
}

function showRipple(event: MouseEvent) {
  const host = event.currentTarget as HTMLElement | null;
  if (!host) return;
  const rect = host.getBoundingClientRect();
  const ripple = document.createElement("span");
  ripple.className = "reader-ripple";
  ripple.style.left = `${event.clientX - rect.left}px`;
  ripple.style.top = `${event.clientY - rect.top}px`;
  host.appendChild(ripple);
  window.setTimeout(() => ripple.remove(), 520);
}

function onSelectChapter(index: number, event: MouseEvent) {
  showRipple(event);
  selectChapter(index);
}

async function urge() {
  await request.post(`/book/${route.params.id}/urge`);
  ElMessage.success("催更成功，已通知管理员");
}

function resetReadPosition() {
  contentRef.value?.scrollTo({ top: 0, behavior: "auto" });
  window.scrollTo({ top: 0, behavior: "auto" });
}

function goBack() {
  router.back();
}

onMounted(loadReadInfo);
</script>

<template>
  <div :class="['reader-shell', { dark: setting.dark }]">
    <header class="reader-topbar">
      <el-button circle plain @click="goBack">返</el-button>
      <div class="top-actions">
        <el-button circle plain @click="settingVisible = !settingVisible">字</el-button>
        <el-button circle plain @click="setting.dark = !setting.dark">夜</el-button>
        <el-button circle plain @click="chapterDrawerVisible = true">章</el-button>
      </div>
    </header>

    <div v-if="settingVisible" class="setting-panel">
      <div class="setting-row">
        <span>字号</span>
        <el-slider v-model="setting.fontSize" :min="14" :max="28" style="width: 180px" />
      </div>
      <div class="setting-row">
        <span>分栏</span>
        <el-select v-model="paragraphMode" size="small" style="width: 120px">
          <el-option :value="1" label="单栏" />
          <el-option :value="2" label="双栏" />
          <el-option :value="3" label="三栏" />
        </el-select>
        <span>宽度</span>
        <el-select v-model="lineWidthMode" size="small" style="width: 120px">
          <el-option value="comfort" label="舒适" />
          <el-option value="focus" label="专注" />
        </el-select>
      </div>
    </div>

    <main ref="contentRef" :class="['reader-content', `width-${lineWidthMode}`]" :style="{ fontSize: `${setting.fontSize}px` }">
      <template v-if="loading">
        <el-skeleton :rows="8" animated />
      </template>

      <template v-else-if="currentChapter">
        <h1 class="chapter-title">{{ currentChapter.title }}</h1>

        <div :class="['chapter-content', `mode-${paragraphMode}`]">
          <div
            v-for="(paragraph, index) in currentParagraphs"
            :key="index"
            class="paragraph-block ripple-host"
            @contextmenu.prevent="openComments(index)"
            @click="showRipple($event)"
          >
            <p class="paragraph">{{ paragraph }}</p>
            <button
              v-if="commentCounts[index]"
              type="button"
              class="comment-bubble"
              @click="openComments(index)"
            >
              评 {{ commentCounts[index] }}
            </button>
          </div>
        </div>
      </template>

      <template v-else>
        <el-empty description="当前书籍还没有章节内容" />
        <p class="file-url" v-if="fileUrl">资源地址：{{ fileUrl }}</p>
      </template>
    </main>

    <footer class="reader-footer">
      <div class="progress-row">
        <span>CHAPTER {{ String(currentIndex + 1).padStart(2, "0") }}</span>
        <span>PROGRESS {{ progressText }}</span>
      </div>
      <div class="progress-bar">
        <span :style="{ width: `${progressNumber}%` }"></span>
      </div>
      <div class="chapter-nav">
        <el-button size="small" :disabled="currentIndex <= 0" @click="prevChapter">上一章</el-button>
        <el-button size="small" :disabled="currentIndex >= chapters.length - 1 || chapters.length === 0" @click="nextChapter">
          下一章
        </el-button>
        <el-button v-if="isLastChapter" size="small" type="primary" plain @click="urge">催更</el-button>
      </div>
    </footer>

    <el-drawer v-model="chapterDrawerVisible" direction="ltr" size="320px" :with-header="false" append-to-body>
      <aside class="chapter-panel">
        <div class="panel-title">章节目录</div>
        <el-empty v-if="chapters.length === 0" description="暂无章节" />
        <div v-else class="chapter-list">
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            :class="['chapter-item', 'ripple-host', { active: index === currentIndex }]"
            @click="onSelectChapter(index, $event)"
          >
            {{ index + 1 }}. {{ chapter.title }}
          </div>
        </div>
      </aside>
    </el-drawer>

    <el-drawer v-model="commentDrawerVisible" direction="rtl" size="420px" append-to-body>
      <div class="comment-panel">
        <div class="panel-title">段评</div>
        <el-select v-model="commentSort" size="small" style="width: 120px" @change="loadComments">
          <el-option value="time" label="最新" />
          <el-option value="hot" label="最热" />
        </el-select>

        <div class="comment-input">
          <div v-if="replyTarget" class="reply-tip">
            正在回复用户 {{ replyTarget.userId }} 的评论
            <el-button size="small" text @click="cancelReply">取消</el-button>
          </div>
          <el-input v-model="commentInput" type="textarea" :rows="4" placeholder="说点什么吧" />
          <div class="comment-actions">
            <el-button type="primary" :loading="commentSubmitting" @click="submitComment">提交</el-button>
          </div>
        </div>

        <el-divider />

        <div v-loading="commentLoading" class="comment-list">
          <el-empty v-if="!comments.length" description="暂无评论" />
          <div v-for="item in comments" :key="item.id" class="comment-item">
            <div class="comment-content" v-if="!item.hidden">
              <p>{{ item.content }}</p>
              <div class="comment-meta">
                <span>赞 {{ item.likeCount || 0 }}</span>
                <span>踩 {{ item.dislikeCount || 0 }}</span>
              </div>
              <div class="comment-buttons">
                <el-button size="small" text @click="react(item.id, item.userReaction === 1 ? 0 : 1)">
                  {{ item.userReaction === 1 ? "取消赞" : "点赞" }}
                </el-button>
                <el-button size="small" text @click="react(item.id, item.userReaction === -1 ? 0 : -1)">
                  {{ item.userReaction === -1 ? "取消踩" : "拉踩" }}
                </el-button>
                <el-button size="small" text @click="startReply(item)">回复</el-button>
                <el-button size="small" text @click="hideComment(item.id)">折叠</el-button>
              </div>
            </div>
            <div v-else class="comment-hidden">
              <span>该评论已折叠</span>
              <el-button size="small" text @click="unhideComment(item.id)">展开</el-button>
            </div>

            <div v-if="item.replies && item.replies.length" class="reply-list">
              <div v-for="reply in item.replies" :key="reply.id" class="reply-item">
                <div class="comment-content" v-if="!reply.hidden">
                  <p>{{ reply.content }}</p>
                  <div class="comment-meta">
                    <span>赞 {{ reply.likeCount || 0 }}</span>
                    <span>踩 {{ reply.dislikeCount || 0 }}</span>
                  </div>
                  <div class="comment-buttons">
                    <el-button size="small" text @click="react(reply.id, reply.userReaction === 1 ? 0 : 1)">
                      {{ reply.userReaction === 1 ? "取消赞" : "点赞" }}
                    </el-button>
                    <el-button size="small" text @click="react(reply.id, reply.userReaction === -1 ? 0 : -1)">
                      {{ reply.userReaction === -1 ? "取消踩" : "拉踩" }}
                    </el-button>
                    <el-button size="small" text @click="startReply(item)">回复</el-button>
                    <el-button size="small" text @click="hideComment(reply.id)">折叠</el-button>
                  </div>
                </div>
                <div v-else class="comment-hidden">
                  <span>该评论已折叠</span>
                  <el-button size="small" text @click="unhideComment(reply.id)">展开</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.reader-shell {
  min-height: calc(100vh - 160px);
  background: #f4ecd8;
  color: #5b4636;
  border-radius: 18px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}

.reader-shell.dark {
  background: #1a1a1a;
  color: #d2c7b8;
}

.reader-topbar {
  height: 64px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(128, 101, 73, 0.16);
  background: rgba(253, 247, 236, 0.64);
  backdrop-filter: blur(10px);
}

.dark .reader-topbar {
  background: rgba(35, 35, 35, 0.85);
  border-bottom-color: rgba(212, 196, 178, 0.14);
}

.top-actions {
  display: flex;
  gap: 8px;
}

.setting-panel {
  padding: 10px 16px;
  border-bottom: 1px solid rgba(128, 101, 73, 0.14);
  background: rgba(255, 249, 239, 0.66);
  display: grid;
  gap: 10px;
}

.dark .setting-panel {
  background: rgba(39, 39, 39, 0.84);
}

.setting-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reader-content {
  flex: 1;
  margin: 20px auto 10px;
  width: min(100%, 1080px);
  padding: 24px 30px;
  overflow: auto;
}

.reader-content.width-focus {
  width: min(100%, 760px);
}

.chapter-title {
  margin: 0 0 16px;
  line-height: 1.35;
}

.chapter-content {
  column-gap: 24px;
}

.chapter-content.mode-2 {
  column-count: 2;
}

.chapter-content.mode-3 {
  column-count: 3;
}

.paragraph-block {
  position: relative;
  break-inside: avoid;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.paragraph-block:hover {
  background: rgba(175, 143, 111, 0.08);
}

.paragraph {
  margin: 0 0 1em;
  line-height: 2;
  text-indent: 2em;
  white-space: pre-wrap;
}

.comment-bubble {
  position: absolute;
  right: 0;
  bottom: 4px;
  border: none;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
  cursor: pointer;
  background: rgba(175, 143, 111, 0.16);
  color: #6b513b;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.comment-bubble:hover {
  transform: translateY(-2px);
  background: rgba(175, 143, 111, 0.3);
}

.dark .comment-bubble {
  background: rgba(212, 196, 178, 0.2);
  color: #f0e5d8;
}

.reader-footer {
  padding: 10px 16px 14px;
  border-top: 1px solid rgba(128, 101, 73, 0.14);
  background: rgba(253, 247, 236, 0.74);
}

.dark .reader-footer {
  background: rgba(30, 30, 30, 0.88);
}

.progress-row {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  letter-spacing: 1px;
  opacity: 0.7;
}

.progress-bar {
  margin-top: 6px;
  height: 4px;
  background: rgba(92, 71, 50, 0.14);
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar span {
  display: block;
  height: 100%;
  background: #af8f6f;
}

.chapter-nav {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

.file-url {
  margin-top: 12px;
  color: var(--text-muted);
  font-size: 13px;
}

.panel-title {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: 600;
}

.chapter-list {
  max-height: calc(100vh - 120px);
  overflow: auto;
}

.chapter-item {
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 8px;
  background: rgba(246, 238, 229, 0.74);
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.chapter-item:hover {
  transform: translateX(2px);
  background: rgba(236, 225, 211, 0.85);
}

.chapter-item.active {
  background: rgba(154, 127, 98, 0.86);
  color: #fff;
}

.comment-panel {
  display: grid;
  gap: 12px;
}

.comment-input {
  display: grid;
  gap: 8px;
}

.reply-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  border-radius: 10px;
  background: rgba(244, 236, 227, 0.8);
  color: #6b513b;
  font-size: 13px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  display: grid;
  gap: 12px;
}

.comment-item {
  padding: 12px;
  border-radius: 12px;
  background: rgba(250, 244, 236, 0.7);
  transition: transform 0.2s ease;
}

.comment-item:hover {
  transform: translateY(-2px);
}

.comment-content p {
  margin: 0 0 8px;
  line-height: 1.6;
}

.comment-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
}

.comment-buttons {
  margin-top: 6px;
  display: flex;
  gap: 8px;
}

.comment-hidden {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-muted);
}

.reply-list {
  margin-top: 10px;
  padding-left: 12px;
  border-left: 2px solid rgba(145, 121, 94, 0.2);
  display: grid;
  gap: 10px;
}

.reply-item {
  padding: 10px;
  border-radius: 10px;
  background: rgba(248, 242, 235, 0.7);
}

.dark .reply-tip,
.dark .reply-item,
.dark .comment-item {
  background: rgba(52, 45, 38, 0.75);
}

.ripple-host {
  position: relative;
  overflow: hidden;
}

:deep(.reader-ripple) {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: rgba(175, 143, 111, 0.3);
  transform: translate(-50%, -50%) scale(0);
  pointer-events: none;
  animation: reader-ripple 0.52s ease-out;
}

@keyframes reader-ripple {
  to {
    transform: translate(-50%, -50%) scale(16);
    opacity: 0;
  }
}

@media (max-width: 1100px) {
  .chapter-content.mode-3 {
    column-count: 2;
  }
}

@media (max-width: 900px) {
  .reader-content {
    padding: 16px;
    margin: 10px auto 6px;
  }

  .chapter-content.mode-2,
  .chapter-content.mode-3 {
    column-count: 1;
  }
}
</style>
