const { logout, getUserInfo } = require("../../services/user");
const { getHistoryList } = require("../../services/book");
const { clearAuth, getUser, setUser } = require("../../utils/storage");
const { requireAuth } = require("../../utils/guard");

function formatReadTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  const y = date.getFullYear();
  const m = `${date.getMonth() + 1}`.padStart(2, "0");
  const d = `${date.getDate()}`.padStart(2, "0");
  const hh = `${date.getHours()}`.padStart(2, "0");
  const mm = `${date.getMinutes()}`.padStart(2, "0");
  return `${y}-${m}-${d} ${hh}:${mm}`;
}

Page({
  data: {
    user: {},
    history: [],
    loading: false,
    errorText: ""
  },
  onShow() {
    this.loadData();
  },
  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh());
  },
  async loadData() {
    if (!requireAuth()) return;
    this.setData({ loading: true, errorText: "" });
    const cacheUser = getUser();
    if (cacheUser) {
      this.setData({ user: cacheUser });
    }
    try {
      const [user, history] = await Promise.all([getUserInfo(), getHistoryList()]);
      const historyView = (history || []).map((item) => ({
        ...item,
        readTimeText: formatReadTime(item.readTime)
      }));
      setUser(user || {});
      this.setData({
        user: user || {},
        history: historyView
      });
    } catch (error) {
      this.setData({ errorText: "数据加载失败，下拉重试" });
    } finally {
      this.setData({ loading: false });
    }
  },
  goHistoryRead(e) {
    const { bookid } = e.currentTarget.dataset;
    if (!bookid) return;
    wx.navigateTo({ url: `/pages/reader/index?id=${bookid}` });
  },
  goHistoryDetail(e) {
    const { bookid } = e.currentTarget.dataset;
    if (!bookid) return;
    wx.navigateTo({ url: `/pages/book-detail/index?id=${bookid}` });
  },
  async handleLogout() {
    try {
      await logout();
    } finally {
      clearAuth();
      const app = getApp();
      app.globalData.token = "";
      app.globalData.user = null;
      wx.reLaunch({ url: "/pages/login/index" });
    }
  }
});
