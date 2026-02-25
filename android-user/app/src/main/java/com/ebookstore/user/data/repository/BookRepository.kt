package com.ebookstore.user.data.repository

import com.ebookstore.user.data.model.*
import com.ebookstore.user.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getBooks(
        page: Int,
        size: Int,
        keyword: String? = null,
        categoryId: Int? = null,
        sortBy: String? = null,
        order: String? = null
    ): Result<PageResponse<Book>> {
        return try {
            val response = apiService.getBooks(page, size, keyword, categoryId, sortBy, order)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取书籍列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookDetail(id: Int): Result<Book> {
        return try {
            val response = apiService.getBookDetail(id)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取书籍详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChapters(bookId: Int): Result<List<Chapter>> {
        return try {
            val response = apiService.getChapters(bookId)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取章节失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookTextContent(bookId: Int): Result<String> {
        return try {
            val response = apiService.getBookTextContent(bookId)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取文本内容失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(bookId: Int): Result<Unit> {
        return try {
            val response = apiService.addFavorite(bookId)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "收藏失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(bookId: Int): Result<Unit> {
        return try {
            val response = apiService.removeFavorite(bookId)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "移除收藏失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(): Result<List<Book>> {
        return try {
            val response = apiService.getFavorites()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取收藏列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveHistory(bookId: Int, chapter: String, progress: String): Result<Unit> {
        return try {
            val response = apiService.saveHistory(ReadHistory(bookId, chapter, progress))
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "保存历史失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(): Result<List<ReadHistory>> {
        return try {
            val response = apiService.getHistory()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取历史失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookHistory(bookId: Int): Result<ReadHistory?> {
        return try {
            val response = apiService.getHistory()
            if (response.code == 200 && response.data != null) {
                val history = response.data.find { it.bookId == bookId }
                Result.success(history)
            } else {
                Result.failure(Exception(response.message ?: "获取历史失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryTree(): Result<List<Category>> {
        return try {
            val response = apiService.getCategoryTree()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取分类失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCarousels(): Result<List<Carousel>> {
        return try {
            val response = apiService.getCarousels()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取轮播图失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(bookId: Int, chapterId: Int, paragraphIndex: Int, sortBy: String): Result<List<Comment>> {
        return try {
            val response = apiService.getComments(bookId, chapterId, paragraphIndex, sortBy)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取评论失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(bookId: Int, chapterId: Int, paragraphIndex: Int, content: String, parentId: Int?): Result<Unit> {
        return try {
            val response = apiService.addComment(bookId, chapterId, CommentRequest(paragraphIndex, content, parentId))
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "评论失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommentCounts(bookId: Int, chapterId: Int): Result<Map<Int, Int>> {
        return try {
            val response = apiService.getCommentCounts(bookId, chapterId)
            if (response.code == 200 && response.data != null) {
                val counts = response.data.mapKeys { it.key.toIntOrNull() ?: 0 }
                Result.success(counts)
            } else {
                Result.failure(Exception(response.message ?: "获取评论数失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reactToComment(commentId: Int, value: Int): Result<Unit> {
        return try {
            val response = apiService.reactToComment(commentId, ReactionRequest(value))
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "操作失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun hideComment(commentId: Int): Result<Unit> {
        return try {
            val response = apiService.hideComment(commentId)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "折叠失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unhideComment(commentId: Int): Result<Unit> {
        return try {
            val response = apiService.unhideComment(commentId)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "展开失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun urgeUpdate(bookId: Int): Result<Unit> {
        return try {
            val response = apiService.urgeUpdate(bookId)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "催更失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPreference(): Result<List<PreferenceStats>> {
        return try {
            val response = apiService.getPreference()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取偏好统计失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotices(page: Int, size: Int): Result<PageResponse<Notice>> {
        return try {
            val response = apiService.getNotices(page, size)
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取通知失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNoticeRead(id: Int): Result<Unit> {
        return try {
            val response = apiService.markNoticeRead(id)
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "标记失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllNoticesRead(): Result<Unit> {
        return try {
            val response = apiService.markAllNoticesRead()
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "标记失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
