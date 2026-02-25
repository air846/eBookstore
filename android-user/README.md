# Android 用户端应用

这是云笺书馆的 Android 用户端应用，使用 Kotlin 和 Jetpack Compose 开发。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material 3
- **架构**: MVVM + Repository Pattern
- **依赖注入**: Hilt
- **网络请求**: Retrofit + OkHttp
- **图片加载**: Coil
- **本地存储**: DataStore
- **导航**: Navigation Compose

## 功能特性

### 用户认证
- 登录/注册
- Token 自动管理
- 用户信息管理

### 首页
- 轮播图展示
- 热门书籍推荐
- 继续阅读功能
- 阅读进度显示

### 书库
- 书籍列表展示
- 搜索功能
- 分类筛选
- 排序（最新上架、最多阅读）
- 分页加载

### 我的书架
- 收藏列表
- 阅读历史
- 搜索书籍
- 快速继续阅读

### 书籍详情
- 书籍信息展示
- 收藏功能
- 立即阅读

### 阅读器
- 章节导航
- 字体大小调节
- 夜间模式
- 阅读进度保存
- 段落评论功能
- 评论点赞/点踩
- 评论折叠/展开
- 催更功能

### 个人中心
- 个人资料编辑
- 密码修改
- 评论通知
- 阅读统计
- 退出登录

## 项目结构

```
app/src/main/java/com/ebookstore/user/
├── data/
│   ├── local/          # 本地存储
│   │   └── PreferencesManager.kt
│   ├── model/          # 数据模型
│   │   └── Models.kt
│   ├── remote/         # 网络请求
│   │   └── ApiService.kt
│   └── repository/     # 数据仓库
│       ├── AuthRepository.kt
│       └── BookRepository.kt
├── di/                 # 依赖注入
│   └── NetworkModule.kt
├── ui/
│   ├── navigation/     # 导航
│   │   └── AppNavigation.kt
│   ├── screens/        # 页面
│   │   ├── auth/       # 登录注册
│   │   ├── home/       # 首页
│   │   ├── booklist/   # 书库
│   │   ├── bookshelf/  # 书架
│   │   ├── bookdetail/ # 书籍详情
│   │   ├── reader/     # 阅读器
│   │   └── profile/    # 个人中心
│   └── theme/          # 主题
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── EBookstoreApp.kt    # Application
└── MainActivity.kt     # 主 Activity
```

## 构建和运行

### 前置要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK (API 34)
- Gradle 8.2

### 配置 API 地址

在项目根目录创建或编辑 `local.properties` 文件，配置 API 地址：

```properties
# 模拟器访问本机后端（默认）
API_BASE_URL=http://10.0.2.2:8080/api

# 真实设备访问本机后端（需要在同一局域网，替换为你的电脑 IP）
# API_BASE_URL=http://192.168.1.100:8080/api

# 生产环境
# API_BASE_URL=https://your-domain.com/api
```

**说明**：
- `10.0.2.2` 是 Android 模拟器访问宿主机的特殊 IP
- 真实设备需要使用电脑的局域网 IP 地址
- `local.properties` 文件不会被提交到 Git，可以安全配置本地环境

### 构建步骤

1. 克隆项目
2. 使用 Android Studio 打开 `android-user` 目录
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击 Run 按钮或使用命令：

```bash
./gradlew installDebug
```

## API 接口

应用连接到后端 API，默认地址为 `http://10.0.2.2:8080/api`。

主要接口包括：
- `/user/login` - 用户登录
- `/user/register` - 用户注册
- `/book/list` - 获取书籍列表
- `/book/detail/{id}` - 获取书籍详情
- `/book/{id}/chapters` - 获取章节列表
- `/book/favorite/list` - 获取收藏列表
- `/book/history/list` - 获取阅读历史
- 更多接口请参考 `ApiService.kt`

## 开发说明

### 添加新页面

1. 在 `ui/screens/` 下创建新的包
2. 创建 ViewModel 和 Screen
3. 在 `AppNavigation.kt` 中添加路由

### 修改主题

在 `ui/theme/` 目录下修改颜色、字体等主题配置。

### 网络请求

所有网络请求通过 Repository 层处理，使用 Retrofit 和协程。

## 注意事项

- 应用使用 Bearer Token 进行身份验证
- Token 自动存储在 DataStore 中
- 所有网络请求自动添加 Authorization 头
- 支持 Android 7.0 (API 24) 及以上版本

## 许可证

本项目仅供学习和研究使用。
