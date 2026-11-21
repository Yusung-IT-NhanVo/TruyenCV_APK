package com.truyencv.app

import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase0DomainModelTest {

    @Test
    fun testSourceFromIdPrefix() {
        assertEquals(Source.TRUYENCV, Source.fromId("tcv-12345"))
        assertEquals(Source.MOTTRUYEN, Source.fromId("mt-67890"))
        // Fallback for unknown prefix
        assertEquals(Source.TRUYENCV, Source.fromId("unknown-999"))
    }

    @Test
    fun testSourceProperties() {
        assertEquals("tcv-", Source.TRUYENCV.prefix)
        assertEquals("mt-", Source.MOTTRUYEN.prefix)
        assertEquals("TruyenCV.io", Source.TRUYENCV.label)
        assertEquals("MotTruyen", Source.MOTTRUYEN.label)
    }

    @Test
    fun testNovelModelCreation() {
        val novel = Novel(
            id = "tcv-100",
            rawId = 100L,
            title = "Đại Quản Gia Là Ma Hoàng",
            author = "Dạ Kiêu",
            thumbUrl = "https://static.cdnno.com/poster/100.jpg",
            description = "Mô tả truyện",
            genres = listOf("Tiên Hiệp", "Huyền Huyễn"),
            totalChapters = 1314,
            views = 500000L,
            status = NovelStatus.ONGOING,
            source = Source.TRUYENCV
        )

        assertEquals("tcv-100", novel.id)
        assertEquals(100L, novel.rawId)
        assertEquals("Đại Quản Gia Là Ma Hoàng", novel.title)
        assertEquals(NovelStatus.ONGOING, novel.status)
        assertEquals(Source.TRUYENCV, novel.source)
        assertEquals(2, novel.genres.size)
    }

    @Test
    fun testChapterModel() {
        val chapter = Chapter(
            id = "tcv-c1",
            rawId = 1L,
            novelId = "tcv-100",
            chapterOrder = 1,
            title = "Chương 1: Trọng sinh",
            rawTitle = "1: Trọng sinh",
            content = "Nội dung chương 1...",
            prevId = null,
            nextId = "tcv-c2",
            source = Source.TRUYENCV,
            isDownloaded = false
        )

        assertEquals("tcv-c1", chapter.id)
        assertEquals(1, chapter.chapterOrder)
        assertEquals("Chương 1: Trọng sinh", chapter.title)
        assertFalse(chapter.isDownloaded)
        assertEquals("tcv-c2", chapter.nextId)
    }

    @Test
    fun testReadingProgress() {
        val progress = ReadingProgress(
            novelId = "tcv-100",
            chapterId = "tcv-c5",
            chapterOrder = 5,
            chapterTitle = "Chương 5",
            scrollPosition = 350
        )

        assertEquals("tcv-100", progress.novelId)
        assertEquals("tcv-c5", progress.chapterId)
        assertEquals(5, progress.chapterOrder)
        assertEquals(350, progress.scrollPosition)
        assertTrue(progress.lastReadTime > 0)
    }

    @Test
    fun testRankingPeriod() {
        assertEquals("day", RankingPeriod.DAY.value)
        assertEquals("week", RankingPeriod.WEEK.value)
        assertEquals("month", RankingPeriod.MONTH.value)
        assertEquals("all", RankingPeriod.ALL.value)
    }

    @Test
    fun testSearchQueryDefaults() {
        val query = SearchQuery(keyword = "Ma Hoàng")
        assertEquals("Ma Hoàng", query.keyword)
        assertEquals(1, query.page)
        assertEquals("modified", query.sort)
    }
}
