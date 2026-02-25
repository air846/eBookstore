package com.ebookstore.user.ui.screens.profile;

import androidx.lifecycle.ViewModel;
import com.ebookstore.user.data.model.Book;
import com.ebookstore.user.data.model.Notice;
import com.ebookstore.user.data.model.PreferenceStats;
import com.ebookstore.user.data.model.ReadHistory;
import com.ebookstore.user.data.model.User;
import com.ebookstore.user.data.repository.AuthRepository;
import com.ebookstore.user.data.repository.BookRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0014\u0010\u0010\u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0012J\u0006\u0010\u0013\u001a\u00020\u000fJ\u000e\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001b\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u0014\u0010\u001f\u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0012J\u0014\u0010 \u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0012R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006!"}, d2 = {"Lcom/ebookstore/user/ui/screens/profile/ProfileViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lcom/ebookstore/user/data/repository/AuthRepository;", "bookRepository", "Lcom/ebookstore/user/data/repository/BookRepository;", "(Lcom/ebookstore/user/data/repository/AuthRepository;Lcom/ebookstore/user/data/repository/BookRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ebookstore/user/ui/screens/profile/ProfileUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadData", "", "logout", "onSuccess", "Lkotlin/Function0;", "markAllNoticesRead", "markNoticeRead", "id", "", "onAvatarChange", "value", "", "onConfirmPasswordChange", "onEmailChange", "onNewPasswordChange", "onNicknameChange", "onOldPasswordChange", "updatePassword", "updateProfile", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class ProfileViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ebookstore.user.ui.screens.profile.ProfileUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.profile.ProfileUiState> uiState = null;
    
    @javax.inject.Inject
    public ProfileViewModel(@org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull
    com.ebookstore.user.data.repository.BookRepository bookRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ebookstore.user.ui.screens.profile.ProfileUiState> getUiState() {
        return null;
    }
    
    public final void loadData() {
    }
    
    public final void updateProfile(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void updatePassword(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void markNoticeRead(int id) {
    }
    
    public final void markAllNoticesRead() {
    }
    
    public final void logout(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void onNicknameChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
    
    public final void onEmailChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
    
    public final void onAvatarChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
    
    public final void onOldPasswordChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
    
    public final void onNewPasswordChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
    
    public final void onConfirmPasswordChange(@org.jetbrains.annotations.NotNull
    java.lang.String value) {
    }
}