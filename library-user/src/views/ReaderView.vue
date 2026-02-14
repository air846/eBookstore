<script setup lang="ts">
// 阅读器页面：章节切换、段落排版、段评与催更
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";
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
const loading = ref(false);
const contentRef = ref<HTMLElement | null>(null);
const fileUrl = ref("");
const chapters = ref<ChapterRow[]>([]);
const currentIndex = ref(0);
const chapterDrawerVisible = ref(false);
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

  const chapters: ChapterRow[] = [];
  const buildChapter = (title: string, body: string) => {
    const normalizedBody = body.trim();
    if (!normalizedBody) return;
    chapters.push({
      id: -(chapters.length + 1),
      bookId,
      title,
      content: normalizedBody,
      sortOrder: chapters.length + 1
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
    // 没有明显章节标题时，按长度兜底切分，避免整本书只有一个章节。
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

  return chapters;
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

async function urge() {
  await request.post(`/book/${route.params.id}/urge`);
  ElMessage.success("催更成功，已通知管理员");
}

function resetReadPosition() {
  contentRef.value?.scrollTo({ top: 0, behavior: "auto" });
  window.scrollTo({ top: 0, behavior: "auto" });
}

onMounted(loadReadInfo);
</script>

<template>
  <div :class="['reader', { dark: setting.dark }]" v-loading="loading">
    <div class="toolbar glass">
      <el-space wrap>
        <el-button size="small" @click="chapterDrawerVisible = true">目录</el-button>
        <span class="label">字号</span>
        <el-slider v-model="setting.fontSize" :min="14" :max="28" style="width: 160px" />
        <el-switch v-model="setting.dark" inline-prompt active-text="夜间" inactive-text="日间" />
        <el-divider direction="vertical" />
        <span class="label">排版</span>
        <el-select v-model="paragraphMode" size="small" style="width: 120px">
          <el-option :value="1" label="一段式" />
          <el-option :value="2" label="二段式" />
          <el-option :value="3" label="三段式" />
        </el-select>
        <span class="label">行宽</span>
        <el-select v-model="lineWidthMode" size="small" style="width: 120px">
          <el-option value="comfort" label="舒适模式" />
          <el-option value="focus" label="专注模式" />
        </el-select>
        <el-button size="small" :disabled="currentIndex <= 0" @click="prevChapter">上一章</el-button>
        <el-button
          size="small"
          :disabled="currentIndex >= chapters.length - 1 || chapters.length === 0"
          @click="nextChapter"
        >
          下一章
        </el-button>
        <el-button v-if="isLastChapter" size="small" type="primary" plain @click="urge">催更</el-button>
        <span class="progress">进度：{{ progressText }}</span>
      </el-space>
    </div>

    <main ref="contentRef" :class="['content', `width-${lineWidthMode}`]" :style="{ fontSize: `${setting.fontSize}px` }">
      <template v-if="currentChapter">
        <div class="chapter-nav chapter-nav-top">
          <el-button size="small" :disabled="currentIndex <= 0" @click="prevChapter">上一章</el-button>
          <el-button
            size="small"
            :disabled="currentIndex >= chapters.length - 1 || chapters.length === 0"
            @click="nextChapter"
          >
            下一章
          </el-button>
        </div>
        <h1 class="chapter-title">{{ currentChapter.title }}</h1>
        <div :class="['chapter-content', `mode-${paragraphMode}`]">
          <div
            v-for="(paragraph, index) in currentParagraphs"
            :key="index"
            class="paragraph-block"
            @contextmenu.prevent="openComments(index)"
          >
            <p class="paragraph">{{ paragraph }}</p>
            <button
              v-if="commentCounts[index]"
              type="button"
              class="comment-bubble"
              @click="openComments(index)"
            >
              <span class="comment-bubble-icon" aria-hidden="true"></span>
              <span class="comment-bubble-count">{{ commentCounts[index] }}</span>
            </button>
          </div>
        </div>
        <div class="chapter-nav chapter-nav-bottom">
          <el-button size="small" :disabled="currentIndex <= 0" @click="prevChapter">上一章</el-button>
          <el-button
            size="small"
            :disabled="currentIndex >= chapters.length - 1 || chapters.length === 0"
            @click="nextChapter"
          >
            下一章
          </el-button>
        </div>
      </template>
      <template v-else>
        <el-empty description="当前书籍还没有章节内容" />
        <p class="file-url" v-if="fileUrl">资源地址：{{ fileUrl }}</p>
      </template>
    </main>

    <el-drawer v-model="chapterDrawerVisible" direction="ltr" size="320px" :with-header="false" append-to-body>
      <aside class="chapter-panel">
        <div class="panel-title">章节目录</div>
        <el-empty v-if="chapters.length === 0" description="暂无章节" />
        <div v-else class="chapter-list">
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            :class="['chapter-item', { active: index === currentIndex }]"
            @click="selectChapter(index)"
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
.reader {
  min-height: calc(100vh - 160px);
  border-radius: 18px;
  background: rgba(255, 252, 247, 0.65);
  box-shadow: var(--shadow-soft);
}

.reader.dark {
  background: rgba(22, 21, 20, 0.78);
  color: #f4efe8;
}

.toolbar {
  position: sticky;
  top: 92px;
  z-index: 6;
  padding: 12px 18px;
  border-bottom: 1px solid rgba(145, 121, 94, 0.14);
}

.glass {
  background: rgba(253, 248, 241, 0.66);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.label {
  color: var(--text-muted);
}

.progress {
  color: #725d49;
}

.content {
  position: relative;
  margin: 20px auto;
  padding: 28px 40px 30px;
  line-height: 2;
  border-radius: 16px;
  background: rgba(255, 250, 242, 0.84);
  box-shadow: inset 0 0 0 1px rgba(139, 114, 88, 0.08);
  transition: max-width 0.2s ease, background-color 0.2s ease;
}

.content::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image:
    radial-gradient(rgba(122, 99, 75, 0.06) 0.8px, transparent 0.8px),
    linear-gradient(140deg, rgba(255, 255, 255, 0.44), rgba(255, 255, 255, 0));
  background-size: 3px 3px, 100% 100%;
  opacity: 0.56;
}

.content > * {
  position: relative;
  z-index: 1;
}

.content.width-comfort {
  max-width: 1080px;
}

.content.width-focus {
  max-width: 760px;
}

.chapter-title {
  margin: 0 0 16px;
  font-size: 30px;
  line-height: 1.28;
  letter-spacing: 0.8px;
}

.chapter-content {
  column-gap: 32px;
}

.chapter-content.mode-2 {
  column-count: 2;
}

.chapter-content.mode-3 {
  column-count: 3;
}

.paragraph {
  margin: 0 0 1em;
  text-indent: 2em;
  white-space: pre-wrap;
  break-inside: avoid;
  color: inherit;
}

.paragraph-block {
  position: relative;
  break-inside: avoid;
}

.comment-bubble {
  position: absolute;
  right: 0;
  bottom: 6px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  border: none;
  border-radius: 999px;
  background: rgba(238, 225, 210, 0.85);
  color: #6b513b;
  font-size: 12px;
  line-height: 1;
  cursor: pointer;
  box-shadow: var(--shadow-light);
  transform: translateY(8px);
}

.comment-bubble-icon {
  width: 12px;
  height: 10px;
  border-radius: 6px;
  background: rgba(145, 121, 94, 0.55);
  position: relative;
}

.comment-bubble-icon::after {
  content: "";
  position: absolute;
  right: -2px;
  bottom: -2px;
  width: 6px;
  height: 6px;
  background: rgba(145, 121, 94, 0.55);
  border-radius: 2px;
  transform: rotate(45deg);
}

.comment-bubble-count {
  font-variant-numeric: tabular-nums;
}

.chapter-nav {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.chapter-nav-top {
  margin-bottom: 8px;
}

.chapter-nav-bottom {
  margin-top: 20px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 14px;
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
  transition: all 0.2s ease;
}

.chapter-item.active {
  background: rgba(154, 127, 98, 0.86);
  color: #fff;
}

.file-url {
  margin-top: 12px;
  color: var(--text-muted);
  font-size: 13px;
}

.reader.dark .toolbar {
  border-bottom-color: rgba(214, 198, 177, 0.18);
}

.reader.dark .glass {
  background: rgba(33, 30, 28, 0.74);
}

.reader.dark .chapter-item {
  background: rgba(52, 45, 38, 0.8);
  color: #f5efe7;
}

.reader.dark .content {
  background: rgba(35, 33, 31, 0.82);
  box-shadow: inset 0 0 0 1px rgba(221, 202, 177, 0.1);
}

.reader.dark .content::before {
  background-image:
    radial-gradient(rgba(236, 226, 214, 0.06) 0.9px, transparent 0.9px),
    linear-gradient(140deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0));
  opacity: 0.34;
}

.reader.dark .comment-bubble {
  background: rgba(63, 54, 45, 0.8);
  color: #e9dfd1;
}

.reader.dark .comment-bubble-icon,
.reader.dark .comment-bubble-icon::after {
  background: rgba(214, 198, 177, 0.7);
}

.reader.dark .reply-tip {
  background: rgba(58, 50, 43, 0.8);
  color: #e9dfd1;
}

.reader.dark .reply-item {
  background: rgba(52, 45, 38, 0.7);
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
  box-shadow: var(--shadow-light);
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

@media (max-width: 1240px) {
  .chapter-content.mode-3 {
    column-count: 2;
  }
}

@media (max-width: 900px) {
  .toolbar {
    top: 110px;
  }

  .content {
    padding: 20px 16px 24px;
    margin: 14px 10px;
    max-width: none;
  }

  .chapter-content.mode-2,
  .chapter-content.mode-3 {
    column-count: 1;
  }
}
</style>
