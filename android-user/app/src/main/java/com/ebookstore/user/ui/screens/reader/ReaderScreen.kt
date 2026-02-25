package com.ebookstore.user.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    bookId: Int,
    initialChapter: String?,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showChapterDrawer by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showCommentDrawer by remember { mutableStateOf(false) }
    var showToolbars by remember { mutableStateOf(true) }

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId, initialChapter)
    }

    val currentChapter = uiState.chapters.getOrNull(uiState.currentChapterIndex)
    val paragraphs = remember(currentChapter) {
        currentChapter?.content?.split("\n\n")?.filter { it.isNotBlank() } ?: emptyList()
    }
    val pagedParagraphs = remember(paragraphs, uiState.fontSize) {
        paginateReaderParagraphs(paragraphs, uiState.fontSize)
    }
    val pageCount = pagedParagraphs.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scrollListState = rememberLazyListState()
    val canGoPrevPage = uiState.currentChapterIndex > 0 || pagerState.currentPage > 0
    val canGoNextPage = uiState.currentChapterIndex < uiState.chapters.lastIndex || pagerState.currentPage < pageCount - 1
    val canGoPrevChapter = uiState.currentChapterIndex > 0
    val canGoNextChapter = uiState.currentChapterIndex < uiState.chapters.lastIndex
    val currentPageDisplay = (pagerState.currentPage + 1).coerceAtMost(pageCount)
    val readingProgress = calculateReadingProgress(
        mode = uiState.readerMode,
        currentChapterIndex = uiState.currentChapterIndex,
        chapterCount = uiState.chapters.size,
        currentPage = pagerState.currentPage,
        pageCount = pageCount
    )

    val backgroundColor = if (uiState.isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF4ECD8)
    val textColor = if (uiState.isDarkMode) Color(0xFFD2C7B8) else Color(0xFF5B4636)

    LaunchedEffect(currentChapter?.id, pageCount, uiState.readerMode) {
        if (uiState.readerMode == ReaderMode.PAGED) {
            pagerState.scrollToPage(0)
        } else if (currentChapter != null) {
            scrollListState.scrollToItem(0)
        }
    }

    fun previousPageOrChapter() {
        if (pagerState.currentPage > 0) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        } else {
            viewModel.prevChapter()
        }
    }

    fun nextPageOrChapter() {
        if (pagerState.currentPage < pageCount - 1) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        } else {
            viewModel.nextChapter()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Top Bar
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

                // Settings Panel
                if (showSettings) {
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

                // Content
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.CircularProgressIndicator()
                    }
                } else if (currentChapter != null) {
                    if (uiState.readerMode == ReaderMode.PAGED) {
                        HorizontalPager(
                            state = pagerState,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .weight(1f)
                                .padding(24.dp)
                        ) { pageIndex ->
                            val pageParagraphs = pagedParagraphs.getOrElse(pageIndex) { emptyList() }
                            Column(
                                modifier = Modifier.fillMaxSize(),
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
                    } else {
                        LazyColumn(
                            state = scrollListState,
                            modifier = Modifier
                                .weight(1f)
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
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("当前书籍还没有章节内容", color = textColor)
                    }
                }

                // Bottom Bar
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

            // Chapter Drawer
            if (showChapterDrawer) {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "章节目录",
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(onClick = { showChapterDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "关闭")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.chapters) { chapter ->
                                val index = uiState.chapters.indexOf(chapter)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectChapter(index)
                                            showChapterDrawer = false
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (index == uiState.currentChapterIndex) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        text = "${index + 1}. ${chapter.title}",
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Comment Drawer
            if (showCommentDrawer && uiState.selectedParagraphIndex != null) {
                CommentDrawer(
                    comments = uiState.comments,
                    isLoading = uiState.isLoadingComments,
                    sortBy = uiState.commentSortBy,
                    onDismiss = {
                        showCommentDrawer = false
                        viewModel.closeComments()
                    },
                    onSortChange = viewModel::setCommentSortBy,
                    onAddComment = { content, parentId ->
                        viewModel.addComment(content, parentId) {
                            scope.launch {
                                snackbarHostState.showSnackbar("评论成功")
                            }
                        }
                    },
                    onReact = viewModel::reactToComment,
                    onHide = viewModel::hideComment,
                    onUnhide = viewModel::unhideComment
                )
            }
        }
    }
}

@Composable
private fun ReaderParagraphItem(
    paragraphIndex: Int,
    paragraphText: String,
    fontSize: Int,
    textColor: Color,
    commentCount: Int?,
    onOpenComment: () -> Unit
) {
    val normalizedText = paragraphText.trim()
    if (normalizedText.isEmpty()) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(paragraphIndex) {
                detectTapGestures(onLongPress = { onOpenComment() })
            }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = normalizedText,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 2).sp,
            color = textColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if ((commentCount ?: 0) > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable { onOpenComment() },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "评 $commentCount",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

internal data class ReaderPageParagraph(
    val index: Int,
    val text: String
)

internal fun calculateReadingProgress(
    mode: ReaderMode,
    currentChapterIndex: Int,
    chapterCount: Int,
    currentPage: Int,
    pageCount: Int
): Float {
    val safeChapterCount = chapterCount.coerceAtLeast(1)
    val safePageCount = pageCount.coerceAtLeast(1)
    val safeChapterIndex = currentChapterIndex.coerceAtLeast(0)
    val safePage = currentPage.coerceIn(0, safePageCount - 1)

    return when (mode) {
        ReaderMode.PAGED -> {
            val chapterProgress = (safePage + 1).toFloat() / safePageCount
            ((safeChapterIndex + chapterProgress) / safeChapterCount).coerceIn(0f, 1f)
        }

        ReaderMode.SCROLL -> {
            ((safeChapterIndex + 1f) / safeChapterCount).coerceIn(0f, 1f)
        }
    }
}

internal fun paginateReaderParagraphs(
    paragraphs: List<String>,
    fontSize: Int
): List<List<ReaderPageParagraph>> {
    if (paragraphs.isEmpty()) return emptyList()

    val charsPerPage = (720f * (18f / fontSize.coerceAtLeast(12))).toInt()
        .coerceIn(260, 900)

    val pages = mutableListOf<MutableList<ReaderPageParagraph>>()
    var currentPage = mutableListOf<ReaderPageParagraph>()
    var currentChars = 0

    paragraphs.forEachIndexed { index, paragraph ->
        val normalizedParagraph = paragraph.trim()
        if (normalizedParagraph.isEmpty()) return@forEachIndexed

        val paragraphChars = normalizedParagraph.length + 2
        if (currentPage.isNotEmpty() && currentChars + paragraphChars > charsPerPage) {
            pages.add(currentPage)
            currentPage = mutableListOf()
            currentChars = 0
        }

        currentPage.add(ReaderPageParagraph(index = index, text = normalizedParagraph))
        currentChars += paragraphChars
    }

    if (currentPage.isNotEmpty()) {
        pages.add(currentPage)
    }

    return pages
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDrawer(
    comments: List<com.ebookstore.user.data.model.Comment>,
    isLoading: Boolean,
    sortBy: String,
    onDismiss: () -> Unit,
    onSortChange: (String) -> Unit,
    onAddComment: (String, Int?) -> Unit,
    onReact: (Int, Int) -> Unit,
    onHide: (Int) -> Unit,
    onUnhide: (Int) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Int?>(null) }

    ModalDrawerSheet(
        modifier = Modifier
            .width(420.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("段评", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            }

            var sortExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it }
            ) {
                OutlinedTextField(
                    value = if (sortBy == "time") "最新" else "最热",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("最新") },
                        onClick = {
                            onSortChange("time")
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("最热") },
                        onClick = {
                            onSortChange("hot")
                            sortExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("说点什么吧") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (replyTo != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("正在回复评论", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { replyTo = null }) {
                        Text("取消")
                    }
                }
            }

            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onAddComment(commentText, replyTo)
                        commentText = ""
                        replyTo = null
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("提交")
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.CircularProgressIndicator()
                }
            } else if (comments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无评论")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onReply = { replyTo = it },
                            onReact = onReact,
                            onHide = onHide,
                            onUnhide = onUnhide
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: com.ebookstore.user.data.model.Comment,
    onReply: (Int) -> Unit,
    onReact: (Int, Int) -> Unit,
    onHide: (Int) -> Unit,
    onUnhide: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!comment.hidden) {
                Text(text = comment.content)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "赞 ${comment.likeCount ?: 0}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "踩 ${comment.dislikeCount ?: 0}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        onReact(comment.id, if (comment.userReaction == 1) 0 else 1)
                    }) {
                        Text(if (comment.userReaction == 1) "取消赞" else "点赞")
                    }
                    TextButton(onClick = {
                        onReact(comment.id, if (comment.userReaction == -1) 0 else -1)
                    }) {
                        Text(if (comment.userReaction == -1) "取消踩" else "拉踩")
                    }
                    TextButton(onClick = { onReply(comment.id) }) {
                        Text("回复")
                    }
                    TextButton(onClick = { onHide(comment.id) }) {
                        Text("折叠")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("该评论已折叠")
                    TextButton(onClick = { onUnhide(comment.id) }) {
                        Text("展开")
                    }
                }
            }

            comment.replies?.forEach { reply ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    CommentItem(
                        comment = reply,
                        onReply = onReply,
                        onReact = onReact,
                        onHide = onHide,
                        onUnhide = onUnhide
                    )
                }
            }
        }
    }
}
