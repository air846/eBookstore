const { getBookDetail, favorite, unfavorite, getFavoriteList } = require("../../services/book");
const { requireAuth } = require("../../utils/guard");
const { hasToken } = require("../../utils/storage");

Page({
  data: {
    id: 0,
    detail: {},
    loading: true,
    errorText: "",
    isFavorited: false,
    favoriteText: "收藏"
  },
  async onLoad(options) {
    const id = Number(options.id || 0);
    if (!id) {
      this.setData({ loading: false, errorText: "参数错误，无法加载图书" });
      return;
    }
    this.setData({ id });
    await this.loadDetail();
    await this.checkFavorite();
  },
  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh());
  },
  async loadDetail() {
    this.setData({ loading: true, errorText: "" });
    try {
      const detail = await getBookDetail(this.data.id);
      this.setData({ detail: detail || {} });
    } catch (error) {
      this.setData({ errorText: "图书加载失败，请重试" });
    } finally {
      this.setData({ loading: false });
    }
  },
  async checkFavorite() {
    if (!hasToken()) {
      this.setData({
        isFavorited: false,
        favoriteText: "收藏"
      });
      return;
    }
    try {
      const list = await getFavoriteList();
      const exists = (list || []).some((item) => Number(item.id) === this.data.id);
      this.setData({
        isFavorited: exists,
        favoriteText: exists ? "取消收藏" : "收藏"
      });
    } catch (error) {
      this.setData({
        isFavorited: false,
        favoriteText: "收藏"
      });
    }
  },
  async toggleFavorite() {
    if (!requireAuth()) return;
    try {
      if (this.data.isFavorited) {
        await unfavorite(this.data.id);
        wx.showToast({ title: "已取消收藏", icon: "none" });
      } else {
        await favorite(this.data.id);
        wx.showToast({ title: "收藏成功", icon: "none" });
      }
      await this.checkFavorite();
    } catch (error) {
      wx.showToast({ title: "操作失败，请稍后再试", icon: "none" });
    }
  },
  async reload() {
    await this.loadDetail();
    await this.checkFavorite();
  },
  goRead() {
    if (!requireAuth()) return;
    wx.navigateTo({ url: `/pages/reader/index?id=${this.data.id}` });
  }
});
