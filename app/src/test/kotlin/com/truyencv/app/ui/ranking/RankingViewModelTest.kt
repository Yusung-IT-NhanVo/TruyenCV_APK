package com.truyencv.app.ui.ranking

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
class RankingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeNovelRepository: FakeNovelRepository
    private lateinit var viewModel: RankingViewModel

    private val sampleNovel1 = Novel(
        id = "tcv-1",
        rawId = 1L,
        title = "Truyện Số 1",
        author = "Tác Giả A",
        thumbUrl = "https://example.com/cover1.jpg",
        description = "Mô tả 1",
        genres = listOf("Tiên Hiệp"),
        totalChapters = 500,
        views = 100000L,
        status = NovelStatus.COMPLETED,
        source = Source.TRUYENCV
    )

    private val sampleNovel2 = Novel(
        id = "mt-2",
        rawId = 2L,
        title = "Truyện Số 2",
        author = "Tác Giả B",
        thumbUrl = "https://example.com/cover2.jpg",
        description = "Mô tả 2",
        genres = listOf("Đô Thị"),
        totalChapters = 300,
        views = 85000L,
        status = NovelStatus.ONGOING,
        source = Source.MOTTRUYEN
    )

    private val sampleNovel3 = Novel(
        id = "tcv-3",
        rawId = 3L,
        title = "Truyện Số 3",
        author = "Tác Giả C",
        thumbUrl = "https://example.com/cover3.jpg",
        description = "Mô tả 3",
        genres = listOf("Huyền Huyễn"),
        totalChapters = 1200,
        views = 60000L,
        status = NovelStatus.ONGOING,
        source = Source.TRUYENCV
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeNovelRepository = FakeNovelRepository()
        fakeNovelRepository.rankingNovelsToReturn = { _, _, page ->
            if (page == 1) listOf(sampleNovel1, sampleNovel2) else listOf(sampleNovel3)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoad_loadsRankingForDayAllSources() = runTest(testDispatcher) {
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(RankingPeriod.DAY, state.selectedPeriod)
        assertNull(state.selectedSource)
        assertEquals(2, state.rankingNovels.size)
        assertEquals("tcv-1", state.rankingNovels[0].id)
        assertEquals("mt-2", state.rankingNovels[1].id)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(1, state.page)
        assertTrue(state.canLoadMore)
        assertEquals(RankingPeriod.DAY, fakeNovelRepository.lastPeriodCalled)
        assertNull(fakeNovelRepository.lastSourceCalled)
        assertEquals(1, fakeNovelRepository.lastPageCalled)
    }

    @Test
    fun testSelectPeriod_reloadsWithNewPeriod() = runTest(testDispatcher) {
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        viewModel.selectPeriod(RankingPeriod.WEEK)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(RankingPeriod.WEEK, state.selectedPeriod)
        assertEquals(RankingPeriod.WEEK, fakeNovelRepository.lastPeriodCalled)
        assertEquals(1, state.page)
    }

    @Test
    fun testSelectSource_reloadsWithNewSource() = runTest(testDispatcher) {
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        viewModel.selectSource(Source.MOTTRUYEN)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(Source.MOTTRUYEN, state.selectedSource)
        assertEquals(Source.MOTTRUYEN, fakeNovelRepository.lastSourceCalled)
        assertEquals(1, state.page)
    }

    @Test
    fun testLoadMore_incrementsPageAndAppendsNovels() = runTest(testDispatcher) {
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.rankingNovels.size)
        assertEquals(1, viewModel.uiState.value.page)

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.rankingNovels.size)
        assertEquals(2, state.page)
        assertEquals("tcv-3", state.rankingNovels[2].id)
        assertEquals(2, fakeNovelRepository.lastPageCalled)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun testRefresh_resetsPageAndReloads() = runTest(testDispatcher) {
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.page)
        assertFalse(state.isRefreshing)
        assertEquals(2, state.rankingNovels.size)
    }

    @Test
    fun testErrorState_whenRepositoryThrows() = runTest(testDispatcher) {
        fakeNovelRepository.shouldThrowError = true
        viewModel = RankingViewModel(fakeNovelRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Network error simulation"))
        assertFalse(state.isLoading)
        assertTrue(state.rankingNovels.isEmpty())
    }

    private class FakeNovelRepository : NovelRepository {
        var rankingNovelsToReturn: (RankingPeriod, Source?, Int) -> List<Novel> = { _, _, _ -> emptyList() }
        var shouldThrowError = false
        var lastPeriodCalled: RankingPeriod? = null
        var lastSourceCalled: Source? = null
        var lastPageCalled: Int? = null

        override fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>> = flow {
            lastPeriodCalled = period
            lastSourceCalled = source
            lastPageCalled = page
            if (shouldThrowError) {
                throw RuntimeException("Network error simulation")
            }
            emit(rankingNovelsToReturn(period, source, page))
        }

        override fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun getRecommended(source: Source?, page: Int): Flow<List<Novel>> = flowOf(emptyList())
        override fun searchNovels(query: SearchQuery): Flow<List<Novel>> = flowOf(emptyList())
        override suspend fun getNovelDetail(id: String): Novel = error("Not implemented")
        override fun getGenres(source: Source?): Flow<List<Genre>> = flowOf(emptyList())
    }
}
