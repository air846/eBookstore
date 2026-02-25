package com.ebookstore.user.ui.screens.reader

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderLayoutPaddingTest {

    @Test
    fun mergePaddingWithSafeArea_usesLargerBottomInsetWhenSystemInsetExists() {
        val mergedPadding = mergePaddingWithSafeArea(
            basePadding = PaddingValues(start = 4.dp, top = 8.dp, end = 12.dp, bottom = 0.dp),
            safePadding = PaddingValues(bottom = 24.dp),
            layoutDirection = LayoutDirection.Ltr
        )

        assertEquals(4.dp, mergedPadding.calculateStartPadding(LayoutDirection.Ltr))
        assertEquals(8.dp, mergedPadding.calculateTopPadding())
        assertEquals(12.dp, mergedPadding.calculateEndPadding(LayoutDirection.Ltr))
        assertEquals(24.dp, mergedPadding.calculateBottomPadding())
    }

    @Test
    fun mergePaddingWithSafeArea_respectsLayoutDirectionWhenComparingStartEnd() {
        val mergedPadding = mergePaddingWithSafeArea(
            basePadding = PaddingValues(start = 10.dp, end = 2.dp),
            safePadding = PaddingValues(start = 1.dp, end = 20.dp),
            layoutDirection = LayoutDirection.Rtl
        )

        assertEquals(20.dp, mergedPadding.calculateStartPadding(LayoutDirection.Rtl))
        assertEquals(10.dp, mergedPadding.calculateEndPadding(LayoutDirection.Rtl))
    }
}
