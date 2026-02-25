# Android 阅读器底部栏被遮挡问题修复设计

## 问题描述

Android app 的阅读器底部栏（显示 "CHAPTER 01" 和页码信息的区域）被系统导航栏（虚拟按键）或屏幕边缘遮挡，导致内容显示不完整。该问题在翻页模式和滚动模式下都存在。

## 根本原因

应用未正确处理 Android 的 WindowInsets（窗口边距），导致 UI 内容延伸到系统栏区域，被系统导航栏遮挡。

## 解决方案

### 选定方案：使用 WindowInsets 适配系统栏

在 `ReaderScreen.kt` 的 Scaffold 组件中添加 `contentWindowInsets` 参数，让 Jetpack Compose 自动处理系统栏的边距。

**修改位置：** `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:100`

**修改内容：**
```kotlin
Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = backgroundColor,
    contentWindowInsets = WindowInsets.systemBars  // 添加此行
)
```

**需要导入：**
```kotlin
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
```

### 方案优势

1. **简单直接** - 只需添加一行代码
2. **自动适配** - 自动处理各种设备（有/无虚拟按键、刘海屏、折叠屏等）
3. **符合规范** - Material3 推荐的标准做法
4. **无副作用** - 不影响现有布局逻辑

### 备选方案（如需要）

**方案 2：手动添加底部 padding**
在底部栏 Column 中使用 `Modifier.padding(WindowInsets.navigationBars.asPaddingValues())`

**方案 3：全屏模式 + 手动处理**
使用 `WindowCompat.setDecorFitsSystemWindows(window, false)` 并手动处理所有边距

## 实现步骤

1. 在 ReaderScreen.kt 添加必要的 import 语句
2. 在 Scaffold 中添加 `contentWindowInsets = WindowInsets.systemBars`
3. 测试验证：
   - 在有虚拟按键的设备上测试
   - 在全面屏手势导航的设备上测试
   - 验证翻页模式和滚动模式
   - 验证日间/夜间模式

## 预期效果

底部栏内容将自动添加适当的 padding，完整显示在系统导航栏上方，不再被遮挡。
