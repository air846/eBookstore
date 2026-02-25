package com.ebookstore.user.ui.navigation

suspend fun resolveInitialRoute(
    hasToken: suspend () -> Boolean,
    validateSession: suspend () -> Boolean
): String {
    if (!hasToken()) {
        return Screen.Login.route
    }

    return if (validateSession()) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }
}
