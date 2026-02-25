package com.ebookstore.user.ui.screens.bookshelf;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.ReadHistory;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0012\b\u0086\b\u0018\u00002\u00020\u0001B?\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u0015\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bH\u00c6\u0003J\t\u0010\u0018\u001a\u00020\fH\u00c6\u0003JC\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\tH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\fH\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001d\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0012R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u001e"}, d2 = {"Lcom/ebookstore/user/ui/screens/bookshelf/BookshelfUiState;", "", "isLoading", "", "favorites", "", "Lcom/ebookstore/user/data/model/Book;", "historyMap", "", "", "Lcom/ebookstore/user/data/model/ReadHistory;", "searchQuery", "", "(ZLjava/util/List;Ljava/util/Map;Ljava/lang/String;)V", "getFavorites", "()Ljava/util/List;", "getHistoryMap", "()Ljava/util/Map;", "()Z", "getSearchQuery", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class BookshelfUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Book> favorites = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.Integer, com.ebookstore.user.data.model.ReadHistory> historyMap = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String searchQuery = null;
    
    public BookshelfUiState(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> favorites, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, com.ebookstore.user.data.model.ReadHistory> historyMap, @org.jetbrains.annotations.NotNull
    java.lang.String searchQuery) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> getFavorites() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, com.ebookstore.user.data.model.ReadHistory> getHistoryMap() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    public BookshelfUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, com.ebookstore.user.data.model.ReadHistory> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.screens.bookshelf.BookshelfUiState copy(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> favorites, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, com.ebookstore.user.data.model.ReadHistory> historyMap, @org.jetbrains.annotations.NotNull
    java.lang.String searchQuery) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}