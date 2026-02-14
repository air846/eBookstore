const { login, miniLogin, getUserInfo } = require("../../services/user");
const { setToken, setUser } = require("../../utils/storage");

Page({
  data: {
    submitting: false,
    form: {
      username: "reader",
      password: "123456"
    }
  },
  onUsernameInput(e) {
    this.setData({
      "form.username": e.detail.value
    });
  },
  onPasswordInput(e) {
    this.setData({
      "form.password": e.detail.value
    });
  },
  async handleLogin() {
    const { username, password } = this.data.form;
    if (!username || !password) {
      wx.showToast({ title: "请输入用户名和密码", icon: "none" });
      return;
    }
    this.setData({ submitting: true });
    try {
      const res = await login({ username, password });
      setToken(res.token || "");
      const user = await getUserInfo();
      setUser(user);
      const app = getApp();
      app.globalData.token = res.token || "";
      app.globalData.user = user;
      wx.switchTab({ url: "/pages/home/index" });
    } finally {
      this.setData({ submitting: false });
    }
  },
  async handleMiniLogin() {
    this.setData({ submitting: true });
    try {
      const code = await this.getWxCode();
      const profile = await this.getWxProfileSafe();
      const res = await miniLogin({
        code,
        nickname: profile.nickName || "微信读者",
        avatar: profile.avatarUrl || ""
      });
      setToken(res.token || "");
      const user = await getUserInfo();
      setUser(user);
      const app = getApp();
      app.globalData.token = res.token || "";
      app.globalData.user = user;
      wx.switchTab({ url: "/pages/home/index" });
    } catch (error) {
      const errMsg = (error && error.errMsg) || "";
      const bizMsg = (error && error.message) || "";
      if (String(errMsg).includes("cancel")) {
        wx.showToast({ title: "已取消微信授权", icon: "none" });
      } else {
        wx.showToast({ title: bizMsg || "微信登录失败", icon: "none" });
      }
    } finally {
      this.setData({ submitting: false });
    }
  },
  getWxCode() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            resolve(res.code);
            return;
          }
          reject(new Error("wx.login未返回code"));
        },
        fail: reject
      });
    });
  },
  getWxProfileSafe() {
    return new Promise((resolve) => {
      wx.getUserProfile({
        desc: "用于完善账号资料",
        success: (res) => resolve(res.userInfo || {}),
        fail: () => resolve({})
      });
    });
  }
});
