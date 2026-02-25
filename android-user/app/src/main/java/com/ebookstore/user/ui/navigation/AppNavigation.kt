package com.ebookstore.user.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ebookstore.user.ui.screens.auth.LoginScreen
import com.ebookstore.user.ui.screens.auth.RegisterScreen
import com.ebookstore.user.ui.screens.bookdetail.BookDetailScreen
import com.ebookstore.user.ui.screens.booklist.BookListScreen
import com.ebookstore.user.ui.screens.bookshelf.BookshelfScreen
import com.ebookstore.user.ui.screens.home.HomeScreen
import com.ebookstore.user.ui.screens.profile.ProfileScreen
import com.ebookstore.user.ui.screens.reader.ReaderScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object BookList : Screen("books")
    object Bookshelf : Screen("bookshelf")
    object Profile : Screen("profile")
    object BookDetail : Screen("book/{bookId}") {
        fun createRoute(bookId: Int) = "book/$bookId"
    }
    object Reader : Screen("reader/{bookId}?chapter={chapter}") {
        fun createRoute(bookId: Int, chapter: String? = null) =
            if (chapter != null) "reader/$bookId?chapter=$chapter" else "reader/$bookId"
    }
}

sealed class BottomNavItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem(Screen.Home, "首页", Icons.Default.Home)
    object Books : BottomNavItem(Screen.BookList, "书库", Icons.Default.LibraryBooks)
    object Bookshelf : BottomNavItem(Screen.Bookshelf, "书架", Icons.Default.Bookmarks)
    object Profile : BottomNavItem(Screen.Profile, "我的", Icons.Default.Person)
}

@Composable
fun AppNavigation(
    viewModel: AppNavigationViewModel = hiltViewModel()
) {
    val appNavigationState by viewModel.uiState.collectAsState()
    val startDestination = appNavigationState.startDestination

    if (appNavigationState.isCheckingAuth || startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Books,
        BottomNavItem.Bookshelf,
        BottomNavItem.Profile
    )

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToBookDetail = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNavigateToReader = { bookId, chapter ->
                        navController.navigate(Screen.Reader.createRoute(bookId, chapter))
                    },
                    onNavigateToBooks = {
                        navController.navigate(Screen.BookList.route)
                    }
                )
            }

            composable(Screen.BookList.route) {
                BookListScreen(
                    onNavigateToBookDetail = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }

            composable(Screen.Bookshelf.route) {
                BookshelfScreen(
                    onNavigateToBookDetail = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNavigateToReader = { bookId, chapter ->
                        navController.navigate(Screen.Reader.createRoute(bookId, chapter))
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToReader = { bookId, chapterId, paragraphIndex ->
                        navController.navigate("reader/$bookId?chapterId=$chapterId&paragraphIndex=$paragraphIndex")
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                BookDetailScreen(
                    bookId = bookId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReader = { chapter ->
                        navController.navigate(Screen.Reader.createRoute(bookId, chapter))
                    }
                )
            }

            composable(
                route = Screen.Reader.route,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.IntType },
                    navArgument("chapter") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                val chapter = backStackEntry.arguments?.getString("chapter")
                ReaderScreen(
                    bookId = bookId,
                    initialChapter = chapter,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
