package com.ebookstore.user.data.remote

import com.ebookstore.user.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Unit>

    @GET("user/info")
    suspend fun getUserInfo(): ApiResponse<User>

    @PUT("user/info")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<User>

    @PUT("user/password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): ApiResponse<Unit>

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiResponse<FileUploadResponse>

    // Books
    @GET("book/list")
    suspend fun getBooks(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("order") order: String? = null
    ): ApiResponse<PageResponse<Book>>

    @GET("book/detail/{id}")
    suspend fun getBookDetail(@Path("id") id: Int): ApiResponse<Book>

    @GET("book/read/{id}")
    suspend fun getBookReadUrl(@Path("id") id: Int): ApiResponse<String>

    @GET("book/read-text/{id}")
    suspend fun getBookTextContent(@Path("id") id: Int): ApiResponse<String>

    @GET("book/{id}/chapters")
    suspend fun getChapters(@Path("id") bookId: Int): ApiResponse<List<Chapter>>

    // Favorites
    @POST("book/favorite/{id}")
    suspend fun addFavorite(@Path("id") bookId: Int): ApiResponse<Unit>

    @DELETE("book/favorite/{id}")
    suspend fun removeFavorite(@Path("id") bookId: Int): ApiResponse<Unit>

    @GET("book/favorite/list")
    suspend fun getFavorites(): ApiResponse<List<Book>>

    // History
    @POST("book/history")
    suspend fun saveHistory(@Body history: ReadHistory): ApiResponse<Unit>

    @GET("book/history/list")
    suspend fun getHistory(): ApiResponse<List<ReadHistory>>

    @GET("book/history/preference")
    suspend fun getPreference(): ApiResponse<List<PreferenceStats>>

    // Categories
    @GET("category/tree")
    suspend fun getCategoryTree(): ApiResponse<List<Category>>

    // Comments
    @GET("book/{bookId}/chapter/{chapterId}/comments")
    suspend fun getComments(
        @Path("bookId") bookId: Int,
        @Path("chapterId") chapterId: Int,
        @Query("paragraphIndex") paragraphIndex: Int,
        @Query("sortBy") sortBy: String = "time"
    ): ApiResponse<List<Comment>>

    @POST("book/{bookId}/chapter/{chapterId}/comments")
    suspend fun addComment(
        @Path("bookId") bookId: Int,
        @Path("chapterId") chapterId: Int,
        @Body request: CommentRequest
    ): ApiResponse<Unit>

    @GET("book/{bookId}/chapter/{chapterId}/comment-counts")
    suspend fun getCommentCounts(
        @Path("bookId") bookId: Int,
        @Path("chapterId") chapterId: Int
    ): ApiResponse<Map<String, Int>>

    @POST("book/comments/{id}/reaction")
    suspend fun reactToComment(
        @Path("id") commentId: Int,
        @Body request: ReactionRequest
    ): ApiResponse<Unit>

    @POST("book/comments/{id}/hide")
    suspend fun hideComment(@Path("id") commentId: Int): ApiResponse<Unit>

    @DELETE("book/comments/{id}/hide")
    suspend fun unhideComment(@Path("id") commentId: Int): ApiResponse<Unit>

    // Notices
    @GET("book/notice/list")
    suspend fun getNotices(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PageResponse<Notice>>

    @POST("book/notice/read/{id}")
    suspend fun markNoticeRead(@Path("id") id: Int): ApiResponse<Unit>

    @POST("book/notice/read/all")
    suspend fun markAllNoticesRead(): ApiResponse<Unit>

    // Carousel
    @GET("carousel/list")
    suspend fun getCarousels(): ApiResponse<List<Carousel>>

    // Urge
    @POST("book/{id}/urge")
    suspend fun urgeUpdate(@Path("id") bookId: Int): ApiResponse<Unit>
}
