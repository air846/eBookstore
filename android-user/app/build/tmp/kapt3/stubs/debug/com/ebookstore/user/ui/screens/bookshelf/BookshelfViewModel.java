package com.ebookstore.user.ui.screens.bookshelf;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.ReadHistory;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0014"}, d2 = {"Lcom/ebookstore/user/ui/screens/bookshelf/BookshelfViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lcom/ebookstore/user/data/repository/BookRepository;", "(Lcom/ebookstore/user/data/repository/BookRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ebookstore/user/ui/screens/bookshelf/BookshelfUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadData", "", "onSearchChange", "query", "", "removeFavorite", "bookId", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class BookshelfViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ebookstore.user.ui.screens.bookshelf.BookshelfUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.bookshelf.BookshelfUiState> uiState = null;
    
    @javax.inject.Inject
    public BookshelfViewModel(@org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.repository.BookRepository bookRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.bookshelf.BookshelfUiState> getUiState() {
        return null;
    }
    
    public final void loadData() {
    }
    
    public final void removeFavorite(int bookId) {
    }
    
    public final void onSearchChange(@org.jetbrains.annotations.NotNull
    java.lang.String query) {
    }
}