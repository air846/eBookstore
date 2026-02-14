# library-mp

eBookstore 微信小程序端（MVP）。

## 当前已实现

- 账号密码登录（复用 `/api/user/login`）
- 微信一键登录（复用 `/api/user/mp/login`，通过 `wx.login` code 换取 unionId/openid）
- 首页：轮播图 + 热门/最新图书
- 书库：分类筛选、关键词搜索、分页加载
- 详情：图书详情、收藏/取消收藏
- 阅读：章节目录、正文阅读、阅读进度上报
- 书架：收藏列表
- 我的：用户信息、阅读历史、退出登录

## 目录结构

```text
library-mp/
├─ app.js
├─ app.json
├─ app.wxss
├─ project.config.json
├─ sitemap.json
├─ services/
├─ utils/
└─ pages/
```

## 运行方式

1. 使用微信开发者工具导入 `library-mp` 目录
2. 修改 `utils/config.js` 中 `BASE_URL` 为你的后端地址
3. 启动后端（默认 `http://localhost:8080`）
4. 小程序中使用默认账号登录：
   - 管理员：`admin / 123456`（仅用于测试登录）
   - 用户：`reader / 123456`

## 注意事项

- 真机调试时，`localhost` 指向手机本机，需改为电脑局域网 IP 或可访问域名
- 发布时需在微信后台配置合法 request 域名（HTTPS）
- 若接入真实微信 UnionID，请在后端将 `unionId` 替换为微信开放接口返回值
  - 后端需配置环境变量：
    - `WECHAT_MINIAPP_APPID`
    - `WECHAT_MINIAPP_SECRET`
