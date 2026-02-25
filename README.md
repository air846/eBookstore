# eBookstore

一个前后端分离的电子书阅读平台，包含：

- `backend`：Spring Boot 后端（统一 API）
- `library-user`：用户端 Web（Vue 3 + Element Plus，移动端 App 化适配）
- `library-admin`：管理端 Web（Vue 3 + Element Plus）
- `library-mp`：微信小程序端（MVP）
- `android-user`：Android 原生用户端（Jetpack Compose + Kotlin）

---

## 技术栈

### 后端（`backend`）
- Spring Boot `3.3.2`
- MyBatis-Plus `3.5.7`
- MySQL `8.x`
- JWT（`jjwt 0.12.6`）
- SpringDoc OpenAPI（Swagger）
- Apache POI（报表导出）

### 前端（`library-user` / `library-admin`）
- Vue `3` + TypeScript
- Vite `5`
- Element Plus
- Pinia
- Vue Router
- Axios
- ECharts

### 小程序（`library-mp`）
- 微信小程序原生框架
- 复用后端 `/api` 接口

### Android 端（`android-user`）
- Kotlin + Jetpack Compose
- Material Design 3
- Retrofit + OkHttp（网络请求）
- Hilt（依赖注入）
- Coil（图片加载）
- DataStore（本地存储）
- Navigation Compose（导航）

---

## 主要功能

### 用户端（`library-user`）
- 登录 / 注册 / 个人资料维护
- 首页推荐、热门书籍、继续阅读
- 书库检索（分类、关键词、排序、分页）
- 书籍详情、收藏、书架管理
- 阅读器（章节切换、阅读进度、段评互动、催更）
- 个人中心（通知、阅读偏好、历史记录）

### Android 端（`android-user`）
- 用户登录 / 注册
- 首页推荐、分类浏览
- 书籍搜索、详情查看
- 书架管理（收藏、历史记录）
- 阅读器（章节切换、阅读进度保存、自动恢复上次阅读位置）
- 个人中心（用户信息、退出登录）

### 管理端（`library-admin`）
- 管理员登录
- 控制台（用户、活跃、库存、服务器负载、新增书籍）
- 书籍管理（上下架、章节管理、TXT 导章）
- 分类管理、用户管理、互动管理
- 系统设置（轮播图、服务器负载）
- 控制台报表导出（Excel）

### 后端 API（`backend`）
- 用户：`/api/user/**`
- 图书：`/api/book/**`
- 分类/轮播：`/api/category/**`、`/api/carousel/**`
- 管理端：`/api/admin/**`

---

## 目录结构

```text
.
├─ backend/                  # Spring Boot 后端
├─ library-user/             # 用户端前端
├─ library-admin/            # 管理端前端
├─ library-mp/               # 微信小程序端
├─ android-user/             # Android 原生用户端
└─ doc/
   ├─ api/接口清单.md
   ├─ sql/init.sql
   ├─ sql/fix_default_password.sql
   └─ 联调与运行说明.md
```

---

## 环境要求

- JDK `17+`
- Maven `3.9+`
- Node.js `18+`（建议 LTS）
- npm `9+`
- MySQL `8+`
- Android Studio `Hedgehog+`（用于 Android 端开发）
- Android SDK `34+`

---

## 快速开始

### 1) 初始化数据库

导入初始化脚本：

- `doc/sql/init.sql`

可选修复脚本：

- `doc/sql/fix_default_password.sql`
- `doc/sql/add_union_id.sql`

默认数据库配置（见 `backend/src/main/resources/application.yml`）：

- 库名：`ebookstore`
- 用户：`root`
- 密码：`123456`
- 端口：`3306`

### 2) 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认地址：`http://localhost:8080`

Swagger：`http://localhost:8080/swagger-ui.html`

### 3) 启动用户端

```bash
cd library-user
npm install
npm run dev
```

默认地址：`http://localhost:5173`

### 4) 启动管理端

```bash
cd library-admin
npm install
npm run dev
```

默认地址：`http://localhost:5174`

### 5) 启动小程序（可选）

1. 微信开发者工具导入 `library-mp`
2. 修改 `library-mp/utils/config.js` 中 `BASE_URL`
3. 确保后端可访问

### 6) 运行 Android 端（可选）

1. 使用 Android Studio 打开 `android-user` 目录
2. 等待 Gradle 同步完成
3. 修改 `app/src/main/java/com/example/library/data/remote/ApiConfig.kt` 中的 `BASE_URL`（如需）
4. 连接 Android 设备或启动模拟器
5. 点击 Run 按钮运行应用

---

## 默认账号

- 管理员：`admin / 123456`
- 普通用户：`reader / 123456`

---

## 构建命令

### 用户端

```bash
cd library-user
npm run build
npm run preview
```

### 管理端

```bash
cd library-admin
npm run build
npm run preview
```

### 后端

```bash
cd backend
mvn clean package
```

### Android 端

```bash
cd android-user
./gradlew assembleRelease
# APK 输出路径：app/build/outputs/apk/release/
```

---

## 常见问题

### 1. 控制台“服务器负载”不显示

控制台会尝试读取两路接口：

- `/api/admin/dashboard`
- `/api/admin/system/server-load`

请先在“系统设置”确认负载接口有返回值。

### 2. 导出报表提示失败，但后端似乎已导出

如果浏览器控制台出现 `ERR_BLOCKED_BY_CLIENT`，通常是广告拦截插件拦截了下载请求。

处理方式：
- 关闭 AdBlock/uBlock/AdGuard 对 `localhost` 的拦截
- 或将 `localhost:5174`、`localhost:8080` 加入白名单

### 3. 小程序真机访问失败

`localhost` 在手机上指向手机本机。请改用电脑局域网 IP 或可访问域名。

### 4. Android 端网络请求失败

确保：
- 后端服务已启动
- `ApiConfig.kt` 中的 `BASE_URL` 配置正确
- 如使用模拟器，`localhost` 应改为 `10.0.2.2`
- 如使用真机，需使用局域网 IP 地址

---

## 配置说明

### 后端关键配置（`backend/src/main/resources/application.yml`）

- `server.port`：后端端口
- `spring.datasource.*`：数据库连接
- `jwt.secret`、`jwt.expire-seconds`：JWT 配置
- `wechat.miniapp.appid/secret`：小程序登录配置

### 前端 API 地址

- 用户端：`library-user/src/utils/request.ts`
- 管理端：`library-admin/src/utils/request.ts`
- Android 端：`android-user/app/src/main/java/com/example/library/data/remote/ApiConfig.kt`

默认都指向：`http://localhost:8080/api`

---

## 相关文档

- 接口清单：`doc/api/接口清单.md`
- SQL 初始化：`doc/sql/init.sql`
- 联调说明：`doc/联调与运行说明.md`
- 小程序说明：`library-mp/README.md`
- Android 端说明：`android-user/README.md`

---

## License

仅用于学习与内部开发演示。
