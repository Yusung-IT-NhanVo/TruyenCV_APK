package com.truyencv.app.ui.search

import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.NovelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeSearchNovelRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeSearchNovelRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testKeywordChangeWithDebounceTriggersSearch() = runTest {
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Phàm Nhân")
        // Trước debounce (400ms), chưa tìm kiếm
        testDispatcher.scheduler.advanceTimeBy(200L)
        assertFalse(viewModel.uiState.value.hasSearched)

        // Sau debounce (400ms+)
        testDispatcher.scheduler.advanceTimeBy(300L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Phàm Nhân", state.query.keyword)
        assertTrue(state.hasSearched)
        assertEquals(2, state.results.size)
        assertFalse(state.isSearching)
        assertNull(state.error)
    }

    @Test
    fun testExplicitSearchTriggersImmediately() = runTest {
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Đấu La")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasSearched)
        assertEquals(2, state.results.size)
    }

    @Test
    fun testFilterUpdates() = runTest {
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // 1. Thể loại
        viewModel.onGenreChange("tien-hiep")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("tien-hiep", viewModel.uiState.value.query.genre)

        // 2. Trạng thái
        viewModel.onStatusChange(NovelStatus.COMPLETED)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(NovelStatus.COMPLETED, viewModel.uiState.value.query.status)

        // 3. Số chương
        viewModel.onChapterRangeChange(ChapterFilterRange.FROM_300_TO_600)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(300, viewModel.uiState.value.query.minChapters)
        assertEquals(600, viewModel.uiState.value.query.maxChapters)
        assertEquals(ChapterFilterRange.FROM_300_TO_600, viewModel.uiState.value.selectedChapterRange)

        // 4. Sắp xếp
        viewModel.onSortChange(SearchSortOption.VIEWS)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("views", viewModel.uiState.value.query.sort)
        assertEquals(SearchSortOption.VIEWS, viewModel.uiState.value.sortOption)

        // 5. Nguồn
        viewModel.onSourceChange(Source.MOTTRUYEN)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Source.MOTTRUYEN, viewModel.uiState.value.query.source)
    }

    @Test
    fun testResetFilterRestoresDefaults() = runTest {
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Từ khóa")
        viewModel.onGenreChange("kiem-hiep")
        viewModel.onStatusChange(NovelStatus.ONGOING)
        viewModel.onSortChange(SearchSortOption.RATING)
        viewModel.onSourceChange(Source.TRUYENCV)
        testDispatcher.scheduler.advanceUntilIdle()

        // Reset
        viewModel.resetFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query.keyword)
        assertNull(state.query.genre)
        assertNull(state.query.status)
        assertNull(state.query.source)
        assertEquals("modified", state.query.sort)
        assertEquals(ChapterFilterRange.ALL, state.selectedChapterRange)
        assertEquals(SearchSortOption.MODIFIED, state.sortOption)
        assertTrue(state.results.isEmpty())
        assertFalse(state.hasSearched)
    }

    @Test
    fun testInitialGenreTriggersSearchOnCreation() = runTest {
        val viewModel = SearchViewModel(fakeRepository, initialGenre = "huyen-huyen")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("huyen-huyen", state.query.genre)
        assertTrue(state.hasSearched)
        assertEquals(2, state.results.size)
    }

    @Test
    fun testLoadMoreAppendsResults() = runTest {
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Test")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.results.size)
        assertEquals(1, viewModel.uiState.value.page)

        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.page)
        assertEquals(4, state.results.size)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun testSearchErrorSetsErrorMessage() = runTest {
        fakeRepository.shouldThrow = true
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Lỗi")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNotNull(state.error)
        assertTrue(state.results.isEmpty())
    }

    @Test
    fun testEmptyResultsHandling() = runTest {
        fakeRepository.returnEmpty = true
        val viewModel = SearchViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onKeywordChange("Không có kết quả")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasSearched)
        assertTrue(state.results.isEmpty())
        assertFalse(state.canLoadMore)
        assertNull(state.error)
    }

    private class FakeSearchNovelRepository : NovelRepository {
        var shouldThrow: Boolean = false
        var returnEmpty: Boolean = false

        private val sampleNovels = listOf(
            Novel(
                id = "tcv-101",
                rawId = 101L,
                title = "Phàm Nhân Tu Tiên",
                author = "Vong Ngữ",
                thumbUrl = "https://example.com/101.jpg",
                description = "Mô tả",
                genres = listOf("Tiên Hiệp"),
                totalChapters = 1500,
                views = 200000,
                status = NovelStatus.COMPLETED,
                source = Source.TRUYENCV
            ),
            Novel(
                id = "mt-202",
                rawId = 202L,
                title = "Đấu La Đại Lục",
                author = "Đường Gia Tam Thiếu",
                thumbUrl = "https://example.com/202.jpg",
                description = "Mô tả",
                genres = listOf("Huyền Huyễn"),
                totalChapters = 1200,
                views = 150000,
                status = NovelStatus.ONGOING,
                source = Source.MOTTRUYEN
            )
        )

        override fun searchNovels(query: SearchQuery): Flow<List<Novel>> = flow {
            if (shouldThrow) throw RuntimeException("Lỗi máy chủ khi tìm kiếm")
            if (returnEmpty) {
                emit(emptyList())
            } else {
                emit(sampleNovels)
            }
        }

        override fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override fun getRecommended(source: Source?, page: Int): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override suspend fun getNovelDetail(id: String): Novel {
            return sampleNovels.first()
        }

        override fun getGenres(source: Source?): Flow<List<Genre>> = flow {
            emit(
                listOf(
                    Genre(id = "1", name = "Tiên Hiệp", slug = "tien-hiep", count = 100, source = Source.TRUYENCV),
                    Genre(id = "2", name = "Huyền Huyễn", slug = "huyen-huyen", count = 120, source = Source.TRUYENCV)
                )
            )
        }
    }
}
