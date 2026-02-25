# Android阅读器自动翻页功能设计

## 概述

为Android阅读器添加自动翻页功能,当用户阅读到章节末尾时,自动切换到下一章,提升阅读连贯性。

## 需求

- 翻页模式:用户翻到当前章节最后一页时,自动切换到下一章
- 滚动模式:用户滚动到当前章节底部时,自动切换到下一章
- 添加防抖延迟(500ms),避免误触发
- 用户操作可取消自动翻页

## 设计方案

### 1. 核心功能

**翻页模式(PAGED):**
- 监听 `HorizontalPager` 的 `currentPage` 状态
- 当用户翻到当前章节的最后一页时,等待500ms
- 如果500ms内用户没有向前翻页或其他操作,自动切换到下一章
- 如果已经是最后一章,不触发自动翻页

**滚动模式(SCROLL):**
- 监听 `LazyColumn` 的滚动状态
- 当用户滚动到当前章节底部时,等待500ms
- 如果500ms内用户没有向上滚动,自动切换到下一章
- 如果已经是最后一章,不触发自动翻页

### 2. 技术实现

**状态管理:**
- 使用 `LaunchedEffect` 监听页面/滚动状态变化
- 使用 `kotlinx.coroutines.delay` 实现500ms延迟
- 使用 `Job` 来管理延迟任务,支持取消

**防抖逻辑:**
- 翻页模式:当 `currentPage` 变化时,取消之前的延迟任务
- 滚动模式:当滚动方向改变或位置变化时,取消之前的延迟任务
- 用户点击屏幕、打开工具栏等操作也会取消自动翻页

**边界处理:**
- 检查是否有下一章 (`currentChapterIndex < chapters.lastIndex`)
- 翻页模式:检查是否在最后一页 (`currentPage == pageCount - 1`)
- 滚动模式:检查是否滚动到底部 (`!canScrollForward`)

### 3. 用户体验优化

**取消自动翻页的场景:**
- 用户向前翻页/向上滚动
- 用户点击屏幕(触发工具栏显示)
- 用户打开章节目录、设置面板、评论抽屉
- 用户切换阅读模式

**视觉反馈:**
- 保持现有的章节切换动画
- 自动翻页时使用相同的 `nextChapter()` 方法,确保体验一致
- 无需额外的加载提示,因为切换是即时的

**性能考虑:**
- 延迟任务在组件销毁时自动取消
- 不会创建多余的协程或内存泄漏
- 监听器只在必要时触发

### 4. 代码改动范围

**ReaderScreen.kt 改动:**
- 在翻页模式的 `LaunchedEffect` 中添加自动翻页逻辑
  - 监听 `pagerState.currentPage` 和 `pageCount`
  - 当在最后一页且有下一章时,延迟500ms后自动调用 `viewModel.nextChapter()`

- 在滚动模式的 `LaunchedEffect` 中添加自动翻页逻辑
  - 监听 `scrollListState.canScrollForward` 和 `scrollListState.isScrollInProgress`
  - 当滚动到底部且停止滚动时,延迟500ms后自动调用 `viewModel.nextChapter()`

- 在用户交互时取消自动翻页
  - 点击屏幕、打开抽屉、切换模式时取消延迟任务

**ReaderViewModel.kt 改动:**
- 无需改动,现有的 `nextChapter()` 方法已经满足需求

### 5. 测试要点

- 翻页模式:翻到最后一页,等待500ms,验证自动切换
- 滚动模式:滚动到底部,等待500ms,验证自动切换
- 在延迟期间向前翻页/向上滚动,验证取消自动翻页
- 最后一章不触发自动翻页
- 打开工具栏/抽屉时取消自动翻页

## 实现细节

### 翻页模式自动翻页

```kotlin
LaunchedEffect(pagerState.currentPage, pageCount, uiState.currentChapterIndex, uiState.chapters.size) {
    val isLastPage = pagerState.currentPage == pageCount - 1
    val hasNextChapter = uiState.currentChapterIndex < uiState.chapters.lastIndex

    if (isLastPage && hasNextChapter) {
        delay(500)
        viewModel.nextChapter()
    }
}
```

### 滚动模式自动翻页

```kotlin
LaunchedEffect(scrollListState.canScrollForward, scrollListState.isScrollInProgress, uiState.currentChapterIndex, uiState.chapters.size) {
    val isAtBottom = !scrollListState.canScrollForward
    val hasStoppedScrolling = !scrollListState.isScrollInProgress
    val hasNextChapter = uiState.currentChapterIndex < uiState.chapters.lastIndex

    if (isAtBottom && hasStoppedScrolling && hasNextChapter) {
        delay(500)
        viewModel.nextChapter()
    }
}
```

## 风险与限制

- 用户可能不习惯自动翻页,但由于有500ms延迟和多种取消机制,应该不会造成困扰
- 如果章节内容很短,可能会快速连续切换多个章节,但这是符合预期的行为
- 未来可考虑添加设置选项,让用户选择是否启用自动翻页

## 时间线

设计完成后,将创建实现计划并开始开发。
