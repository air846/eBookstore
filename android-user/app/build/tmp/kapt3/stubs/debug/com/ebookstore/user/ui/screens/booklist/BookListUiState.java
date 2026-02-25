package com.ebookstore.user.ui.screens.booklist;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.Category;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\"\b\u0086\b\u0018\u00002\u00020\u0001Bw\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\u0002\u0010\u0013J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0012H\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\nH\u00c6\u0003J\u0010\u0010)\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010*\u001a\u00020\nH\u00c6\u0003J\t\u0010+\u001a\u00020\nH\u00c6\u0003J\t\u0010,\u001a\u00020\fH\u00c6\u0003J\t\u0010-\u001a\u00020\fH\u00c6\u0003J\u0080\u0001\u0010.\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\n2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u00c6\u0001\u00a2\u0006\u0002\u0010/J\u0013\u00100\u001a\u00020\u00032\b\u00101\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00102\u001a\u00020\fH\u00d6\u0001J\t\u00103\u001a\u00020\nH\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0017R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0010\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0019R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#\u00a8\u00064"}, d2 = {"Lcom/ebookstore/user/ui/screens/booklist/BookListUiState;", "", "isLoading", "", "books", "", "Lcom/ebookstore/user/data/model/Book;", "categories", "Lcom/ebookstore/user/data/model/Category;", "keyword", "", "selectedCategoryId", "", "sortBy", "order", "page", "size", "total", "", "(ZLjava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;IIJ)V", "getBooks", "()Ljava/util/List;", "getCategories", "()Z", "getKeyword", "()Ljava/lang/String;", "getOrder", "getPage", "()I", "getSelectedCategoryId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getSize", "getSortBy", "getTotal", "()J", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZLjava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;IIJ)Lcom/ebookstore/user/ui/screens/booklist/BookListUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class BookListUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Book> books = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Category> categories = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String keyword = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Integer selectedCategoryId = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sortBy = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String order = null;
    private final int page = 0;
    private final int size = 0;
    private final long total = 0L;
    
    public BookListUiState(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> books, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Category> categories, @org.jetbrains.annotations.NotNull
    java.lang.String keyword, @org.jetbrains.annotations.Nullable
    java.lang.Integer selectedCategoryId, @org.jetbrains.annotations.NotNull
    java.lang.String sortBy, @org.jetbrains.annotations.NotNull
    java.lang.String order, int page, int size, long total) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> getBooks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Category> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getKeyword() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer getSelectedCategoryId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSortBy() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getOrder() {
        return null;
    }
    
    public final int getPage() {
        return 0;
    }
    
    public final int getSize() {
        return 0;
    }
    
    public final long getTotal() {
        return 0L;
    }
    
    public BookListUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final long component10() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Category> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    public final int component8() {
        return 0;
    }
    
    public final int component9() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.screens.booklist.BookListUiState copy(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> books, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Category> categories, @org.jetbrains.annotations.NotNull
    java.lang.String keyword, @org.jetbrains.annotations.Nullable
    java.lang.Integer selectedCategoryId, @org.jetbrains.annotations.NotNull
    java.lang.String sortBy, @org.jetbrains.annotations.NotNull
    java.lang.String order, int page, int size, long total) {
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