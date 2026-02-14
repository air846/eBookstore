const { getToken } = require("./storage");

function requireAuth() {
  if (getToken()) {
    return true;
  }
  wx.showToast({ title: "请先登录", icon: "none" });
  wx.navigateTo({ url: "/pages/login/index" });
  return false;
}

module.exports = {
  requireAuth
};
