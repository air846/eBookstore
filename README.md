# eBookstore

`eBookstore` 是一个前后端分离的电子书阅读与管理系统，包含：

- `backend`：Spring Boot 后端服务（用户端 + 管理端 API）
- `library-user`：Vue 3 用户端（读者使用）
- `library-admin`：Vue 3 管理端（管理员使用）

## 技术栈

- 后端：Spring Boot 3.3.2、MyBatis-Plus 3.5.7、MySQL 8、JWT、SpringDoc OpenAPI
- 前端：Vue 3、TypeScript、Vite 5、Element Plus、Pinia、Vue Router、Axios、ECharts

## 目录结构

```text
eBookstore/
├─ backend/                 # Spring Boot 后端
├─ library-user/            # 用户端前端
├─ library-admin/           # 管理端前端
└─ doc/
   ├─ api/接口清单.md
   ├─ sql/init.sql
   ├─ sql/fix_default_password.sql
   ├─ 开发计划.md
   ├─ 开发提示词.md
   └─ 联调与运行说明.md
```

## 功能概览

### 用户端（library-user）

- 用户注册、登录、个人信息
- 首页推荐、图书列表、图书详情
- 在线阅读（章节）
- 图书收藏、阅读历史
- 段落评论、点赞/点踩、隐藏评论
- 催更、互动通知

### 管理端（library-admin）

- 管理员登录、仪表盘统计
- 图书管理（含章节 CRUD、上下架）
- 分类管理、用户管理
- 轮播图管理
- 互动管理（评论管理、催更统计）

### 后端接口（backend）

- 用户 API：`/api/user/**`
- 图书/阅读 API：`/api/book/**`
- 分类与轮播图 API：`/api/category/**`、`/api/carousel/**`
- 管理 API：`/api/admin/**`

## 快速开始

### 1) 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+（建议 LTS）
- MySQL 8+

### 2) 初始化数据库

1. 创建数据库并导入脚本：
   - `doc/sql/init.sql`
2. 如需修复默认密码，可执行：
   - `doc/sql/fix_default_password.sql`
3. 按需修改数据库配置：
   - `backend/src/main/resources/application.yml`

默认配置：
- 数据库：`ebookstore`
- 用户名：`root`
- 密码：`123456`
- 后端端口：`8080`

### 3) 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 4) 启动用户端

```bash
cd library-user
npm install
npm run dev
```

默认地址：`http://localhost:5173`

### 5) 启动管理端

```bash
cd library-admin
npm install
npm run dev
```

默认地址：`http://localhost:5174`

### 6) 默认账号

- 管理员：`admin / 123456`
- 普通用户：`reader / 123456`

## 常用地址

- 用户端：`http://localhost:5173`
- 管理端：`http://localhost:5174`
- 后端 API：`http://localhost:8080/api`
- Swagger UI：`http://localhost:8080/swagger-ui.html`

## 相关文档

- 接口清单：`doc/api/接口清单.md`
- 联调与运行说明：`doc/联调与运行说明.md`
- 开发计划：`doc/开发计划.md`
- 开发提示词：`doc/开发提示词.md`
