# Android 阅读器控制栏切换功能实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 Android 阅读器添加点击内容区域切换顶部栏和底部栏显示/隐藏的功能，实现沉浸式阅读体验。

**Architecture:** 使用 Jetpack Compose 的 AnimatedVisibility 和手势检测实现。添加状态变量控制工具栏显示，在阅读内容区域添加点击检测切换状态，使用动画实现平滑过渡。

**Tech Stack:** Kotlin, Jetpack Compose, AnimatedVisibility, detectTapGestures

---

## Task 1: 添加状态变量和动画导入

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:1-31`

**Step 1: 添加动画相关导入**

在文件顶部的导入区域添加：

```kotlin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
```

位置：在现有导入语句之后，第 31 行之前。

**Step 2: 添加状态变量**

在 ReaderScreen 函数内部，第 46 行（`var showCommentDrawer by remember { mutableStateOf(false) }` 之后）添加：

```kotlin
var showToolbars by remember { mutableStateOf(true) }
```

**Step 3: 验证编译**

```bash
cd android-user
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

**Step 4: 提交更改**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add toolbar visibility state and animation imports"
```

---

## Task 2: 包裹顶部栏和设置面板

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:118-200`

**Step 1: 用 AnimatedVisibility 包裹顶部栏**

找到第 118-154 行的顶部栏 Surface，将其包裹在 AnimatedVisibility 中：

```kotlin
// Top Bar
AnimatedVisibility(
    visible = showToolbars,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
) {
    Surface(
        tonalElevation = 2.dp,
        color = backgroundColor.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = textColor
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(Icons.Default.TextFields, contentDescription = "字体", tint = textColor)
                }
                IconButton(onClick = { viewModel.toggleDarkMode() }) {
                    Icon(
                        if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "夜间模式",
                        tint = textColor
                    )
                }
                IconButton(onClick = { showChapterDrawer = true }) {
                    Icon(Icons.Default.List, contentDescription = "章节", tint = textColor)
                }
            }
        }
    }
}
```

**Step 2: 用 AnimatedVisibility 包裹设置面板**

找到第 156-200 行的设置面板，将其包裹：

```kotlin
// Settings Panel
AnimatedVisibility(
    visible = showSettings && showToolbars,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
) {
    Surface(
        tonalElevation = 1.dp,
        color = backgroundColor.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("字号", color = textColor)
                Slider(
                    value = uiState.fontSize.toFloat(),
                    onValueChange = { viewModel.setFontSize(it.toInt()) },
                    valueRange = 14f..28f,
                    modifier = Modifier.width(180.dp)
                )
                Text("${uiState.fontSize}sp", color = textColor)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.readerMode == ReaderMode.PAGED,
                    onClick = { viewModel.setReaderMode(ReaderMode.PAGED) },
                    label = { Text("左右翻页") }
                )
                FilterChip(
                    selected = uiState.readerMode == ReaderMode.SCROLL,
                    onClick = { viewModel.setReaderMode(ReaderMode.SCROLL) },
                    label = { Text("上下滚动") }
                )
            }
        }
    }
}
```

**Step 3: 验证编译**

```bash
cd android-user
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

**Step 4: 提交更改**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add animated visibility to top bar and settings panel"
```

---

