# eBookstore 部署到 Vercel 指南

本文档用于部署前端项目到 Vercel：

- `library-user`（用户端）
- `library-admin`（管理端）

> 注意：`backend` 是 Spring Boot 服务，不建议部署在 Vercel。请先将后端部署到可公网访问的服务器（如云主机、Railway、Render、宝塔、Docker 主机等）。

---

## 1. 部署前准备

## 1.1 后端先上线

确保后端 API 已经可公网访问，例如：

- `https://api.yourdomain.com/api`

并且 Swagger 可访问（可选）：

- `https://api.yourdomain.com/swagger-ui.html`

## 1.2 CORS 配置

后端需要允许你的 Vercel 域名跨域，例如：

- `https://your-user-app.vercel.app`
- `https://your-admin-app.vercel.app`

当前项目里后端已开启通配 CORS（`backend/src/main/java/com/bookstore/config/CorsConfig.java`），通常可直接联通。

---

## 2. 前端 API 地址改造（强烈建议）

当前两个前端默认写死了本地地址：

- `library-user/src/utils/request.ts`
- `library-admin/src/utils/request.ts`

建议改为环境变量：

```ts
baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api"
```

然后在 Vercel 项目里配置：

- Key: `VITE_API_BASE_URL`
- Value: `https://api.yourdomain.com/api`

这样可以在本地和线上复用同一套代码。

---

## 3. 部署 `library-user`

## 3.1 在 Vercel 创建项目

1. 登录 Vercel，点击 **Add New Project**
2. 选择仓库：`air846/eBookstore`
3. 配置：
   - **Root Directory**: `library-user`
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Environment Variables 添加：
   - `VITE_API_BASE_URL=https://api.yourdomain.com/api`
5. 点击 Deploy

## 3.2 SPA 路由刷新 404 处理（建议）

在 `library-user` 目录新增 `vercel.json`：

```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

用于解决 `Vue Router` 历史模式刷新 404 问题。

---

## 4. 部署 `library-admin`

流程与用户端一致，再新建一个 Vercel 项目：

- **Root Directory**: `library-admin`
- **Framework Preset**: `Vite`
- **Build Command**: `npm run build`
- **Output Directory**: `dist`
- 环境变量：`VITE_API_BASE_URL=https://api.yourdomain.com/api`

同样建议在 `library-admin` 目录新增 `vercel.json`：

```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

---

## 5. 可选：使用 Vercel CLI 部署

在项目根目录执行：

```bash
npm i -g vercel
```

部署用户端：

```bash
cd library-user
vercel
```

部署管理端：

```bash
cd library-admin
vercel
```

首次会交互式配置项目；后续可使用 `vercel --prod` 发布正式版本。

---

## 6. 上线后检查清单

- 用户端能正常登录、浏览书库、阅读
- 管理端能登录、打开控制台、导出报表
- 控制台“服务器负载”能显示
- 浏览器控制台无跨域错误
- 导出报表若出现 `ERR_BLOCKED_BY_CLIENT`，检查广告拦截插件

---

## 7. 常见问题

### Q1：页面打开了，但接口全是 localhost:8080

说明前端仍在使用硬编码地址。请改用 `VITE_API_BASE_URL` 并在 Vercel 配置环境变量。

### Q2：刷新子路由 404

请添加 `vercel.json` rewrite 到 `index.html`（见上文）。

### Q3：导出报表失败

先看浏览器 Network：

- 若 `ERR_BLOCKED_BY_CLIENT`：是浏览器插件拦截
- 若 401：管理员 token 无效
- 若 500：后端导出接口异常，查看后端日志

---

## 8. 推荐上线架构

- 前端：Vercel（`library-user` + `library-admin`）
- 后端：云主机 / 容器平台（Java 17 + MySQL）
- 域名：
  - `app.yourdomain.com` -> 用户端
  - `admin.yourdomain.com` -> 管理端
  - `api.yourdomain.com` -> 后端 API
