package com.ebookstore.user.data.repository

import com.ebookstore.user.data.local.PreferencesManager
import com.ebookstore.user.data.model.*
import com.ebookstore.user.data.remote.ApiService
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.code == 200 && response.data != null) {
                preferencesManager.saveToken(response.data.token)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "登录失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, nickname: String, email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.register(RegisterRequest(username, nickname, email, password))
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "注册失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(): Result<User> {
        return try {
            val response = apiService.getUserInfo()
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(nickname: String, email: String, avatar: String): Result<User> {
        return try {
            val response = apiService.updateProfile(UpdateProfileRequest(nickname, email, avatar))
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "更新失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<Unit> {
        return try {
            val response = apiService.updatePassword(UpdatePasswordRequest(oldPassword, newPassword, confirmPassword))
            if (response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "修改密码失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        preferencesManager.clearToken()
    }

    suspend fun isLoggedIn(): Boolean {
        return hasToken()
    }

    suspend fun hasToken(): Boolean {
        return !preferencesManager.token.first().isNullOrBlank()
    }

    suspend fun validateSession(): Boolean {
        if (!hasToken()) return false

        return try {
            val response = apiService.getUserInfo()
            when {
                response.code == 200 && response.data != null -> true
                response.code == 401 || response.code == 403 -> {
                    preferencesManager.clearToken()
                    false
                }
                else -> true
            }
        } catch (exception: HttpException) {
            if (exception.code() == 401 || exception.code() == 403) {
                preferencesManager.clearToken()
                false
            } else {
                true
            }
        } catch (exception: Exception) {
            true
        }
    }

    suspend fun getToken(): String? {
        return preferencesManager.token.first()
    }
}
