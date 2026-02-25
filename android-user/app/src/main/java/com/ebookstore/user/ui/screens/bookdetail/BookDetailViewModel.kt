package com.ebookstore.user.ui.screens.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Book
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            bookRepository.getBookDetail(bookId)
                .onSuccess { book ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        book = book
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }

            // Load reading history
            bookRepository.getBookHistory(bookId)
                .onSuccess { history ->
                    _uiState.value = _uiState.value.copy(lastReadChapter = history?.chapter)
                }
        }
    }

    fun addToFavorite(bookId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            bookRepository.addFavorite(bookId)
                .onSuccess {
                    onSuccess()
                }
        }
    }
}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val lastReadChapter: String? = null
)
