package com.truyencv.app.data.mapper

import com.truyencv.app.data.remote.mottruyen.MotTruyenChapterContentData
import com.truyencv.app.data.remote.mottruyen.MotTruyenChapterItem
import com.truyencv.app.data.remote.mottruyen.MotTruyenStoryDto
import com.truyencv.app.data.remote.truyencv.TruyenCvChapterDetailDto
import com.truyencv.app.data.remote.truyencv.TruyenCvChapterItem
import com.truyencv.app.data.remote.truyencv.WpAuthorItem
import com.truyencv.app.data.remote.truyencv.WpEmbedded
import com.truyencv.app.data.remote.truyencv.WpFeaturedMedia
import com.truyencv.app.data.remote.truyencv.WpMangaDto
import com.truyencv.app.data.remote.truyencv.WpRendered
import com.truyencv.app.data.remote.truyencv.WpTermItem
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapperTest {

    @Test
    fun testTruyenCvAuthorResolutionFromEmbeddedTerms() {
        val dto = WpMangaDto(
            id = 1234L,
            title = WpRendered("Vạn Cổ Thần Đế"),
            author = 99L,
            embedded = WpEmbedded(
                terms = listOf(
                    listOf(
                        WpTermItem(id = 1, name = "Phi Thiên Ngư", slug = "phi-thien-ngu", taxonomy = "author_tax")
                    ),
                    listOf(
                        WpTermItem(id = 2, name = "Huyền Huyễn", slug = "huyen-huyen", taxonomy = "genre")
                    )
                )
            )
        )

        val novel = TruyenCvMapper.mapWpMangaToNovel(dto)
        assertEquals("tcv-1234", novel.id)
        assertEquals("Phi Thiên Ngư", novel.author)
        assertEquals("Vạn Cổ Thần Đế", novel.title)
        assertEquals(listOf("Huyền Huyễn"), novel.genres)
        assertEquals(Source.TRUYENCV, novel.source)
    }

    @Test
    fun testTruyenCvAuthorResolutionFromClassList() {
        val dto = WpMangaDto(
            id = 5678L,
            title = WpRendered("Đại Quản Gia"),
            author = 1123L,
            classList = listOf("post-5678", "manga", "author_tax-da-kieu", "genre-tien-hiep")
        )

        val novel = TruyenCvMapper.mapWpMangaToNovel(dto)
        assertEquals("Da Kieu", novel.author)
        assertEquals(listOf("Tien Hiep"), novel.genres)
    }

    @Test
    fun testTruyenCvAuthorFallback() {
        val dto = WpMangaDto(
            id = 9999L,
            title = WpRendered("Truyện Vô Danh"),
            authorName = "Tác Giả Ẩn Danh"
        )
        val novel = TruyenCvMapper.mapWpMangaToNovel(dto)
        assertEquals("Tác Giả Ẩn Danh", novel.author)

        val dtoUnknown = WpMangaDto(id = 8888L, title = WpRendered("Truyện Không Rõ"))
        val novelUnknown = TruyenCvMapper.mapWpMangaToNovel(dtoUnknown)
        assertEquals("Chưa rõ", novelUnknown.author)
    }

    @Test
    fun testTruyenCvStatusResolution() {
        val completedDto = WpMangaDto(
            id = 111L,
            status = "completed"
        )
        assertEquals(NovelStatus.COMPLETED, TruyenCvMapper.resolveStatus(completedDto))

        val classListCompletedDto = WpMangaDto(
            id = 222L,
            classList = listOf("status-completed")
        )
        assertEquals(NovelStatus.COMPLETED, TruyenCvMapper.resolveStatus(classListCompletedDto))

        val ongoingDto = WpMangaDto(
            id = 333L,
            status = "publish"
        )
        assertEquals(NovelStatus.ONGOING, TruyenCvMapper.resolveStatus(ongoingDto))
    }

    @Test
    fun testTruyenCvThumbnailSanitization() {
        val dto = WpMangaDto(
            id = 444L,
            embedded = WpEmbedded(
                featuredMedia = listOf(
                    WpFeaturedMedia(sourceUrl = "https://static.cdnno.com/poster//broken.jpg")
                )
            )
        )
        val novel = TruyenCvMapper.mapWpMangaToNovel(dto)
        assertEquals("https://truyencv.io/default-cover.png", novel.thumbUrl)
    }

    @Test
    fun testTruyenCvChapterItemMapping() {
        val item = TruyenCvChapterItem(
            id = 1001L,
            name = "Trọng Sinh Về Thời Niên Thiếu"
        )
        val chapter = TruyenCvMapper.mapChapterItemToChapter(item, "tcv-100", 1, prevId = null, nextId = "tcv-1002")
        assertEquals("tcv-1001", chapter.id)
        assertEquals(1, chapter.chapterOrder)
        assertEquals("Chương 1: Trọng Sinh Về Thời Niên Thiếu", chapter.title)
        assertEquals("tcv-1002", chapter.nextId)
        assertFalse(chapter.isDownloaded)
    }

    @Test
    fun testTruyenCvChapterDetailMapping() {
        val detail = TruyenCvChapterDetailDto(
            id = 1001L,
            title = "Chương 1: Bắt đầu",
            content = "<p>Chương 1: Bắt đầu</p><br/>Hôm nay là ngày đầu tiên..."
        )
        val chapter = TruyenCvMapper.mapChapterDetailToChapter(detail, "tcv-100", 1)
        assertEquals("tcv-1001", chapter.id)
        assertEquals("Chương 1: Bắt đầu", chapter.title)
        assertEquals("Hôm nay là ngày đầu tiên...", chapter.content)
        assertTrue(chapter.isDownloaded)
    }

    @Test
    fun testMotTruyenStoryMapping() {
        val dto = MotTruyenStoryDto(
            ID = 789L,
            NAME = "Toàn Chức Pháp Sư",
            AUTHOR = "Loạn",
            THUMB = "http://api.mottruyen.com/poster/789.jpg",
            DESC = "<p>Mạc Phàm thức tỉnh hệ thống ma pháp...</p>",
            CAT = "Huyền Huyễn, Đô Thị, Ma Pháp",
            STATUS = 2,
            TOTALCHAPTER = 3200,
            VIEW = 1500000L
        )

        val novel = MotTruyenMapper.mapStoryDtoToNovel(dto)
        assertEquals("mt-789", novel.id)
        assertEquals(789L, novel.rawId)
        assertEquals("Toàn Chức Pháp Sư", novel.title)
        assertEquals("Loạn", novel.author)
        assertEquals(NovelStatus.COMPLETED, novel.status)
        assertEquals(3, novel.genres.size)
        assertEquals("Huyền Huyễn", novel.genres[0])
        assertEquals(Source.MOTTRUYEN, novel.source)
        assertEquals("Mạc Phàm thức tỉnh hệ thống ma pháp...", novel.description)
        assertEquals(3200, novel.totalChapters)
        assertEquals(1500000L, novel.views)
    }

    @Test
    fun testMotTruyenChapterMapping() {
        val item = MotTruyenChapterItem(
            ID = 555L,
            NAME = "Thức Tỉnh Lôi Hệ",
            ORDER = 1
        )
        val chapter = MotTruyenMapper.mapChapterItemToChapter(item, "mt-789")
        assertEquals("mt-555", chapter.id)
        assertEquals(1, chapter.chapterOrder)
        assertEquals("Chương 1: Thức Tỉnh Lôi Hệ", chapter.title)
        assertEquals(Source.MOTTRUYEN, chapter.source)

        val contentData = MotTruyenChapterContentData(
            ID = 555L,
            NAME = "Thức Tỉnh Lôi Hệ",
            ORDER = 1,
            CONTENT = "<p>Chương 1: Thức Tỉnh Lôi Hệ</p><br/>Sấm sét chớp nháy khắp bầu trời..."
        )
        val contentChapter = MotTruyenMapper.mapChapterContentToChapter(contentData, "mt-789")
        assertEquals("Sấm sét chớp nháy khắp bầu trời...", contentChapter.content)
        assertTrue(contentChapter.isDownloaded)
    }

    @Test
    fun testNovelEntityRoundTrip() {
        val original = Novel(
            id = "tcv-555",
            rawId = 555L,
            title = "Test Novel",
            author = "Test Author",
            thumbUrl = "https://example.com/cover.jpg",
            coverBase64 = "base64data",
            description = "Test Description",
            genres = listOf("Tiên Hiệp", "Huyền Huyễn"),
            totalChapters = 100,
            views = 9999L,
            status = NovelStatus.COMPLETED,
            source = Source.TRUYENCV
        )

        val entity = TruyenCvMapper.novelToEntity(original)
        val restored = TruyenCvMapper.entityToNovel(entity)

        assertEquals(original.id, restored.id)
        assertEquals(original.rawId, restored.rawId)
        assertEquals(original.title, restored.title)
        assertEquals(original.author, restored.author)
        assertEquals(original.coverBase64, restored.coverBase64)
        assertEquals(original.genres, restored.genres)
        assertEquals(original.status, restored.status)
        assertEquals(original.source, restored.source)
    }

    @Test
    fun testChapterEntityRoundTrip() {
        val original = Chapter(
            id = "mt-123",
            rawId = 123L,
            novelId = "mt-456",
            chapterOrder = 5,
            title = "Chương 5: Bước Ngoặt",
            rawTitle = "Bước Ngoặt",
            content = "Nội dung chương 5",
            prevId = "mt-122",
            nextId = "mt-124",
            source = Source.MOTTRUYEN,
            isDownloaded = true
        )

        val entity = TruyenCvMapper.chapterToEntity(original)
        val restored = TruyenCvMapper.entityToChapter(entity)

        assertEquals(original.id, restored.id)
        assertEquals(original.novelId, restored.novelId)
        assertEquals(original.chapterOrder, restored.chapterOrder)
        assertEquals(original.title, restored.title)
        assertEquals(original.content, restored.content)
        assertEquals(original.prevId, restored.prevId)
        assertEquals(original.nextId, restored.nextId)
        assertEquals(original.source, restored.source)
        assertEquals(original.isDownloaded, restored.isDownloaded)
    }
}
