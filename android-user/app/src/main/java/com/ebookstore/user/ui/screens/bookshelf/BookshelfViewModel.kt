package com.ebookstore.user.ui.screens.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Book
import com.ebookstore.user.data.model.ReadHistory
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookshelfUiState())
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val favoritesResult = bookRepository.getFavorites()
            val historyResult = bookRepository.getHistory()

            val favorites = favoritesResult.getOrNull() ?: emptyList()
            val history = historyResult.getOrNull() ?: emptyList()

            val historyMap = history.associateBy { it.bookId }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favorites = favorites,
                historyMap = historyMap
            )
        }
    }

    fun removeFavorite(bookId: Int) {
        viewModelScope.launch {
            bookRepository.removeFavorite(bookId)
                .onSuccess {
                    loadData()
                }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}

data class BookshelfUiState(
    val isLoading: Boolean = false,
    val favorites: List<Book> = emptyList(),
    val historyMap: Map<Int, ReadHistory> = emptyMap(),
    val searchQuery: String = ""
)
