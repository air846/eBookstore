const TOKEN_KEY = "ebookstore_user_token";
const USER_KEY = "ebookstore_user_info";

function getToken() {
  return wx.getStorageSync(TOKEN_KEY) || "";
}

function hasToken() {
  return !!getToken();
}

function setToken(token) {
  wx.setStorageSync(TOKEN_KEY, token || "");
}

function getUser() {
  return wx.getStorageSync(USER_KEY) || null;
}

function setUser(user) {
  wx.setStorageSync(USER_KEY, user || null);
}

function clearAuth() {
  wx.removeStorageSync(TOKEN_KEY);
  wx.removeStorageSync(USER_KEY);
}

module.exports = {
  hasToken,
  getToken,
  setToken,
  getUser,
  setUser,
  clearAuth
};
