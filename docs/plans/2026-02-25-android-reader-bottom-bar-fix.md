# Android 阅读器底部栏遮挡修复实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复 Android 阅读器底部栏被系统导航栏遮挡的问题

**Architecture:** 在 ReaderScreen 的 Scaffold 组件中添加 WindowInsets 配置，让 Compose 自动处理系统栏边距，确保内容不被系统导航栏遮挡

**Tech Stack:** Kotlin, Jetpack Compose, Material3, WindowInsets API

---

## Task 1: 添加 WindowInsets 导入

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:1-28`

**Step 1: 添加必要的 import 语句**

在文件顶部的 import 区域添加：

```kotlin
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
```

具体位置：在现有的 `androidx.compose.foundation.layout.*` 导入之后添加这两行。

**Step 2: 验证导入**

检查文件确保导入语句正确添加，没有重复或冲突。

**Step 3: Commit**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat: add WindowInsets imports for system bar handling"
```

---

## Task 2: 配置 Scaffold 的 WindowInsets

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:100-103`

**Step 1: 修改 Scaffold 配置**

找到第 100 行的 Scaffold 组件，修改为：

```kotlin
Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = backgroundColor,
    contentWindowInsets = WindowInsets.systemBars
) { padding ->
```

**Step 2: 验证语法**

确保代码格式正确，括号匹配，参数顺序合理。

**Step 3: Commit**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "fix: add contentWindowInsets to prevent bottom bar occlusion"
```

---

## Task 3: 构建和测试验证

**Files:**
- Test: Android app 在真机或模拟器上运行

**Step 1: 清理并构建项目**

```bash
cd android-user
./gradlew clean assembleDebug
```

Expected: BUILD SUCCESSFUL

**Step 2: 安装到设备并测试**

测试场景：
1. 打开任意书籍的阅读器
2. 检查底部栏（CHAPTER 和页码区域）是否完整显示
3. 切换到翻页模式，验证底部栏不被遮挡
4. 切换到滚动模式，验证底部栏不被遮挡
5. 切换日间/夜间模式，验证底部栏始终可见
6. 在有虚拟按键的设备上测试
7. 在全面屏手势导航的设备上测试

Expected: 底部栏内容完整显示，不被系统导航栏遮挡

**Step 3: 记录测试结果**

如果测试通过，继续下一步。如果仍有问题，考虑使用备选方案（手动添加 padding）。

**Step 4: 最终 Commit（如有额外调整）**

```bash
git add .
git commit -m "test: verify bottom bar fix on multiple devices"
```

---

## 验收标准

- [ ] 底部栏在有虚拟按键的设备上完整显示
- [ ] 底部栏在全面屏手势导航的设备上完整显示
- [ ] 翻页模式下底部栏不被遮挡
- [ ] 滚动模式下底部栏不被遮挡
- [ ] 日间和夜间模式下都正常显示
- [ ] 代码已提交到 git

## 备选方案（如果主方案不够）

如果 `contentWindowInsets = WindowInsets.systemBars` 不能完全解决问题，可以尝试：

**方案 2：在底部栏 Surface 中手动添加 padding**

修改第 290-293 行的底部栏 Surface：

```kotlin
Surface(
    tonalElevation = 2.dp,
    color = backgroundColor.copy(alpha = 0.9f),
    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
) {
```

需要额外导入：
```kotlin
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
```
