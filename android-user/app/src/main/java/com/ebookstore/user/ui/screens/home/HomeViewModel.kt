package com.ebookstore.user.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Book
import com.ebookstore.user.data.model.Carousel
import com.ebookstore.user.data.model.ReadHistory
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val carouselsResult = bookRepository.getCarousels()
            val hotBooksResult = bookRepository.getBooks(1, 8, sortBy = "visit_count", order = "desc")
            val historyResult = bookRepository.getHistory()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                carousels = carouselsResult.getOrNull() ?: emptyList(),
                hotBooks = hotBooksResult.getOrNull()?.records ?: emptyList(),
                history = historyResult.getOrNull() ?: emptyList()
            )
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val carousels: List<Carousel> = emptyList(),
    val hotBooks: List<Book> = emptyList(),
    val history: List<ReadHistory> = emptyList()
)
