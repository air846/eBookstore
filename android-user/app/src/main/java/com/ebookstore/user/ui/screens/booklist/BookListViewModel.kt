package com.ebookstore.user.ui.screens.booklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Book
import com.ebookstore.user.data.model.Category
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookListUiState())
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadBooks()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            bookRepository.getCategoryTree()
                .onSuccess { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = flattenCategories(categories)
                    )
                }
        }
    }

    private fun flattenCategories(categories: List<Category>): List<Category> {
        val result = mutableListOf<Category>()
        fun flatten(list: List<Category>) {
            list.forEach { category ->
                result.add(category)
                category.children?.let { flatten(it) }
            }
        }
        flatten(categories)
        return result
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            bookRepository.getBooks(
                page = _uiState.value.page,
                size = _uiState.value.size,
                keyword = _uiState.value.keyword.takeIf { it.isNotBlank() },
                categoryId = _uiState.value.selectedCategoryId,
                sortBy = _uiState.value.sortBy,
                order = _uiState.value.order
            )
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        books = response.records,
                        total = response.total
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    fun onKeywordChange(keyword: String) {
        _uiState.value = _uiState.value.copy(keyword = keyword)
    }

    fun onCategoryChange(categoryId: Int?) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            page = 1
        )
        loadBooks()
    }

    fun onSortChange(sortBy: String) {
        _uiState.value = _uiState.value.copy(
            sortBy = sortBy,
            page = 1
        )
        loadBooks()
    }

    fun onPageChange(page: Int) {
        _uiState.value = _uiState.value.copy(page = page)
        loadBooks()
    }

    fun search() {
        _uiState.value = _uiState.value.copy(page = 1)
        loadBooks()
    }
}

data class BookListUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
    val keyword: String = "",
    val selectedCategoryId: Int? = null,
    val sortBy: String = "create_time",
    val order: String = "desc",
    val page: Int = 1,
    val size: Int = 10,
    val total: Long = 0
)
