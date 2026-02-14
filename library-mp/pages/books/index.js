const { getCategoryTree } = require("../../services/common");
const { getBookList } = require("../../services/book");

function flatten(nodes, result = [], level = 0) {
  (nodes || []).forEach((item) => {
    result.push({
      id: item.id,
      name: item.name,
      level,
      label: `${level > 0 ? `${"—".repeat(level)} ` : ""}${item.name}`
    });
    if (item.children && item.children.length) {
      flatten(item.children, result, level + 1);
    }
  });
  return result;
}

Page({
  data: {
    categories: [],
    books: [],
    total: 0,
    hasMore: true,
    loading: false,
    firstLoading: true,
    errorText: "",
    query: {
      page: 1,
      size: 10,
      keyword: "",
      categoryId: 0
    }
  },
  async onLoad() {
    await this.loadCategories();
    await this.loadBooks(true);
  },
  onReachBottom() {
    this.loadMore();
  },
  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh());
  },
  async loadCategories() {
    try {
      const tree = await getCategoryTree();
      this.setData({ categories: flatten(tree || []) });
    } catch (error) {
      wx.showToast({ title: "分类加载失败", icon: "none" });
    }
  },
  async loadBooks(reset) {
    if (this.data.loading) return;
    const query = { ...this.data.query };
    const payload = {
      page: query.page,
      size: query.size
    };
    if (query.keyword) payload.keyword = query.keyword;
    if (query.categoryId) payload.categoryId = query.categoryId;
    this.setData({ loading: true, errorText: "" });
    try {
      const res = await getBookList(payload);
      const nextRecords = (res && res.records) || [];
      const merged = reset ? nextRecords : this.data.books.concat(nextRecords);
      const total = (res && res.total) || 0;
      this.setData({
        books: merged,
        total,
        hasMore: merged.length < total
      });
    } catch (error) {
      this.setData({ errorText: "加载失败，请点击重试" });
    } finally {
      this.setData({ loading: false, firstLoading: false });
    }
  },
  onKeywordInput(e) {
    this.setData({ "query.keyword": e.detail.value });
  },
  onSearchConfirm() {
    this.reload();
  },
  async selectCategory(e) {
    const id = Number(e.currentTarget.dataset.id || 0);
    if (id === this.data.query.categoryId) return;
    this.setData({
      "query.categoryId": id,
      "query.page": 1
    });
    await this.loadBooks(true);
  },
  async reload() {
    this.setData({ "query.page": 1 });
    await this.loadBooks(true);
  },
  async loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    this.setData({ "query.page": this.data.query.page + 1 });
    await this.loadBooks(false);
  },
  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/book-detail/index?id=${id}` });
  }
});
