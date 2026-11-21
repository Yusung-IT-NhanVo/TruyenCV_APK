package com.truyencv.app.ui.detail

import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.BookmarkRepository
import com.truyencv.app.domain.repository.ChapterRepository
import com.truyencv.app.domain.repository.HistoryRepository
import com.truyencv.app.domain.repository.NovelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NovelDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeNovelRepository: FakeNovelRepository
    private lateinit var fakeChapterRepository: FakeChapterRepository
    private lateinit var fakeBookmarkRepository: FakeBookmarkRepository
    private lateinit var fakeHistoryRepository: FakeHistoryRepository
    private lateinit var viewModel: NovelDetailViewModel

    private val targetNovelId = "tcv-100"

    private val sampleNovel = Novel(
        id = targetNovelId,
        rawId = 100L,
        title = "Đại Quản Gia Là Ma Hoàng",
        author = "Dạ Kiêu",
        thumbUrl = "https://example.com/cover.jpg",
        description = "<p>Mô tả truyện Trác Nhất Phàm...</p>",
        genres = listOf("Tiên Hiệp", "Trọng Sinh"),
        totalChapters = 1314,
        views = 2500000L,
        status = NovelStatus.COMPLETED,
        source = Source.TRUYENCV
    )

    private val sampleChapters = listOf(
        Chapter(
            id = "tcv-c1",
            rawId = 1L,
            novelId = targetNovelId,
            chapterOrder = 1,
            title = "Chương 1: Ma Hoàng Trọng Sinh",
            rawTitle = "1: Ma Hoàng Trọng Sinh",
            source = Source.TRUYENCV
        ),
        Chapter(
            id = "tcv-c2",
            rawId = 2L,
            novelId = targetNovelId,
            chapterOrder = 2,
            title = "Chương 2: Lạc Gia Đại Thiếu Gia",
            rawTitle = "2: Lạc Gia Đại Thiếu Gia",
            source = Source.TRUYENCV
        ),
        Chapter(
            id = "tcv-c3",
            rawId = 3L,
            novelId = targetNovelId,
            chapterOrder = 3,
            title = "Chương 3: Cửu U Bí Lục",
            rawTitle = "3: Cửu U Bí Lục",
            source = Source.TRUYENCV
        )
    )

    private val sampleProgress = ReadingProgress(
        novelId = targetNovelId,
        chapterId = "tcv-c2",
        chapterOrder = 2,
        chapterTitle = "Chương 2: Lạc Gia Đại Thiếu Gia"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeNovelRepository = FakeNovelRepository(novelToReturn = sampleNovel)
        fakeChapterRepository = FakeChapterRepository(chaptersToReturn = sampleChapters)
        fakeBookmarkRepository = FakeBookmarkRepository(isBookmarkedState = false)
        fakeHistoryRepository = FakeHistoryRepository(progressToReturn = sampleProgress)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadDetail_loadsNovelChaptersBookmarkProgress() = runTest(testDispatcher) {
        viewModel = NovelDetailViewModel(
            novelId = targetNovelId,
            novelRepository = fakeNovelRepository,
            chapterRepository = fakeChapterRepository,
            bookmarkRepository = fakeBookmarkRepository,
            historyRepository = fakeHistoryRepository
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.novel)
        assertEquals("Đại Quản Gia Là Ma Hoàng", state.novel?.title)
        assertEquals(3, state.chapters.size)
        assertFalse(state.isBookmarked)
        assertNotNull(state.lastProgress)
        assertEquals("tcv-c2", state.lastProgress?.chapterId)
        assertEquals(2, state.lastProgress?.chapterOrder)
        assertFalse(state.isLoadingNovel)
        assertFalse(state.isLoadingChapters)
        assertNull(state.error)
    }

    @Test
    fun testToggleBookmark() = runTest(testDispatcher) {
        viewModel = NovelDetailViewModel(
            novelId = targetNovelId,
            novelRepository = fakeNovelRepository,
            chapterRepository = fakeChapterRepository,
            bookmarkRepository = fakeBookmarkRepository,
            historyRepository = fakeHistoryRepository
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBookmarked)

        // Bật theo dõi
        viewModel.toggleBookmark()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBookmarked)

        // Hủy theo dõi
        viewModel.toggleBookmark()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isBookmarked)
    }

    @Test
    fun testToggleChapterSort() = runTest(testDispatcher) {
        viewModel = NovelDetailViewModel(
            novelId = targetNovelId,
            novelRepository = fakeNovelRepository,
            chapterRepository = fakeChapterRepository,
            bookmarkRepository = fakeBookmarkRepository,
            historyRepository = fakeHistoryRepository
        )
        advanceUntilIdle()

        // Mặc định là sắp xếp tăng dần (ASC)
        assertTrue(viewModel.uiState.value.isSortAsc)
        assertEquals(1, viewModel.uiState.value.displayChapters.first().chapterOrder)
        assertEquals(3, viewModel.uiState.value.displayChapters.last().chapterOrder)

        // Đảo chiều sắp xếp giảm dần (DESC)
        viewModel.toggleChapterSort()
        assertFalse(viewModel.uiState.value.isSortAsc)
        assertEquals(3, viewModel.uiState.value.displayChapters.first().chapterOrder)
        assertEquals(1, viewModel.uiState.value.displayChapters.last().chapterOrder)

        // Đảo chiều quay lại tăng dần (ASC)
        viewModel.toggleChapterSort()
        assertTrue(viewModel.uiState.value.isSortAsc)
        assertEquals(1, viewModel.uiState.value.displayChapters.first().chapterOrder)
    }

    @Test
    fun testDownloadAllChapters() = runTest(testDispatcher) {
        viewModel = NovelDetailViewModel(
            novelId = targetNovelId,
            novelRepository = fakeNovelRepository,
            chapterRepository = fakeChapterRepository,
            bookmarkRepository = fakeBookmarkRepository,
            historyRepository = fakeHistoryRepository
        )
        advanceUntilIdle()

        viewModel.downloadAllChapters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isDownloading)
        assertNotNull(state.downloadMessage)
        assertTrue(state.downloadMessage!!.contains("Đã tải xong toàn bộ 3 chương"))
    }

    @Test
    fun testLoadDetail_errorHandling() = runTest(testDispatcher) {
        fakeNovelRepository.shouldThrow = true
        viewModel = NovelDetailViewModel(
            novelId = targetNovelId,
            novelRepository = fakeNovelRepository,
            chapterRepository = fakeChapterRepository,
            bookmarkRepository = fakeBookmarkRepository,
            historyRepository = fakeHistoryRepository
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.novel)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Lỗi tải chi tiết truyện giả lập"))
        assertFalse(state.isLoadingNovel)
    }

    // --- Fakes ---

    private class FakeNovelRepository(
        var novelToReturn: Novel? = null,
        var shouldThrow: Boolean = false
    ) : NovelRepository {
        override suspend fun getNovelDetail(id: String): Novel {
            if (shouldThrow) throw RuntimeException("Lỗi tải chi tiết truyện giả lập")
            return novelToReturn ?: error("Novel not found")
        }

        override fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun getRecommended(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun searchNovels(query: SearchQuery): Flow<List<Novel>> = flowOf(emptyList())
        override fun getGenres(source: Source?): Flow<List<Genre>> = flowOf(emptyList())
    }

    private class FakeChapterRepository(
        var chaptersToReturn: List<Chapter> = emptyList()
    ) : ChapterRepository {
        override suspend fun getAllChapters(novelId: String): List<Chapter> = chaptersToReturn
        override fun getChapters(novelId: String, page: Int, perPage: Int): Flow<List<Chapter>> = flowOf(chaptersToReturn)
        override suspend fun getChapterContent(chapterId: String): Chapter = error("Not implemented")
        override suspend fun getChapterBuffer(novelId: String, currentChapterOrder: Int, bufferSize: Int): List<Chapter> = emptyList()
    }

    private class FakeBookmarkRepository(
        private var isBookmarkedState: Boolean = false
    ) : BookmarkRepository {
        override suspend fun isBookmarked(novelId: String): Boolean = isBookmarkedState
        override suspend fun toggleBookmark(novel: Novel): Boolean {
            isBookmarkedState = !isBookmarkedState
            return isBookmarkedState
        }
        override fun getBookmarks(): Flow<List<Novel>> = flowOf(emptyList())
        override suspend fun removeBookmark(novelId: String) {
            isBookmarkedState = false
        }
    }

    private class FakeHistoryRepository(
        var progressToReturn: ReadingProgress? = null
    ) : HistoryRepository {
        override suspend fun getProgress(novelId: String): ReadingProgress? = progressToReturn
        override fun getHistory(): Flow<List<ReadingProgress>> = flowOf(emptyList())
        override suspend fun saveProgress(progress: ReadingProgress) {
            progressToReturn = progress
        }
        override suspend fun deleteProgress(novelId: String) {
            progressToReturn = null
        }
        override suspend fun clearHistory() {
            progressToReturn = null
        }
    }
}
