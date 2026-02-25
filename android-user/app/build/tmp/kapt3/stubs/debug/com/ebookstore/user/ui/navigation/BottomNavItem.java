package com.ebookstore.user.ui.navigation;

import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.navigation.NavType;
import androidx.navigation.compose.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u000f\u0010\u0011\u0012B\u001f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u0082\u0001\u0004\u0013\u0014\u0015\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/ebookstore/user/ui/navigation/BottomNavItem;", "", "screen", "Lcom/ebookstore/user/ui/navigation/Screen;", "label", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "(Lcom/ebookstore/user/ui/navigation/Screen;Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;)V", "getIcon", "()Landroidx/compose/ui/graphics/vector/ImageVector;", "getLabel", "()Ljava/lang/String;", "getScreen", "()Lcom/ebookstore/user/ui/navigation/Screen;", "Books", "Bookshelf", "Home", "Profile", "Lcom/ebookstore/user/ui/navigation/BottomNavItem$Books;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem$Bookshelf;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem$Home;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem$Profile;", "app_debug"})
public abstract class BottomNavItem {
    @org.jetbrains.annotations.NotNull
    private final com.ebookstore.user.ui.navigation.Screen screen = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String label = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.ui.graphics.vector.ImageVector icon = null;
    
    private BottomNavItem(com.ebookstore.user.ui.navigation.Screen screen, java.lang.String label, androidx.compose.ui.graphics.vector.ImageVector icon) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ebookstore.user.ui.navigation.Screen getScreen() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.compose.ui.graphics.vector.ImageVector getIcon() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/BottomNavItem$Books;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem;", "()V", "app_debug"})
    public static final class Books extends com.ebookstore.user.ui.navigation.BottomNavItem {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.BottomNavItem.Books INSTANCE = null;
        
        private Books() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/BottomNavItem$Bookshelf;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem;", "()V", "app_debug"})
    public static final class Bookshelf extends com.ebookstore.user.ui.navigation.BottomNavItem {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.BottomNavItem.Bookshelf INSTANCE = null;
        
        private Bookshelf() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/BottomNavItem$Home;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem;", "()V", "app_debug"})
    public static final class Home extends com.ebookstore.user.ui.navigation.BottomNavItem {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.BottomNavItem.Home INSTANCE = null;
        
        private Home() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/ebookstore/user/ui/navigation/BottomNavItem$Profile;", "Lcom/ebookstore/user/ui/navigation/BottomNavItem;", "()V", "app_debug"})
    public static final class Profile extends com.ebookstore.user.ui.navigation.BottomNavItem {
        @org.jetbrains.annotations.NotNull
        public static final com.ebookstore.user.ui.navigation.BottomNavItem.Profile INSTANCE = null;
        
        private Profile() {
        }
    }
}