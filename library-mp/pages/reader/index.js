const {
  getChapters,
  getReadText,
  saveHistory,
  getHistoryList,
  getCommentCounts,
  getComments,
  createComment,
  reactComment,
  hideComment,
  unhideComment,
  urgeBook
} = require("../../services/book");
const { requireAuth } = require("../../utils/guard");

const READER_SETTING_KEY = "ebookstore_reader_setting";

function getDefaultSetting() {
  return {
    fontSize: 32,
    darkMode: false,
    paragraphMode: 1
  };
}

function loadReaderSetting() {
  return wx.getStorageSync(READER_SETTING_KEY) || getDefaultSetting();
}

function saveReaderSetting(setting) {
  wx.setStorageSync(READER_SETTING_KEY, {
    ...getDefaultSetting(),
    ...(setting || {})
  });
}

function splitParagraphs(content) {
  const normalized = String(content || "").replace(/\r\n/g, "\n").trim();
  if (!normalized) return [];

  const paragraphs = normalized
    .split(/\n{2,}/)
    .map((item) => item.trim())
    .filter(Boolean);
  if (paragraphs.length > 1) return paragraphs;

  const parts = normalized.split(/([。！？.!?；;])/).filter(Boolean);
  if (parts.length <= 1) return [normalized];

  const sentenceParts = [];
  for (let i = 0; i < parts.length; i += 2) {
    const text = parts[i] || "";
    const punct = parts[i + 1] || "";
    const sentence = `${text}${punct}`.trim();
    if (sentence) sentenceParts.push(sentence);
  }
  if (!sentenceParts.length) return [normalized];

  const merged = [];
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

function buildTxtChapters(rawContent, bookId) {
  const content = String(rawContent || "").replace(/\r\n/g, "\n").trim();
  if (!content) return [];

  const lines = content.split("\n");
  const titlePattern = /^(第[0-9一二三四五六七八九十百千万零两]+[章节卷篇部回].*|(?:chapter|chap\.?)\s*\d+.*)$/i;
  const titleIndexes = [];

  lines.forEach((line, idx) => {
    const text = line.trim();
    if (!text || text.length > 60) return;
    if (titlePattern.test(text)) {
      titleIndexes.push(idx);
    }
  });

  const chapters = [];
  const buildChapter = (title, body) => {
    const normalizedBody = String(body || "").trim();
    if (!normalizedBody) return;
    chapters.push({
      id: -(chapters.length + 1),
      bookId,
      virtualChapterIndex: chapters.length + 1,
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
    const maxChars = 8000;
    let buffer = "";
    let index = 1;
    const parts = content
      .split(/\n{2,}/)
      .map((item) => item.trim())
      .filter(Boolean);
    parts.forEach((paragraph) => {
      if (`${buffer}\n\n${paragraph}`.length > maxChars && buffer) {
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

function buildParagraphBlocks(paragraphs, groupSize, countMap) {
  if (!paragraphs.length) {
    return [{ key: "empty", start: 0, text: "当前章节暂无内容", commentCount: 0 }];
  }
  const size = Math.max(1, Math.min(3, Number(groupSize) || 1));
  const result = [];
  for (let i = 0; i < paragraphs.length; i += size) {
    const part = paragraphs.slice(i, i + size);
    let commentCount = 0;
    for (let j = i; j < i + part.length; j += 1) {
      commentCount += Number((countMap && countMap[j]) || 0);
    }
    result.push({
      key: `${i}-${part.length}`,
      start: i,
      text: part.join("\n\n"),
      commentCount
    });
  }
  return result;
}

function formatCommentTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  const m = `${date.getMonth() + 1}`.padStart(2, "0");
  const d = `${date.getDate()}`.padStart(2, "0");
  const hh = `${date.getHours()}`.padStart(2, "0");
  const mm = `${date.getMinutes()}`.padStart(2, "0");
  return `${m}-${d} ${hh}:${mm}`;
}

function normalizeComments(items) {
  return (items || []).map((item) => ({
    ...item,
    displayName: item.nickname || item.username || `用户${item.userId || ""}`,
    createTimeText: formatCommentTime(item.createTime),
    replies: (item.replies || []).map((reply) => ({
      ...reply,
      displayName: reply.nickname || reply.username || `用户${reply.userId || ""}`,
      createTimeText: formatCommentTime(reply.createTime)
    }))
  }));
}

Page({
  data: {
    id: 0,
    chapters: [],
    chapterTitles: [],
    currentIndex: 0,
    paragraphBlocks: [],
    loading: true,
    errorText: "",
    fontSize: 32,
    darkMode: false,
    paragraphMode: 1,
    progressText: "0%",
    canComment: false,
    isLastChapter: false,
    commentCounts: {},
    commentVisible: false,
    selectedParagraphIndex: -1,
    commentSort: "time",
    comments: [],
    commentLoading: false,
    commentInput: "",
    commentSubmitting: false,
    replyParentId: 0,
    replyHint: ""
  },
  async onLoad(options) {
    if (!requireAuth()) return;
    const id = Number(options.id || 0);
    if (!id) {
      this.setData({ loading: false, errorText: "参数错误，无法打开阅读器" });
      return;
    }
    const setting = loadReaderSetting();
    this.setData({
      id,
      fontSize: setting.fontSize || 32,
      darkMode: !!setting.darkMode,
      paragraphMode: setting.paragraphMode || 1
    });
    await this.loadChapters();
  },
  onPullDownRefresh() {
    this.loadChapters().finally(() => wx.stopPullDownRefresh());
  },
  async loadChapters() {
    this.setData({ loading: true, errorText: "" });
    try {
      const [chaptersRes, historyRes] = await Promise.all([getChapters(this.data.id), getHistoryList()]);
      let chapters = (chaptersRes || []).filter((item) => item && item.title);
      if (!chapters.length) {
        const raw = await this.loadTxtContent();
        chapters = buildTxtChapters(raw, this.data.id);
        if (!chapters.length && raw) {
          chapters = [{ id: -1, virtualChapterIndex: 1, title: "全文", content: raw }];
        }
      }
      const chapterTitles = chapters.map((item) => item.title);
      const currentIndex = this.resolveHistoryIndex(chapters, historyRes || []);
      this.setData({
        chapters,
        chapterTitles,
        currentIndex
      });
      await this.refreshReadingView(true);
      await this.reportHistory(currentIndex);
    } catch (error) {
      this.setData({ errorText: "章节加载失败，请下拉重试" });
    } finally {
      this.setData({ loading: false });
    }
  },
  async loadTxtContent() {
    try {
      return await getReadText(this.data.id);
    } catch (error) {
      return "";
    }
  },
  resolveHistoryIndex(chapters, historyList) {
    const current = (historyList || []).find((item) => Number(item.bookId) === this.data.id);
    if (!current || !current.chapter) return 0;
    const index = chapters.findIndex((item) => item.title === current.chapter);
    return index >= 0 ? index : 0;
  },
  async refreshReadingView(loadCounts) {
    const chapter = this.data.chapters[this.data.currentIndex];
    if (!chapter) {
      this.setData({
        paragraphBlocks: [],
        progressText: "0%",
        canComment: false,
        isLastChapter: false,
        commentCounts: {}
      });
      return;
    }
    let counts = this.data.commentCounts;
    if (loadCounts) {
      counts = await this.loadCommentCounts(chapter);
    }
    const paragraphs = splitParagraphs(chapter.content || "");
    const paragraphBlocks = buildParagraphBlocks(paragraphs, this.data.paragraphMode, counts);
    const progressText = `${Math.round(((this.data.currentIndex + 1) * 100) / this.data.chapters.length)}%`;
    this.setData({
      paragraphBlocks,
      progressText,
      canComment: true,
      isLastChapter: this.data.currentIndex === this.data.chapters.length - 1,
      commentCounts: counts
    });
  },
  async loadCommentCounts(chapter) {
    if (!chapter) return {};
    const chapterId = Number(chapter.id) > 0 ? Number(chapter.id) : 0;
    const virtualChapterIndex = chapter.virtualChapterIndex || (Number(chapter.id) < 0 ? Math.abs(Number(chapter.id)) : undefined);
    try {
      const data = await getCommentCounts(this.data.id, chapterId, virtualChapterIndex);
      const normalized = {};
      Object.keys(data || {}).forEach((key) => {
        const idx = Number(key);
        if (!Number.isNaN(idx)) {
          normalized[idx] = Number(data[key]) || 0;
        }
      });
      return normalized;
    } catch (error) {
      return {};
    }
  },
  async onChapterChange(e) {
    const index = Number(e.detail.value || 0);
    await this.switchChapter(index);
  },
  async prevChapter() {
    await this.switchChapter(this.data.currentIndex - 1);
  },
  async nextChapter() {
    await this.switchChapter(this.data.currentIndex + 1);
  },
  async switchChapter(index) {
    if (index < 0 || index >= this.data.chapters.length || index === this.data.currentIndex) return;
    this.setData({
      currentIndex: index,
      commentVisible: false,
      selectedParagraphIndex: -1
    });
    await this.refreshReadingView(true);
    this.scrollToChapterTop();
    await this.reportHistory(index);
  },
  scrollToChapterTop() {
    // Ensure chapter switch always starts reading from top.
    wx.pageScrollTo({
      selector: "#chapterTop",
      duration: 0
    });
  },
  setParagraphMode(e) {
    const mode = Number(e.currentTarget.dataset.mode || 1);
    if (mode < 1 || mode > 3 || mode === this.data.paragraphMode) return;
    this.setData({ paragraphMode: mode });
    this.refreshReadingView(false);
    saveReaderSetting({
      fontSize: this.data.fontSize,
      darkMode: this.data.darkMode,
      paragraphMode: mode
    });
  },
  changeFontSize(e) {
    const action = e.currentTarget.dataset.action;
    const delta = action === "inc" ? 2 : -2;
    const fontSize = Math.max(24, Math.min(44, this.data.fontSize + delta));
    this.setData({ fontSize });
    saveReaderSetting({
      fontSize,
      darkMode: this.data.darkMode,
      paragraphMode: this.data.paragraphMode
    });
  },
  toggleTheme() {
    const darkMode = !this.data.darkMode;
    this.setData({ darkMode });
    saveReaderSetting({
      fontSize: this.data.fontSize,
      darkMode,
      paragraphMode: this.data.paragraphMode
    });
  },
  async reportHistory(index) {
    const chapter = this.data.chapters[index];
    if (!chapter) return;
    try {
      await saveHistory({
        bookId: this.data.id,
        chapter: chapter.title,
        progress: `${index + 1}/${this.data.chapters.length}`
      });
    } catch (error) {
      console.warn("saveHistory failed", error);
    }
  },
  async openComments(e) {
    const paragraphIndex = Number(e.currentTarget.dataset.start || 0);
    const chapter = this.data.chapters[this.data.currentIndex];
    if (!chapter) return;
    this.setData({
      commentVisible: true,
      selectedParagraphIndex: paragraphIndex,
      commentInput: "",
      replyParentId: 0,
      replyHint: ""
    });
    await this.loadComments();
  },
  async onParagraphLongPress(e) {
    await this.openComments(e);
  },
  closeCommentPanel() {
    this.setData({ commentVisible: false });
  },
  stopBubble() {},
  setCommentSort(e) {
    const sort = e.currentTarget.dataset.sort;
    if (!sort || sort === this.data.commentSort) return;
    this.setData({ commentSort: sort });
    this.loadComments();
  },
  async loadComments() {
    const chapter = this.data.chapters[this.data.currentIndex];
    if (!chapter || this.data.selectedParagraphIndex < 0) return;
    const chapterId = Number(chapter.id) > 0 ? Number(chapter.id) : 0;
    const virtualChapterIndex = chapter.virtualChapterIndex || (Number(chapter.id) < 0 ? Math.abs(Number(chapter.id)) : undefined);
    this.setData({ commentLoading: true });
    try {
      const list = await getComments(
        this.data.id,
        chapterId,
        this.data.selectedParagraphIndex,
        this.data.commentSort,
        virtualChapterIndex
      );
      this.setData({ comments: normalizeComments(list) });
    } finally {
      this.setData({ commentLoading: false });
    }
  },
  onCommentInput(e) {
    this.setData({ commentInput: e.detail.value });
  },
  startReply(e) {
    const parentId = Number(e.currentTarget.dataset.parentid || 0);
    const username = e.currentTarget.dataset.username;
    if (!parentId) return;
    this.setData({
      replyParentId: parentId,
      replyHint: username ? `正在回复 ${username}` : "正在回复评论"
    });
  },
  cancelReply() {
    this.setData({
      replyParentId: 0,
      replyHint: ""
    });
  },
  async submitComment() {
    const chapter = this.data.chapters[this.data.currentIndex];
    const content = String(this.data.commentInput || "").trim();
    if (!chapter || this.data.selectedParagraphIndex < 0) return;
    const chapterId = Number(chapter.id) > 0 ? Number(chapter.id) : 0;
    const virtualChapterIndex = chapter.virtualChapterIndex || (Number(chapter.id) < 0 ? Math.abs(Number(chapter.id)) : undefined);
    if (!content) {
      wx.showToast({ title: "请输入评论内容", icon: "none" });
      return;
    }
    this.setData({ commentSubmitting: true });
    try {
      await createComment(this.data.id, chapterId, {
        paragraphIndex: this.data.selectedParagraphIndex,
        content,
        virtualChapterIndex,
        parentId: this.data.replyParentId || undefined
      });
      this.setData({
        commentInput: "",
        replyParentId: 0,
        replyHint: ""
      });
      await this.loadComments();
      await this.refreshReadingView(true);
    } finally {
      this.setData({ commentSubmitting: false });
    }
  },
  async onReact(e) {
    const commentId = Number(e.currentTarget.dataset.id || 0);
    const value = Number(e.currentTarget.dataset.value || 0);
    if (!commentId) return;
    await reactComment(commentId, value);
    await this.loadComments();
  },
  async onHide(e) {
    const commentId = Number(e.currentTarget.dataset.id || 0);
    if (!commentId) return;
    await hideComment(commentId);
    await this.loadComments();
  },
  async onUnhide(e) {
    const commentId = Number(e.currentTarget.dataset.id || 0);
    if (!commentId) return;
    await unhideComment(commentId);
    await this.loadComments();
  },
  async urgeUpdate() {
    if (!this.data.isLastChapter) return;
    await urgeBook(this.data.id);
    wx.showToast({ title: "催更成功", icon: "none" });
  }
});
