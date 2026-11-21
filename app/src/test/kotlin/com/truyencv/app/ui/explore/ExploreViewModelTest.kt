package com.truyencv.app.ui.explore

import com.truyencv.app.domain.model.Chapter
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
class ExploreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTestNovelRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTestNovelRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadSuccess() = runTest {
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
        assertEquals(2, state.bannerHotNovels.size)
        assertEquals(1, state.recommendedNovels.size)
        assertEquals(2, state.genresList.size)
        assertEquals(2, state.newUpdateNovels.size)
        assertEquals(1, state.newUpdatePage)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun testSelectSourceUpdatesFilterAndReloads() = runTest {
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectSource(Source.TRUYENCV)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(Source.TRUYENCV, state.selectedSource)
        assertEquals(1, state.newUpdatePage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testRefreshResetsPageAndReloads() = runTest {
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMoreNewUpdates()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.newUpdatePage)

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.newUpdatePage)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun testLoadMoreNewUpdatesAppendsResults() = runTest {
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.newUpdateNovels.size)

        viewModel.loadMoreNewUpdates()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.newUpdatePage)
        assertEquals(4, state.newUpdateNovels.size)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun testLoadMoreWhenCanLoadMoreIsFalse() = runTest {
        fakeRepository.emptyOnNextPage = true
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMoreNewUpdates()
        testDispatcher.scheduler.advanceUntilIdle()

        // Khi trang 2 trả về rỗng, canLoadMore sẽ thành false
        val state = viewModel.uiState.value
        assertFalse(state.canLoadMore)
        val initialCount = state.newUpdateNovels.size

        // Lần gọi tiếp theo không tăng page vì canLoadMore = false
        viewModel.loadMoreNewUpdates()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(initialCount, viewModel.uiState.value.newUpdateNovels.size)
    }

    @Test
    fun testErrorStateWhenAllDataFails() = runTest {
        fakeRepository.shouldThrowError = true
        val viewModel = ExploreViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.bannerHotNovels.isEmpty())
        assertTrue(state.newUpdateNovels.isEmpty())
    }

    private class FakeTestNovelRepository : NovelRepository {
        var shouldThrowError: Boolean = false
        var emptyOnNextPage: Boolean = false

        private val sampleNovels = listOf(
            Novel(
                id = "tcv-1",
                rawId = 1L,
                title = "Phàm Nhân Tu Tiên",
                author = "Vong Ngữ",
                thumbUrl = "https://example.com/1.jpg",
                description = "Mô tả",
                genres = listOf("Tiên Hiệp"),
                totalChapters = 1500,
                views = 100000,
                status = NovelStatus.COMPLETED,
                source = Source.TRUYENCV
            ),
            Novel(
                id = "mt-2",
                rawId = 2L,
                title = "Đấu La Đại Lục",
                author = "Đường Gia Tam Thiếu",
                thumbUrl = "https://example.com/2.jpg",
                description = "Mô tả",
                genres = listOf("Huyền Huyễn"),
                totalChapters = 1200,
                views = 80000,
                status = NovelStatus.ONGOING,
                source = Source.MOTTRUYEN
            )
        )

        override fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>> = flow {
            if (shouldThrowError) throw RuntimeException("Lỗi kết nối máy chủ")
            emit(sampleNovels)
        }

        override fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>> = flow {
            if (shouldThrowError) throw RuntimeException("Lỗi kết nối máy chủ")
            if (page > 1 && emptyOnNextPage) {
                emit(emptyList())
            } else {
                emit(sampleNovels)
            }
        }

        override fun getRecommended(source: Source?, page: Int): Flow<List<Novel>> = flow {
            if (shouldThrowError) throw RuntimeException("Lỗi kết nối máy chủ")
            emit(listOf(sampleNovels.first()))
        }

        override fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override fun searchNovels(query: SearchQuery): Flow<List<Novel>> = flow {
            emit(sampleNovels)
        }

        override suspend fun getNovelDetail(id: String): Novel {
            return sampleNovels.first()
        }

        override fun getGenres(source: Source?): Flow<List<Genre>> = flow {
            if (shouldThrowError) throw RuntimeException("Lỗi kết nối máy chủ")
            emit(
                listOf(
                    Genre(id = "1", name = "Tiên Hiệp", slug = "tien-hiep", count = 100, source = Source.TRUYENCV),
                    Genre(id = "2", name = "Huyền Huyễn", slug = "huyen-huyen", count = 120, source = Source.TRUYENCV)
                )
            )
        }
    }
}
