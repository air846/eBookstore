package com.ebookstore.user.ui.screens.booklist;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.Category;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0002J\u0006\u0010\u0010\u001a\u00020\u0011J\b\u0010\u0012\u001a\u00020\u0011H\u0002J\u0015\u0010\u0013\u001a\u00020\u00112\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u0015J\u000e\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u0019J\u0006\u0010\u001e\u001a\u00020\u0011R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u001f"}, d2 = {"Lcom/ebookstore/user/ui/screens/booklist/BookListViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lcom/ebookstore/user/data/repository/BookRepository;", "(Lcom/ebookstore/user/data/repository/BookRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ebookstore/user/ui/screens/booklist/BookListUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "flattenCategories", "", "Lcom/ebookstore/user/data/model/Category;", "categories", "loadBooks", "", "loadCategories", "onCategoryChange", "categoryId", "", "(Ljava/lang/Integer;)V", "onKeywordChange", "keyword", "", "onPageChange", "page", "onSortChange", "sortBy", "search", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class BookListViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ebookstore.user.ui.screens.booklist.BookListUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.booklist.BookListUiState> uiState = null;
    
    @javax.inject.Inject
    public BookListViewModel(@org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.repository.BookRepository bookRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.booklist.BookListUiState> getUiState() {
        return null;
    }
    
    private final void loadCategories() {
    }
    
    private final java.util.List<com.ebookstore.user.data.model.Category> flattenCategories(java.util.List<com.ebookstore.user.data.model.Category> categories) {
        return null;
    }
    
    public final void loadBooks() {
    }
    
    public final void onKeywordChange(@org.jetbrains.annotations.NotNull
    java.lang.String keyword) {
    }
    
    public final void onCategoryChange(@org.jetbrains.annotations.Nullable
    java.lang.Integer categoryId) {
    }
    
    public final void onSortChange(@org.jetbrains.annotations.NotNull
    java.lang.String sortBy) {
    }
    
    public final void onPageChange(int page) {
    }
    
    public final void search() {
    }
}