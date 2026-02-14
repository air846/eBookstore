const { getCarouselList } = require("../../services/common");
const { getBookList } = require("../../services/book");

Page({
  data: {
    carouselList: [],
    hotBooks: [],
    newBooks: []
  },
  onLoad() {
    this.loadData();
  },
  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh());
  },
  async loadData() {
    const [carouselList, hotList, newList] = await Promise.all([
      getCarouselList(),
      getBookList({ page: 1, size: 6, sortBy: "visit_count", order: "desc" }),
      getBookList({ page: 1, size: 6, sortBy: "create_time", order: "desc" })
    ]);
    this.setData({
      carouselList: carouselList || [],
      hotBooks: (hotList && hotList.records) || [],
      newBooks: (newList && newList.records) || []
    });
  },
  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/book-detail/index?id=${id}` });
  }
});
