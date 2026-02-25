package com.ebookstore.user.ui.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Chapter
import com.ebookstore.user.data.model.Comment
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadBook(bookId: Int, initialChapter: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, bookId = bookId)

            val chaptersResult = bookRepository.getChapters(bookId)
            var chapters = chaptersResult.getOrNull() ?: emptyList()

            // If no chapters, try to load text content and parse
            if (chapters.isEmpty()) {
                val textResult = bookRepository.getBookTextContent(bookId)
                textResult.getOrNull()?.let { content ->
                    chapters = parseTextToChapters(content, bookId)
                }
            }

            val initialIndex = if (initialChapter != null) {
                chapters.indexOfFirst { it.title == initialChapter }.takeIf { it >= 0 } ?: 0
            } else {
                0
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                chapters = chapters,
                currentChapterIndex = initialIndex
            )

            if (chapters.isNotEmpty()) {
                saveHistory()
                loadCommentCounts()
            }
        }
    }

    private fun parseTextToChapters(content: String, bookId: Int): List<Chapter> {
        val lines = content.replace("\r\n", "\n").trim().split("\n")
        val titlePattern = Regex("^(第[0-9一二三四五六七八九十百千万零两]+[章节卷篇部回].*|(?:chapter|chap\\.?)\\s*\\d+.*)", RegexOption.IGNORE_CASE)
        val titleIndexes = mutableListOf<Int>()

        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && trimmed.length <= 60 && titlePattern.matches(trimmed)) {
                titleIndexes.add(index)
            }
        }

        val chapters = mutableListOf<Chapter>()

        if (titleIndexes.isNotEmpty()) {
            titleIndexes.forEachIndexed { i, startIdx ->
                val endIdx = if (i < titleIndexes.size - 1) titleIndexes[i + 1] else lines.size
                val title = lines[startIdx].trim()
                val body = lines.subList(startIdx + 1, endIdx).joinToString("\n").trim()
                if (body.isNotEmpty()) {
                    chapters.add(
                        Chapter(
                            id = -(chapters.size + 1),
                            bookId = bookId,
                            title = title,
                            content = body,
                            sortOrder = chapters.size + 1
                        )
                    )
                }
            }
        } else {
            // Split by size
            val maxChars = 8000
            var buffer = ""
            var index = 1
            content.split("\n\n").forEach { paragraph ->
                if ((buffer + "\n\n" + paragraph).length > maxChars && buffer.isNotEmpty()) {
                    chapters.add(
                        Chapter(
                            id = -index,
                            bookId = bookId,
                            title = "第${index}节",
                            content = buffer,
                            sortOrder = index
                        )
                    )
                    index++
                    buffer = paragraph
                } else {
                    buffer = if (buffer.isEmpty()) paragraph else "$buffer\n\n$paragraph"
                }
            }
            if (buffer.isNotEmpty()) {
                chapters.add(
                    Chapter(
                        id = -index,
                        bookId = bookId,
                        title = "第${index}节",
                        content = buffer,
                        sortOrder = index
                    )
                )
            }
        }

        return chapters
    }

    fun selectChapter(index: Int) {
        if (index in _uiState.value.chapters.indices) {
            _uiState.value = _uiState.value.copy(currentChapterIndex = index)
            saveHistory()
            loadCommentCounts()
        }
    }

    fun nextChapter() {
        val nextIndex = _uiState.value.currentChapterIndex + 1
        if (nextIndex < _uiState.value.chapters.size) {
            selectChapter(nextIndex)
        }
    }

    fun prevChapter() {
        val prevIndex = _uiState.value.currentChapterIndex - 1
        if (prevIndex >= 0) {
            selectChapter(prevIndex)
        }
    }

    private fun saveHistory() {
        viewModelScope.launch {
            val chapter = _uiState.value.chapters.getOrNull(_uiState.value.currentChapterIndex)
            if (chapter != null) {
                val progress = "${((_uiState.value.currentChapterIndex + 1) * 100) / _uiState.value.chapters.size}%"
                bookRepository.saveHistory(_uiState.value.bookId, chapter.title, progress)
            }
        }
    }

    private fun loadCommentCounts() {
        viewModelScope.launch {
            val chapter = _uiState.value.chapters.getOrNull(_uiState.value.currentChapterIndex)
            if (chapter != null && chapter.id > 0) {
                bookRepository.getCommentCounts(_uiState.value.bookId, chapter.id)
                    .onSuccess { counts ->
                        _uiState.value = _uiState.value.copy(commentCounts = counts)
                    }
            }
        }
    }

    fun loadComments(paragraphIndex: Int, sortBy: String = "time") {
        viewModelScope.launch {
            val chapter = _uiState.value.chapters.getOrNull(_uiState.value.currentChapterIndex)
            if (chapter != null && chapter.id > 0) {
                _uiState.value = _uiState.value.copy(isLoadingComments = true)
                bookRepository.getComments(_uiState.value.bookId, chapter.id, paragraphIndex, sortBy)
                    .onSuccess { comments ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingComments = false,
                            comments = comments,
                            selectedParagraphIndex = paragraphIndex
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(isLoadingComments = false)
                    }
            }
        }
    }

    fun addComment(content: String, parentId: Int?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val chapter = _uiState.value.chapters.getOrNull(_uiState.value.currentChapterIndex)
            val paragraphIndex = _uiState.value.selectedParagraphIndex
            if (chapter != null && chapter.id > 0 && paragraphIndex != null) {
                bookRepository.addComment(_uiState.value.bookId, chapter.id, paragraphIndex, content, parentId)
                    .onSuccess {
                        loadComments(paragraphIndex, _uiState.value.commentSortBy)
                        loadCommentCounts()
                        onSuccess()
                    }
            }
        }
    }

    fun reactToComment(commentId: Int, value: Int) {
        viewModelScope.launch {
            bookRepository.reactToComment(commentId, value)
                .onSuccess {
                    _uiState.value.selectedParagraphIndex?.let {
                        loadComments(it, _uiState.value.commentSortBy)
                    }
                }
        }
    }

    fun hideComment(commentId: Int) {
        viewModelScope.launch {
            bookRepository.hideComment(commentId)
                .onSuccess {
                    _uiState.value.selectedParagraphIndex?.let {
                        loadComments(it, _uiState.value.commentSortBy)
                    }
                }
        }
    }

    fun unhideComment(commentId: Int) {
        viewModelScope.launch {
            bookRepository.unhideComment(commentId)
                .onSuccess {
                    _uiState.value.selectedParagraphIndex?.let {
                        loadComments(it, _uiState.value.commentSortBy)
                    }
                }
        }
    }

    fun urgeUpdate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            bookRepository.urgeUpdate(_uiState.value.bookId)
                .onSuccess { onSuccess() }
        }
    }

    fun setFontSize(size: Int) {
        _uiState.value = _uiState.value.copy(fontSize = size)
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(isDarkMode = !_uiState.value.isDarkMode)
    }

    fun setReaderMode(mode: ReaderMode) {
        _uiState.value = _uiState.value.copy(readerMode = mode)
    }

    fun setCommentSortBy(sortBy: String) {
        _uiState.value = _uiState.value.copy(commentSortBy = sortBy)
        _uiState.value.selectedParagraphIndex?.let { loadComments(it, sortBy) }
    }

    fun closeComments() {
        _uiState.value = _uiState.value.copy(
            selectedParagraphIndex = null,
            comments = emptyList()
        )
    }
}

data class ReaderUiState(
    val isLoading: Boolean = false,
    val isLoadingComments: Boolean = false,
    val bookId: Int = 0,
    val chapters: List<Chapter> = emptyList(),
    val currentChapterIndex: Int = 0,
    val fontSize: Int = 18,
    val readerMode: ReaderMode = ReaderMode.PAGED,
    val isDarkMode: Boolean = false,
    val commentCounts: Map<Int, Int> = emptyMap(),
    val selectedParagraphIndex: Int? = null,
    val comments: List<Comment> = emptyList(),
    val commentSortBy: String = "time"
)

enum class ReaderMode {
    PAGED,
    SCROLL
}
