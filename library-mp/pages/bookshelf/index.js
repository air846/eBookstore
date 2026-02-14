const { getFavoriteList } = require("../../services/book");
const { requireAuth } = require("../../utils/guard");

Page({
  data: {
    books: [],
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
    try {
      const books = await getFavoriteList();
      this.setData({ books: books || [] });
    } catch (error) {
      this.setData({ errorText: "书架加载失败，点击重试" });
    } finally {
      this.setData({ loading: false });
    }
  },
  retryLoad() {
    this.loadData();
  },
  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/book-detail/index?id=${id}` });
  },
  goRead(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/reader/index?id=${id}` });
  }
});
