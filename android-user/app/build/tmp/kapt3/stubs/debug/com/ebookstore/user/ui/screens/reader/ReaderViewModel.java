package com.ebookstore.user.ui.screens.reader;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Chapter;
import com.ebookstore.user.data.model.Comment;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J+\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\r0\u0013\u00a2\u0006\u0002\u0010\u0014J\u0006\u0010\u0015\u001a\u00020\rJ\u000e\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0011J\u0018\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u00112\b\u0010\u001a\u001a\u0004\u0018\u00010\u000fJ\b\u0010\u001b\u001a\u00020\rH\u0002J\u0018\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u00112\b\b\u0002\u0010\u001e\u001a\u00020\u000fJ\u0006\u0010\u001f\u001a\u00020\rJ\u001e\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u0011H\u0002J\u0006\u0010#\u001a\u00020\rJ\u0016\u0010$\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u00112\u0006\u0010%\u001a\u00020\u0011J\b\u0010&\u001a\u00020\rH\u0002J\u000e\u0010\'\u001a\u00020\r2\u0006\u0010(\u001a\u00020\u0011J\u000e\u0010)\u001a\u00020\r2\u0006\u0010\u001e\u001a\u00020\u000fJ\u000e\u0010*\u001a\u00020\r2\u0006\u0010+\u001a\u00020\u0011J\u0006\u0010,\u001a\u00020\rJ\u000e\u0010-\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0011J\u0014\u0010.\u001a\u00020\r2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\r0\u0013R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006/"}, d2 = {"Lcom/ebookstore/user/ui/screens/reader/ReaderViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lcom/ebookstore/user/data/repository/BookRepository;", "(Lcom/ebookstore/user/data/repository/BookRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ebookstore/user/ui/screens/reader/ReaderUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addComment", "", "content", "", "parentId", "", "onSuccess", "Lkotlin/Function0;", "(Ljava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function0;)V", "closeComments", "hideComment", "commentId", "loadBook", "bookId", "initialChapter", "loadCommentCounts", "loadComments", "paragraphIndex", "sortBy", "nextChapter", "parseTextToChapters", "", "Lcom/ebookstore/user/data/model/Chapter;", "prevChapter", "reactToComment", "value", "saveHistory", "selectChapter", "index", "setCommentSortBy", "setFontSize", "size", "toggleDarkMode", "unhideComment", "urgeUpdate", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class ReaderViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ebookstore.user.ui.screens.reader.ReaderUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.reader.ReaderUiState> uiState = null;
    
    @javax.inject.Inject
    public ReaderViewModel(@org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.repository.BookRepository bookRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.reader.ReaderUiState> getUiState() {
        return null;
    }
    
    public final void loadBook(int bookId, @org.jetbrains.annotations.Nullable
    java.lang.String initialChapter) {
    }
    
    private final java.util.List<com.ebookstore.user.data.model.Chapter> parseTextToChapters(java.lang.String content, int bookId) {
        return null;
    }
    
    public final void selectChapter(int index) {
    }
    
    public final void nextChapter() {
    }
    
    public final void prevChapter() {
    }
    
    private final void saveHistory() {
    }
    
    private final void loadCommentCounts() {
    }
    
    public final void loadComments(int paragraphIndex, @org.jetbrains.annotations.NotNull
    java.lang.String sortBy) {
    }
    
    public final void addComment(@org.jetbrains.annotations.NotNull
    java.lang.String content, @org.jetbrains.annotations.Nullable
    java.lang.Integer parentId, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void reactToComment(int commentId, int value) {
    }
    
    public final void hideComment(int commentId) {
    }
    
    public final void unhideComment(int commentId) {
    }
    
    public final void urgeUpdate(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void setFontSize(int size) {
    }
    
    public final void toggleDarkMode() {
    }
    
    public final void setCommentSortBy(@org.jetbrains.annotations.NotNull
    java.lang.String sortBy) {
    }
    
    public final void closeComments() {
    }
}