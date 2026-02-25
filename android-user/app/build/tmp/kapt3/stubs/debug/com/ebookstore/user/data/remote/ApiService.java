package com.ebookstore.user.data.remote;

import com.ebookstore.user.data.model.*;
import retrofit2.http.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00aa\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J2\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u000f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u000f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u000f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ^\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00140\u00032\b\b\u0001\u0010\u0015\u001a\u00020\u00062\b\b\u0001\u0010\u0016\u001a\u00020\u00062\n\b\u0003\u0010\u0017\u001a\u0004\u0018\u00010\u00112\n\b\u0003\u0010\u0018\u001a\u0004\u0018\u00010\u00062\n\b\u0003\u0010\u0019\u001a\u0004\u0018\u00010\u00112\n\b\u0003\u0010\u001a\u001a\u0004\u0018\u00010\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u001bJ\u001a\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u001d0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001a\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u001d0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ$\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020#0\u001d0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ4\u0010$\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00060%0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010&JB\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u001d0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u00062\b\b\u0001\u0010)\u001a\u00020\u00062\b\b\u0003\u0010\u0019\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010*J\u001a\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u001d0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001a\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020-0\u001d0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ.\u0010.\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020/0\u00140\u00032\b\b\u0001\u0010\u0015\u001a\u00020\u00062\b\b\u0001\u0010\u0016\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010&J\u001a\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u001d0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u0014\u00102\u001a\b\u0012\u0004\u0012\u0002030\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u00104\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u00105\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u00106\u001a\b\u0012\u0004\u0012\u0002070\u00032\b\b\u0001\u0010\b\u001a\u000208H\u00a7@\u00a2\u0006\u0002\u00109J\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ(\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u00105\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020=H\u00a7@\u00a2\u0006\u0002\u0010>J\u001e\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\b\u001a\u00020@H\u00a7@\u00a2\u0006\u0002\u0010AJ\u001e\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010C\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010D\u001a\u00020-H\u00a7@\u00a2\u0006\u0002\u0010EJ\u001e\u0010F\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u00105\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010G\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\b\u001a\u00020HH\u00a7@\u00a2\u0006\u0002\u0010IJ\u001e\u0010J\u001a\b\u0012\u0004\u0012\u0002030\u00032\b\b\u0001\u0010\b\u001a\u00020KH\u00a7@\u00a2\u0006\u0002\u0010LJ\u001e\u0010M\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\f\u00a8\u0006N"}, d2 = {"Lcom/ebookstore/user/data/remote/ApiService;", "", "addComment", "Lcom/ebookstore/user/data/model/ApiResponse;", "", "bookId", "", "chapterId", "request", "Lcom/ebookstore/user/data/model/CommentRequest;", "(IILcom/ebookstore/user/data/model/CommentRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addFavorite", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBookDetail", "Lcom/ebookstore/user/data/model/Book;", "id", "getBookReadUrl", "", "getBookTextContent", "getBooks", "Lcom/ebookstore/user/data/model/PageResponse;", "page", "size", "keyword", "categoryId", "sortBy", "order", "(IILjava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCarousels", "", "Lcom/ebookstore/user/data/model/Carousel;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCategoryTree", "Lcom/ebookstore/user/data/model/Category;", "getChapters", "Lcom/ebookstore/user/data/model/Chapter;", "getCommentCounts", "", "(IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getComments", "Lcom/ebookstore/user/data/model/Comment;", "paragraphIndex", "(IIILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getFavorites", "getHistory", "Lcom/ebookstore/user/data/model/ReadHistory;", "getNotices", "Lcom/ebookstore/user/data/model/Notice;", "getPreference", "Lcom/ebookstore/user/data/model/PreferenceStats;", "getUserInfo", "Lcom/ebookstore/user/data/model/User;", "hideComment", "commentId", "login", "Lcom/ebookstore/user/data/model/LoginResponse;", "Lcom/ebookstore/user/data/model/LoginRequest;", "(Lcom/ebookstore/user/data/model/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAllNoticesRead", "markNoticeRead", "reactToComment", "Lcom/ebookstore/user/data/model/ReactionRequest;", "(ILcom/ebookstore/user/data/model/ReactionRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "Lcom/ebookstore/user/data/model/RegisterRequest;", "(Lcom/ebookstore/user/data/model/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFavorite", "saveHistory", "history", "(Lcom/ebookstore/user/data/model/ReadHistory;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unhideComment", "updatePassword", "Lcom/ebookstore/user/data/model/UpdatePasswordRequest;", "(Lcom/ebookstore/user/data/model/UpdatePasswordRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfile", "Lcom/ebookstore/user/data/model/UpdateProfileRequest;", "(Lcom/ebookstore/user/data/model/UpdateProfileRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "urgeUpdate", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.POST(value = "user/login")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object login(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.LoginRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "user/register")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object register(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.RegisterRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "user/info")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getUserInfo(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.User>> $completion);
    
    @retrofit2.http.PUT(value = "user/info")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object updateProfile(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.UpdateProfileRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.User>> $completion);
    
    @retrofit2.http.PUT(value = "user/password")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object updatePassword(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.UpdatePasswordRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "book/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getBooks(@retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "size")
    int size, @retrofit2.http.Query(value = "keyword")
    @org.jetbrains.annotations.Nullable
    java.lang.String keyword, @retrofit2.http.Query(value = "categoryId")
    @org.jetbrains.annotations.Nullable
    java.lang.Integer categoryId, @retrofit2.http.Query(value = "sortBy")
    @org.jetbrains.annotations.Nullable
    java.lang.String sortBy, @retrofit2.http.Query(value = "order")
    @org.jetbrains.annotations.Nullable
    java.lang.String order, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.PageResponse<com.ebookstore.user.data.model.Book>>> $completion);
    
    @retrofit2.http.GET(value = "book/detail/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getBookDetail(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.Book>> $completion);
    
    @retrofit2.http.GET(value = "book/read/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getBookReadUrl(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.lang.String>> $completion);
    
    @retrofit2.http.GET(value = "book/read-text/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getBookTextContent(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.lang.String>> $completion);
    
    @retrofit2.http.GET(value = "book/{id}/chapters")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getChapters(@retrofit2.http.Path(value = "id")
    int bookId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.Chapter>>> $completion);
    
    @retrofit2.http.POST(value = "book/favorite/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object addFavorite(@retrofit2.http.Path(value = "id")
    int bookId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.DELETE(value = "book/favorite/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object removeFavorite(@retrofit2.http.Path(value = "id")
    int bookId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "book/favorite/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getFavorites(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.Book>>> $completion);
    
    @retrofit2.http.POST(value = "book/history")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object saveHistory(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.ReadHistory history, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "book/history/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getHistory(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.ReadHistory>>> $completion);
    
    @retrofit2.http.GET(value = "book/history/preference")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPreference(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.PreferenceStats>>> $completion);
    
    @retrofit2.http.GET(value = "category/tree")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCategoryTree(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.Category>>> $completion);
    
    @retrofit2.http.GET(value = "book/{bookId}/chapter/{chapterId}/comments")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getComments(@retrofit2.http.Path(value = "bookId")
    int bookId, @retrofit2.http.Path(value = "chapterId")
    int chapterId, @retrofit2.http.Query(value = "paragraphIndex")
    int paragraphIndex, @retrofit2.http.Query(value = "sortBy")
    @org.jetbrains.annotations.NotNull
    java.lang.String sortBy, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.Comment>>> $completion);
    
    @retrofit2.http.POST(value = "book/{bookId}/chapter/{chapterId}/comments")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object addComment(@retrofit2.http.Path(value = "bookId")
    int bookId, @retrofit2.http.Path(value = "chapterId")
    int chapterId, @retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.CommentRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "book/{bookId}/chapter/{chapterId}/comment-counts")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCommentCounts(@retrofit2.http.Path(value = "bookId")
    int bookId, @retrofit2.http.Path(value = "chapterId")
    int chapterId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.Map<java.lang.String, java.lang.Integer>>> $completion);
    
    @retrofit2.http.POST(value = "book/comments/{id}/reaction")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object reactToComment(@retrofit2.http.Path(value = "id")
    int commentId, @retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.model.ReactionRequest request, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.POST(value = "book/comments/{id}/hide")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object hideComment(@retrofit2.http.Path(value = "id")
    int commentId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.DELETE(value = "book/comments/{id}/hide")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object unhideComment(@retrofit2.http.Path(value = "id")
    int commentId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "book/notice/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getNotices(@retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "size")
    int size, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<com.ebookstore.user.data.model.PageResponse<com.ebookstore.user.data.model.Notice>>> $completion);
    
    @retrofit2.http.POST(value = "book/notice/read/{id}")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markNoticeRead(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.POST(value = "book/notice/read/all")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markAllNoticesRead(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "carousel/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCarousels(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<java.util.List<com.ebookstore.user.data.model.Carousel>>> $completion);
    
    @retrofit2.http.POST(value = "book/{id}/urge")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object urgeUpdate(@retrofit2.http.Path(value = "id")
    int bookId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.ebookstore.user.data.model.ApiResponse<kotlin.Unit>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}