package com.ebookstore.user.ui.navigation;

import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.navigation.NavType;
import androidx.navigation.compose.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\b\u0007\b\t\n\u000b\f\r\u000eB\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\b\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen;", "", "route", "", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "BookDetail", "BookList", "Bookshelf", "Home", "Login", "Profile", "Reader", "Register", "Lcom/ebookstore/user/ui/navigation/Screen$BookDetail;", "Lcom/ebookstore/user/ui/navigation/Screen$BookList;", "Lcom/ebookstore/user/ui/navigation/Screen$Bookshelf;", "Lcom/ebookstore/user/ui/navigation/Screen$Home;", "Lcom/ebookstore/user/ui/navigation/Screen$Login;", "Lcom/ebookstore/user/ui/navigation/Screen$Profile;", "Lcom/ebookstore/user/ui/navigation/Screen$Reader;", "Lcom/ebookstore/user/ui/navigation/Screen$Register;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$BookDetail;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "createRoute", "", "bookId", "", "app_debug"})
    public static final class BookDetail extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.BookDetail INSTANCE = null;
        
        private BookDetail() {
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String createRoute(int bookId) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$BookList;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class BookList extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.BookList INSTANCE = null;
        
        private BookList() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Bookshelf;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Bookshelf extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Bookshelf INSTANCE = null;
        
        private Bookshelf() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Home;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Home extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Home INSTANCE = null;
        
        private Home() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Login;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Login extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Login INSTANCE = null;
        
        private Login() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Profile;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Profile extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Profile INSTANCE = null;
        
        private Profile() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0004\u00a8\u0006\b"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Reader;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "createRoute", "", "bookId", "", "chapter", "app_debug"})
    public static final class Reader extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Reader INSTANCE = null;
        
        private Reader() {
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String createRoute(int bookId, @org.jetbrains.annotations.Nullable
        java.lang.String chapter) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/Screen$Register;", "Lcom/ebookstore/user/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Register extends com.ebookstore.user.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.Screen.Register INSTANCE = null;
        
        private Register() {
        }
    }
}