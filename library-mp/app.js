const { getToken, clearAuth } = require("./utils/storage");
const { getUserInfo } = require("./services/user");

App({
  globalData: {
    token: getToken(),
    user: null
  },
  onLaunch() {
    if (this.globalData.token) {
      this.refreshUser().catch(() => {
        clearAuth();
        this.globalData.token = "";
        this.globalData.user = null;
      });
    }
  },
  async refreshUser() {
    const res = await getUserInfo();
    this.globalData.user = res;
    return res;
  }
});
