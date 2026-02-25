package com.ebookstore.user.ui.screens.reader

import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderModeProgressTest {

    @Test
    fun calculateReadingProgress_pagedMode_usesPageProgressWithinChapter() {
        val progress = calculateReadingProgress(
            mode = ReaderMode.PAGED,
            currentChapterIndex = 1,
            chapterCount = 4,
            currentPage = 1,
            pageCount = 4
        )

        assertEquals(0.375f, progress, 0.0001f)
    }

    @Test
    fun calculateReadingProgress_scrollMode_usesChapterProgress() {
        val progress = calculateReadingProgress(
            mode = ReaderMode.SCROLL,
            currentChapterIndex = 1,
            chapterCount = 4,
            currentPage = 3,
            pageCount = 8
        )

        assertEquals(0.5f, progress, 0.0001f)
    }
}
