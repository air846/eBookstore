package com.ebookstore.user.ui.navigation

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigationAuthResolverTest {

    @Test
    fun resolveInitialRoute_noToken_startsAtLogin() = runBlocking {
        var validateCalled = false

        val route = resolveInitialRoute(
            hasToken = { false },
            validateSession = {
                validateCalled = true
                true
            }
        )

        assertEquals(Screen.Login.route, route)
        assertFalse(validateCalled)
    }

    @Test
    fun resolveInitialRoute_validToken_startsAtHome() = runBlocking {
        val route = resolveInitialRoute(
            hasToken = { true },
            validateSession = { true }
        )

        assertEquals(Screen.Home.route, route)
    }

    @Test
    fun resolveInitialRoute_invalidToken_startsAtLogin() = runBlocking {
        val route = resolveInitialRoute(
            hasToken = { true },
            validateSession = { false }
        )

        assertEquals(Screen.Login.route, route)
    }
}
