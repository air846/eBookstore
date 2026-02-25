package com.ebookstore.user.ui.screens.home;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.Carousel;
import com.ebookstore.user.data.model.ReadHistory;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B?\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0003JC\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0010\u00a8\u0006\u001c"}, d2 = {"Lcom/ebookstore/user/ui/screens/home/HomeUiState;", "", "isLoading", "", "carousels", "", "Lcom/ebookstore/user/data/model/Carousel;", "hotBooks", "Lcom/ebookstore/user/data/model/Book;", "history", "Lcom/ebookstore/user/data/model/ReadHistory;", "(ZLjava/util/List;Ljava/util/List;Ljava/util/List;)V", "getCarousels", "()Ljava/util/List;", "getHistory", "getHotBooks", "()Z", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class HomeUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Carousel> carousels = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Book> hotBooks = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.ReadHistory> history = null;
    
    public HomeUiState(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Carousel> carousels, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> hotBooks, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.ReadHistory> history) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Carousel> getCarousels() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> getHotBooks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.ReadHistory> getHistory() {
        return null;
    }
    
    public HomeUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Carousel> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.ReadHistory> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.screens.home.HomeUiState copy(boolean isLoading, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Carousel> carousels, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> hotBooks, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.ReadHistory> history) {
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