## Task 3: 包裹底部栏

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:299-401`

**Step 1: 用 AnimatedVisibility 包裹底部栏**

找到第 299-401 行的底部栏 Surface，将其包裹：

```kotlin
// Bottom Bar
AnimatedVisibility(
    visible = showToolbars,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
) {
    Surface(
        tonalElevation = 2.dp,
        color = backgroundColor.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHAPTER ${(uiState.currentChapterIndex + 1).toString().padStart(2, '0')}",
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
                Text(
                    text = if (uiState.readerMode == ReaderMode.PAGED) {
                        "PAGE $currentPageDisplay/$pageCount"
                    } else {
                        "滚动模式"
                    },
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            LinearProgressIndicator(
                progress = { readingProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.readerMode == ReaderMode.PAGED) {
                    OutlinedButton(
                        onClick = { previousPageOrChapter() },
                        enabled = canGoPrevPage,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("上一页")
                    }
                    OutlinedButton(
                        onClick = { nextPageOrChapter() },
                        enabled = canGoNextPage,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("下一页")
                    }
                    if (!canGoNextPage) {
                        OutlinedButton(
                            onClick = {
                                viewModel.urgeUpdate {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("催更成功，已通知管理员")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("催更")
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.prevChapter() },
                        enabled = canGoPrevChapter,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("上一章")
                    }
                    OutlinedButton(
                        onClick = { viewModel.nextChapter() },
                        enabled = canGoNextChapter,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("下一章")
                    }
                    if (!canGoNextChapter) {
                        OutlinedButton(
                            onClick = {
                                viewModel.urgeUpdate {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("催更成功，已通知管理员")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("催更")
                        }
                    }
                }
            }
        }
    }
}
```

**Step 2: 验证编译**

```bash
cd android-user
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

**Step 3: 提交更改**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add animated visibility to bottom bar"
```

---

## Task 4: 为翻页模式添加点击检测

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:211-252`

**Step 1: 在 HorizontalPager 外层添加点击检测**

找到第 211 行的 `if (uiState.readerMode == ReaderMode.PAGED) {`，修改 HorizontalPager：

```kotlin
if (uiState.readerMode == ReaderMode.PAGED) {
    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .weight(1f)
            .padding(24.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showToolbars = !showToolbars
                    }
                )
            }
    ) { pageIndex ->
        val pageParagraphs = pagedParagraphs.getOrElse(pageIndex) { emptyList() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // 只在第一页显示章节标题
            if (pageIndex == 0) {
                Text(
                    text = currentChapter.title,
                    fontSize = 24.sp,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            pageParagraphs.forEach { pageParagraph ->
                ReaderParagraphItem(
                    paragraphIndex = pageParagraph.index,
                    paragraphText = pageParagraph.text,
                    fontSize = uiState.fontSize,
                    textColor = textColor,
                    commentCount = uiState.commentCounts[pageParagraph.index],
                    onOpenComment = {
                        if (currentChapter.id > 0) {
                            viewModel.loadComments(pageParagraph.index)
                            showCommentDrawer = true
                        }
                    }
                )
            }
        }
    }
}
```

**Step 2: 验证编译**

```bash
cd android-user
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

**Step 3: 提交更改**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add tap gesture to toggle toolbars in paged mode"
```

---

## Task 5: 为滚动模式添加点击检测

**Files:**
- Modify: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt:253-289`

**Step 1: 在 LazyColumn 外层添加 Box 和点击检测**

找到第 253 行的 `} else {`，修改 LazyColumn：

```kotlin
} else {
    Box(
        modifier = Modifier
            .weight(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showToolbars = !showToolbars
                    }
                )
            }
    ) {
        LazyColumn(
            state = scrollListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item(key = "chapter-title-${currentChapter.id}") {
                Text(
                    text = currentChapter.title,
                    fontSize = 24.sp,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            itemsIndexed(
                items = paragraphs,
                key = { index, _ -> "paragraph-${currentChapter.id}-$index" }
            ) { index, paragraph ->
                ReaderParagraphItem(
                    paragraphIndex = index,
                    paragraphText = paragraph,
                    fontSize = uiState.fontSize,
                    textColor = textColor,
                    commentCount = uiState.commentCounts[index],
                    onOpenComment = {
                        if (currentChapter.id > 0) {
                            viewModel.loadComments(index)
                            showCommentDrawer = true
                        }
                    }
                )
            }
        }
    }
}
```

**Step 2: 验证编译**

```bash
cd android-user
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

**Step 3: 提交更改**

```bash
git add android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt
git commit -m "feat(reader): add tap gesture to toggle toolbars in scroll mode"
```

---

## Task 6: 测试和验证

**Files:**
- Test: `android-user/app/src/main/java/com/ebookstore/user/ui/screens/reader/ReaderScreen.kt`

**Step 1: 构建 APK**

```bash
cd android-user
./gradlew :app:assembleDebug
```

Expected: BUILD SUCCESSFUL

**Step 2: 安装到设备或模拟器**

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Expected: Success

**Step 3: 手动测试清单**

测试项目：
- [ ] 打开任意书籍进入阅读器
- [ ] 点击阅读内容区域，顶部栏和底部栏应该隐藏
- [ ] 再次点击阅读内容区域，工具栏应该重新显示
- [ ] 动画应该流畅（从顶部/底部滑入滑出）
- [ ] 长按段落应该仍然能打开评论面板
- [ ] 在翻页模式下测试点击切换
- [ ] 在滚动模式下测试点击切换
- [ ] 切换章节后工具栏状态应该保持
- [ ] 隐藏工具栏后，阅读内容应该占据全屏

**Step 4: 记录测试结果**

创建测试报告：

```bash
echo "# 阅读器工具栏切换功能测试报告

## 测试环境
- 设备：[填写设备型号]
- Android 版本：[填写版本]
- 测试时间：$(date)

## 测试结果
- 点击切换功能：[通过/失败]
- 动画效果：[通过/失败]
- 长按评论功能：[通过/失败]
- 翻页模式：[通过/失败]
- 滚动模式：[通过/失败]
- 章节切换：[通过/失败]
- 全屏显示：[通过/失败]

## 问题记录
[记录发现的问题]
" > docs/plans/2026-02-25-reader-toggle-controls-test-report.md
```

**Step 5: 最终提交**

```bash
git add docs/plans/2026-02-25-reader-toggle-controls-test-report.md
git commit -m "test(reader): add manual test report for toolbar toggle feature"
```

---

## 完成标准

- [ ] 所有代码已提交
- [ ] 编译无错误
- [ ] 点击内容区域能切换工具栏显示/隐藏
- [ ] 动画流畅自然
- [ ] 长按评论功能不受影响
- [ ] 翻页和滚动模式都正常工作
- [ ] 测试报告已创建
