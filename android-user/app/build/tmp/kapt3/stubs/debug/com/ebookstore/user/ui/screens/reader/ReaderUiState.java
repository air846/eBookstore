package com.ebookstore.user.ui.screens.reader;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Chapter;
import com.ebookstore.user.data.model.Comment;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\"\b\u0086\b\u0018\u00002\u00020\u0001B\u008d\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0006\u0012\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\b\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\u0002\u0010\u0014J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00110\bH\u00c6\u0003J\t\u0010&\u001a\u00020\u0013H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010)\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J\t\u0010*\u001a\u00020\u0006H\u00c6\u0003J\t\u0010+\u001a\u00020\u0006H\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\u0015\u0010-\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u000eH\u00c6\u0003J\u0010\u0010.\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\"J\u0096\u0001\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\u00032\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00062\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\b2\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u00c6\u0001\u00a2\u0006\u0002\u00100J\u0013\u00101\u001a\u00020\u00032\b\u00102\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00103\u001a\u00020\u0006H\u00d6\u0001J\t\u00104\u001a\u00020\u0013H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001d\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018R\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0016R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010 R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010 R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010 R\u0015\u0010\u000f\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010#\u001a\u0004\b!\u0010\"\u00a8\u00065"}, d2 = {"Lcom/ebookstore/user/ui/screens/reader/ReaderUiState;", "", "isLoading", "", "isLoadingComments", "bookId", "", "chapters", "", "Lcom/ebookstore/user/data/model/Chapter;", "currentChapterIndex", "fontSize", "isDarkMode", "commentCounts", "", "selectedParagraphIndex", "comments", "Lcom/ebookstore/user/data/model/Comment;", "commentSortBy", "", "(ZZILjava/util/List;IIZLjava/util/Map;Ljava/lang/Integer;Ljava/util/List;Ljava/lang/String;)V", "getBookId", "()I", "getChapters", "()Ljava/util/List;", "getCommentCounts", "()Ljava/util/Map;", "getCommentSortBy", "()Ljava/lang/String;", "getComments", "getCurrentChapterIndex", "getFontSize", "()Z", "getSelectedParagraphIndex", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZZILjava/util/List;IIZLjava/util/Map;Ljava/lang/Integer;Ljava/util/List;Ljava/lang/String;)Lcom/ebookstore/user/ui/screens/reader/ReaderUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class ReaderUiState {
    private final boolean isLoading = false;
    private final boolean isLoadingComments = false;
    private final int bookId = 0;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Chapter> chapters = null;
    private final int currentChapterIndex = 0;
    private final int fontSize = 0;
    private final boolean isDarkMode = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.Integer, java.lang.Integer> commentCounts = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Integer selectedParagraphIndex = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Comment> comments = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String commentSortBy = null;
    
    public ReaderUiState(boolean isLoading, boolean isLoadingComments, int bookId, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Chapter> chapters, int currentChapterIndex, int fontSize, boolean isDarkMode, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, java.lang.Integer> commentCounts, @org.jetbrains.annotations.Nullable
    java.lang.Integer selectedParagraphIndex, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Comment> comments, @org.jetbrains.annotations.NotNull
    java.lang.String commentSortBy) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean isLoadingComments() {
        return false;
    }
    
    public final int getBookId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Chapter> getChapters() {
        return null;
    }
    
    public final int getCurrentChapterIndex() {
        return 0;
    }
    
    public final int getFontSize() {
        return 0;
    }
    
    public final boolean isDarkMode() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, java.lang.Integer> getCommentCounts() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer getSelectedParagraphIndex() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Comment> getComments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCommentSortBy() {
        return null;
    }
    
    public ReaderUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Comment> component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component11() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    public final int component3() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Chapter> component4() {
        return null;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, java.lang.Integer> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.screens.reader.ReaderUiState copy(boolean isLoading, boolean isLoadingComments, int bookId, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Chapter> chapters, int currentChapterIndex, int fontSize, boolean isDarkMode, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, java.lang.Integer> commentCounts, @org.jetbrains.annotations.Nullable
    java.lang.Integer selectedParagraphIndex, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Comment> comments, @org.jetbrains.annotations.NotNull
    java.lang.String commentSortBy) {
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