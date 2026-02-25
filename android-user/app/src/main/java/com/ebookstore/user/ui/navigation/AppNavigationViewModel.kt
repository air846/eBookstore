package com.ebookstore.user.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookstore.user.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppNavigationUiState())
    val uiState: StateFlow<AppNavigationUiState> = _uiState.asStateFlow()

    init {
        resolveStartupDestination()
    }

    private fun resolveStartupDestination() {
        viewModelScope.launch {
            val startDestination = resolveInitialRoute(
                hasToken = { authRepository.hasToken() },
                validateSession = { authRepository.validateSession() }
            )
            _uiState.value = AppNavigationUiState(
                isCheckingAuth = false,
                startDestination = startDestination
            )
        }
    }
}

data class AppNavigationUiState(
    val isCheckingAuth: Boolean = true,
    val startDestination: String? = null
)
