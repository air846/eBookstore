const { request } = require("../utils/request");

function getCarouselList() {
  return request({
    url: "/carousel/list"
  });
}

function getCategoryTree() {
  return request({
    url: "/category/tree"
  });
}

module.exports = {
  getCarouselList,
  getCategoryTree
};
