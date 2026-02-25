package com.ebookstore.user.data.model

data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T?
)

data class PageResponse<T>(
    val records: List<T>,
    val total: Long,
    val size: Int,
    val current: Int
)

data class User(
    val id: Int? = null,
    val userId: Int? = null,
    val username: String,
    val nickname: String,
    val email: String? = null,
    val role: Int,
    val avatar: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val id: Int?,
    val userId: Int?,
    val username: String,
    val nickname: String,
    val email: String?,
    val role: Int,
    val avatar: String?
)

data class RegisterRequest(
    val username: String,
    val nickname: String,
    val email: String,
    val password: String
)

data class Book(
    val id: Int,
    val title: String,
    val author: String? = null,
    val publisher: String? = null,
    val isbn: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val fileType: String? = null,
    val fileUrl: String? = null,
    val visitCount: Int? = null,
    val createTime: String? = null
)

data class Category(
    val id: Int,
    val name: String,
    val children: List<Category>? = null
)

data class Chapter(
    val id: Int,
    val bookId: Int,
    val title: String,
    val content: String,
    val sortOrder: Int
)

data class ReadHistory(
    val bookId: Int,
    val chapter: String? = null,
    val progress: String? = null,
    val readTime: String? = null
)

data class Comment(
    val id: Int,
    val userId: Int,
    val content: String,
    val likeCount: Int? = null,
    val dislikeCount: Int? = null,
    val userReaction: Int? = null,
    val hidden: Boolean = false,
    val replies: List<Comment>? = null
)

data class CommentRequest(
    val paragraphIndex: Int,
    val content: String,
    val parentId: Int? = null
)

data class ReactionRequest(
    val value: Int
)

data class Notice(
    val id: Int,
    val type: Int,
    val bookId: Int,
    val bookTitle: String? = null,
    val chapterId: Int? = null,
    val paragraphIndex: Int? = null,
    val readFlag: Int
)

data class Carousel(
    val id: Int,
    val imageUrl: String,
    val title: String? = null,
    val subtitle: String? = null
)

data class PreferenceStats(
    val name: String,
    val value: Int
)

data class UpdateProfileRequest(
    val nickname: String,
    val email: String,
    val avatar: String
)

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
