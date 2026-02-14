const { BASE_URL } = require("./config");
const { getToken, clearAuth } = require("./storage");

function joinUrl(base, path) {
  const normalizedBase = String(base || "").replace(/\/+$/, "");
  const normalizedPath = String(path || "").replace(/^\/+/, "");
  return `${normalizedBase}/${normalizedPath}`;
}

function cleanData(data) {
  if (!data || typeof data !== "object" || Array.isArray(data)) return data || {};
  return Object.keys(data).reduce((result, key) => {
    const value = data[key];
    if (value !== undefined && value !== null && value !== "") {
      result[key] = value;
    }
    return result;
  }, {});
}

function request(options) {
  const token = getToken();
  return new Promise((resolve, reject) => {
    wx.request({
      url: joinUrl(BASE_URL, options.url),
      method: options.method || "GET",
      data: cleanData(options.data || {}),
      header: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {})
      },
      success(res) {
        const payload = res.data || {};
        if (payload.code === 200) {
          resolve(payload.data);
          return;
        }

        if (payload.code === 401) {
          clearAuth();
          wx.showToast({ title: "登录已失效", icon: "none" });
          wx.redirectTo({ url: "/pages/login/index" });
        } else {
          wx.showToast({ title: payload.message || "请求失败", icon: "none" });
        }
        reject(payload);
      },
      fail(err) {
        wx.showToast({ title: "网络异常", icon: "none" });
        reject(err);
      }
    });
  });
}

module.exports = {
  request
};
