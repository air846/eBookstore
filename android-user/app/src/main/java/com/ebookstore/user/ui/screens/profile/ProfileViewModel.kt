package com.ebookstore.user.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.model.Book
import com.ebookstore.user.data.model.Notice
import com.ebookstore.user.data.model.PreferenceStats
import com.ebookstore.user.data.model.ReadHistory
import com.ebookstore.user.data.model.User
import com.ebookstore.user.data.repository.AuthRepository
import com.ebookstore.user.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val userResult = authRepository.getUserInfo()
            val favoritesResult = bookRepository.getFavorites()
            val historyResult = bookRepository.getHistory()
            val preferenceResult = bookRepository.getPreference()
            val noticesResult = bookRepository.getNotices(1, 5)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                user = userResult.getOrNull(),
                favorites = favoritesResult.getOrNull() ?: emptyList(),
                history = historyResult.getOrNull() ?: emptyList(),
                preference = preferenceResult.getOrNull() ?: emptyList(),
                notices = noticesResult.getOrNull()?.records ?: emptyList()
            )

            userResult.getOrNull()?.let { user ->
                _uiState.value = _uiState.value.copy(
                    nickname = user.nickname,
                    email = user.email ?: "",
                    avatar = user.avatar ?: ""
                )
            }
        }
    }

    fun updateProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            authRepository.updateProfile(
                _uiState.value.nickname,
                _uiState.value.email,
                _uiState.value.avatar
            )
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        user = user
                    )
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
        }
    }

    fun updatePassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            authRepository.updatePassword(
                _uiState.value.oldPassword,
                _uiState.value.newPassword,
                _uiState.value.confirmPassword
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        oldPassword = "",
                        newPassword = "",
                        confirmPassword = ""
                    )
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                }
        }
    }

    fun markNoticeRead(id: Int) {
        viewModelScope.launch {
            bookRepository.markNoticeRead(id)
            loadData()
        }
    }

    fun markAllNoticesRead() {
        viewModelScope.launch {
            bookRepository.markAllNoticesRead()
            loadData()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }

    fun onNicknameChange(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onAvatarChange(value: String) {
        _uiState.value = _uiState.value.copy(avatar = value)
    }

    fun onOldPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(oldPassword = value)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val user: User? = null,
    val favorites: List<Book> = emptyList(),
    val history: List<ReadHistory> = emptyList(),
    val preference: List<PreferenceStats> = emptyList(),
    val notices: List<Notice> = emptyList(),
    val nickname: String = "",
    val email: String = "",
    val avatar: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)
