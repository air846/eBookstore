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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b&\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u00a1\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\b\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\b\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0017J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0011H\u00c6\u0003J\t\u0010)\u001a\u00020\u0011H\u00c6\u0003J\t\u0010*\u001a\u00020\u0011H\u00c6\u0003J\t\u0010+\u001a\u00020\u0011H\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\u000f\u0010.\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J\u000f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH\u00c6\u0003J\u000f\u00100\u001a\b\u0012\u0004\u0012\u00020\r0\bH\u00c6\u0003J\u000f\u00101\u001a\b\u0012\u0004\u0012\u00020\u000f0\bH\u00c6\u0003J\t\u00102\u001a\u00020\u0011H\u00c6\u0003J\t\u00103\u001a\u00020\u0011H\u00c6\u0003J\u00a5\u0001\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\b2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\b2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u00112\b\b\u0002\u0010\u0014\u001a\u00020\u00112\b\b\u0002\u0010\u0015\u001a\u00020\u00112\b\b\u0002\u0010\u0016\u001a\u00020\u0011H\u00c6\u0001J\u0013\u00105\u001a\u00020\u00032\b\u00106\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00107\u001a\u000208H\u00d6\u0001J\t\u00109\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0013\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0016\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0011\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u001fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001fR\u0011\u0010\u0015\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0019R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0019R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001dR\u0011\u0010\u0014\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0019R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001dR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&\u00a8\u0006:"}, d2 = {"Lcom/ebookstore/user/ui/screens/profile/ProfileUiState;", "", "isLoading", "", "isSaving", "user", "Lcom/ebookstore/user/data/model/User;", "favorites", "", "Lcom/ebookstore/user/data/model/Book;", "history", "Lcom/ebookstore/user/data/model/ReadHistory;", "preference", "Lcom/ebookstore/user/data/model/PreferenceStats;", "notices", "Lcom/ebookstore/user/data/model/Notice;", "nickname", "", "email", "avatar", "oldPassword", "newPassword", "confirmPassword", "(ZZLcom/ebookstore/user/data/model/User;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAvatar", "()Ljava/lang/String;", "getConfirmPassword", "getEmail", "getFavorites", "()Ljava/util/List;", "getHistory", "()Z", "getNewPassword", "getNickname", "getNotices", "getOldPassword", "getPreference", "getUser", "()Lcom/ebookstore/user/data/model/User;", "component1", "component10", "component11", "component12", "component13", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class ProfileUiState {
    private final boolean isLoading = false;
    private final boolean isSaving = false;
    @org.jetbrains.annotations.Nullable
    private final com.ebookstore.user.data.model.User user = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Book> favorites = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.ReadHistory> history = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.PreferenceStats> preference = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.ebookstore.user.data.model.Notice> notices = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String nickname = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String email = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String avatar = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String oldPassword = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String newPassword = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String confirmPassword = null;
    
    public ProfileUiState(boolean isLoading, boolean isSaving, @org.jetbrains.annotations.Nullable
    com.ebookstore.user.data.model.User user, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> favorites, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.ReadHistory> history, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.PreferenceStats> preference, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Notice> notices, @org.jetbrains.annotations.NotNull
    java.lang.String nickname, @org.jetbrains.annotations.NotNull
    java.lang.String email, @org.jetbrains.annotations.NotNull
    java.lang.String avatar, @org.jetbrains.annotations.NotNull
    java.lang.String oldPassword, @org.jetbrains.annotations.NotNull
    java.lang.String newPassword, @org.jetbrains.annotations.NotNull
    java.lang.String confirmPassword) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean isSaving() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.ebookstore.user.data.model.User getUser() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> getFavorites() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.ReadHistory> getHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.PreferenceStats> getPreference() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Notice> getNotices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getNickname() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAvatar() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getOldPassword() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getNewPassword() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getConfirmPassword() {
        return null;
    }
    
    public ProfileUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component13() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.ebookstore.user.data.model.User component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Book> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.ReadHistory> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.PreferenceStats> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.ebookstore.user.data.model.Notice> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.screens.profile.ProfileUiState copy(boolean isLoading, boolean isSaving, @org.jetbrains.annotations.Nullable
    com.ebookstore.user.data.model.User user, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Book> favorites, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.ReadHistory> history, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.PreferenceStats> preference, @org.jetbrains.annotations.NotNull
    java.util.List<com.ebookstore.user.data.model.Notice> notices, @org.jetbrains.annotations.NotNull
    java.lang.String nickname, @org.jetbrains.annotations.NotNull
    java.lang.String email, @org.jetbrains.annotations.NotNull
    java.lang.String avatar, @org.jetbrains.annotations.NotNull
    java.lang.String oldPassword, @org.jetbrains.annotations.NotNull
    java.lang.String newPassword, @org.jetbrains.annotations.NotNull
    java.lang.String confirmPassword) {
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