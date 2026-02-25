# Android 阅读器控制栏收起/展开功能设计

## 概述

为 Android 端阅读器添加点击内容区域切换顶部栏和底部栏显示/隐藏的功能，提供沉浸式阅读体验。

## 需求

用户在阅读时，希望能够隐藏顶部和底部的控制栏，获得更大的阅读区域和更专注的阅读体验。

## 设计方案

### 交互方式

- **触发方式**：点击阅读内容区域（文字部分）
- **行为**：切换顶部栏和底部栏的显示/隐藏状态
- **隐藏状态**：完全隐藏，不显示任何控制元素
- **兼容性**：不影响现有的长按段落评论功能

### 技术实现

#### 1. 状态管理

添加状态变量控制工具栏显示：

```kotlin
var showToolbars by remember { mutableStateOf(true) }
```

#### 2. 手势检测

在阅读内容区域添加点击检测：

- 使用 `Modifier.pointerInput(Unit) { detectTapGestures(...) }`
- `onTap` 事件：切换 `showToolbars` 状态
- `onLongPress` 事件：保持原有的评论功能

应用位置：
- HorizontalPager（翻页模式）
- LazyColumn（滚动模式）

#### 3. 动画效果

使用 `AnimatedVisibility` 实现平滑过渡：

**顶部栏动画：**
- enter: `slideInVertically(initialOffsetY = { -it }) + fadeIn()`
- exit: `slideOutVertically(targetOffsetY = { -it }) + fadeOut()`

**底部栏动画：**
- enter: `slideInVertically(initialOffsetY = { it }) + fadeIn()`
- exit: `slideOutVertically(targetOffsetY = { it }) + fadeOut()`

**动画参数：**
- 时长：200-300ms
- 缓动函数：FastOutSlowInEasing

#### 4. 布局调整

隐藏工具栏后，阅读内容区域自动扩展填充整个屏幕空间。

### 代码改动

**文件：** `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt`

**改动点：**

1. 添加 `showToolbars` 状态变量（第 40 行附近）
2. 顶部栏用 `AnimatedVisibility` 包裹（第 118-154 行）
3. 设置面板用 `AnimatedVisibility` 包裹（第 156-200 行）
4. 底部栏用 `AnimatedVisibility` 包裹（第 299-401 行）
5. HorizontalPager 添加点击检测（第 212 行）
6. LazyColumn 添加点击检测（第 254 行）

### 用户体验

**默认状态：** 工具栏显示

**隐藏后：**
- 阅读内容占据全屏
- 点击任意位置恢复工具栏显示
- 长按段落仍可打开评论

**优势：**
- 符合主流阅读器交互习惯（微信读书、Kindle 等）
- 无需学习成本
- 不影响现有功能

## 实现步骤

1. 添加状态变量和动画导入
2. 修改顶部栏和底部栏，添加 AnimatedVisibility 包裹
3. 在 HorizontalPager 和 LazyColumn 添加手势检测
4. 测试点击切换和长按评论功能
5. 调整动画参数优化体验

## 测试要点

- 点击内容区域能正常切换工具栏显示/隐藏
- 长按段落能正常打开评论面板
- 动画流畅无卡顿
- 隐藏后内容区域正确扩展
- 翻页模式和滚动模式都正常工作
- 章节切换后工具栏状态保持
