# Android阅读器自动翻页功能实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为Android阅读器添加自动翻页功能,当用户阅读到章节末尾时自动切换到下一章

**Architecture:** 使用LaunchedEffect监听翻页/滚动状态,当到达章节末尾时延迟500ms后自动调用nextChapter()。通过监听状态变化实现防抖,避免误触发。

**Tech Stack:** Kotlin, Jetpack Compose, Coroutines

---

## Task 1: 添加翻页模式自动翻页功能

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:93-99`

**Step 1: 在翻页模式区域后添加自动翻页LaunchedEffect**

在现有的 `LaunchedEffect(currentChapter?.id, pageCount, uiState.readerMode)` (第93-99行) 之后添加新的LaunchedEffect:

```kotlin
// Auto page turn for paged mode
LaunchedEffect(pagerState.currentPage, pageCount, uiState.currentChapterIndex, uiState.chapters.size, uiState.readerMode) {
    if (uiState.readerMode != ReaderMode.PAGED) return@LaunchedEffect

    val isLastPage = pagerState.currentPage == pageCount - 1
    val hasNextChapter = uiState.currentChapterIndex < uiState.chapters.lastIndex

    if (isLastPage && hasNextChapter && pageCount > 0) {
        delay(500)
        viewModel.nextChapter()
    }
}
```

**Step 2: 手动测试翻页模式自动翻页**

测试步骤:
1. 运行应用并打开一本有多章节的书
2. 切换到翻页模式
3. 翻到当前章节的最后一页
4. 等待500ms
5. 验证自动切换到下一章

预期结果: 在最后一页停留500ms后自动切换到下一章

**Step 3: 提交翻页模式自动翻页功能**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add auto page turn for paged mode"
```

---

## Task 2: 添加滚动模式自动翻页功能

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:93-99`

**Step 1: 在Task 1添加的LaunchedEffect之后添加滚动模式自动翻页**

在Task 1添加的LaunchedEffect之后继续添加:

```kotlin
// Auto page turn for scroll mode
LaunchedEffect(scrollListState.canScrollForward, scrollListState.isScrollInProgress, uiState.currentChapterIndex, uiState.chapters.size, uiState.readerMode) {
    if (uiState.readerMode != ReaderMode.SCROLL) return@LaunchedEffect

    val isAtBottom = !scrollListState.canScrollForward
    val hasStoppedScrolling = !scrollListState.isScrollInProgress
    val hasNextChapter = uiState.currentChapterIndex < uiState.chapters.lastIndex

    if (isAtBottom && hasStoppedScrolling && hasNextChapter) {
        delay(500)
        viewModel.nextChapter()
    }
}
```

**Step 2: 手动测试滚动模式自动翻页**

测试步骤:
1. 运行应用并打开一本有多章节的书
2. 切换到滚动模式
3. 滚动到当前章节底部
4. 停止滚动并等待500ms
5. 验证自动切换到下一章

预期结果: 滚动到底部并停止滚动500ms后自动切换到下一章

**Step 3: 提交滚动模式自动翻页功能**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add auto page turn for scroll mode"
```

---

## Task 3: 测试边界情况

**Files:**
- Test: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt`

**Step 1: 测试最后一章不触发自动翻页**

测试步骤:
1. 运行应用并打开一本书
2. 导航到最后一章
3. 翻页模式: 翻到最后一页,等待1秒
4. 滚动模式: 滚动到底部,等待1秒
5. 验证不会触发任何自动翻页

预期结果: 在最后一章末尾不会触发自动翻页

**Step 2: 测试防抖机制 - 翻页模式**

测试步骤:
1. 翻页模式下翻到最后一页
2. 在500ms内向前翻页
3. 验证不会触发自动翻页到下一章

预期结果: 向前翻页会取消自动翻页

**Step 3: 测试防抖机制 - 滚动模式**

测试步骤:
1. 滚动模式下滚动到底部
2. 在500ms内向上滚动
3. 验证不会触发自动翻页到下一章

预期结果: 向上滚动会取消自动翻页

**Step 4: 测试工具栏交互不影响自动翻页**

测试步骤:
1. 翻到最后一页或滚动到底部
2. 点击屏幕显示工具栏
3. 验证自动翻页逻辑仍然正常工作

预期结果: 显示工具栏不会影响自动翻页的触发

**Step 5: 测试章节切换后重置状态**

测试步骤:
1. 在某章节触发自动翻页
2. 切换到下一章后
3. 验证可以再次触发自动翻页

预期结果: 每次章节切换后自动翻页逻辑重新生效

---

## Task 4: 最终验证和文档更新

**Files:**
- Test: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt`

**Step 1: 完整功能测试**

测试场景:
1. 多章节书籍的连续阅读体验
2. 翻页模式和滚动模式切换
3. 短章节快速连续切换
4. 长章节正常阅读

预期结果: 所有场景下自动翻页功能正常工作

**Step 2: 性能验证**

验证要点:
1. 没有内存泄漏
2. LaunchedEffect在组件销毁时正确取消
3. 不会创建多余的协程

预期结果: 性能正常,无内存问题

**Step 3: 最终提交**

```bash
git add -A
git commit -m "feat(reader): complete auto page turn feature with debounce"
```

---

## 实现注意事项

1. **LaunchedEffect的key**: 确保监听所有相关状态变化,以便正确触发重组和取消之前的协程
2. **防抖机制**: 通过监听状态变化,LaunchedEffect会自动取消之前的协程并启动新的,实现防抖
3. **边界检查**: 始终检查是否有下一章,避免在最后一章触发自动翻页
4. **模式检查**: 在每个LaunchedEffect开始时检查当前模式,确保只在对应模式下执行
5. **性能**: LaunchedEffect会在key变化时自动清理,无需手动管理协程生命周期

## 测试清单

- [ ] 翻页模式: 最后一页自动翻到下一章
- [ ] 滚动模式: 滚动到底部自动翻到下一章
- [ ] 最后一章不触发自动翻页
- [ ] 翻页模式: 向前翻页取消自动翻页
- [ ] 滚动模式: 向上滚动取消自动翻页
- [ ] 章节切换后自动翻页逻辑重置
- [ ] 多章节连续阅读体验流畅
- [ ] 无内存泄漏和性能问题
