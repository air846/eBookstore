package com.ebookstore.user.ui.screens.reader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderPaginationTest {

    @Test
    fun paginateReaderParagraphs_splitsLongParagraphsAndKeepsOrder() {
        val paragraphs = List(20) { index ->
            "第${index}段 " + "内容".repeat(120)
        }

        val pages = paginateReaderParagraphs(paragraphs, fontSize = 18)

        assertTrue(pages.size > 1)
        assertEquals(paragraphs.size, pages.sumOf { it.size })
        assertEquals(0, pages.first().first().index)
        assertEquals(paragraphs.lastIndex, pages.last().last().index)
    }

    @Test
    fun paginateReaderParagraphs_usesMorePagesForLargerFont() {
        val paragraphs = List(15) { "测试段落 " + "A".repeat(200) }

        val smallFontPages = paginateReaderParagraphs(paragraphs, fontSize = 14).size
        val largeFontPages = paginateReaderParagraphs(paragraphs, fontSize = 28).size

        assertTrue(largeFontPages >= smallFontPages)
    }

    @Test
    fun paginateReaderParagraphs_firstPageReservesSpaceForChapterTitle() {
        val paragraphs = List(24) { "段落$it " + "内容".repeat(30) }

        val pages = paginateReaderParagraphs(paragraphs, fontSize = 18)

        assertTrue(pages.size > 1)
        assertTrue(pages.first().size < pages[1].size)
    }
}
