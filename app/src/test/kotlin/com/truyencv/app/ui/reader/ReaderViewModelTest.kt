package com.truyencv.app.ui.reader

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.truyencv.app.data.local.datastore.ReaderPreferences
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.ChapterRepository
import com.truyencv.app.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

/**
 * In-memory [DataStore] implementation for unit tests.
 */
class FakePreferenceDataStore(
    initialPreferences: Preferences = emptyPreferences()
) : DataStore<Preferences> {
    private val flow = MutableStateFlow(initialPreferences)
    override val data: Flow<Preferences> = flow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(flow.value)
        flow.value = updated
        return updated
    }
}

/**
 * Fake implementation of [ChapterRepository] for reader tests.
 */
class FakeReaderChapterRepository(
    val chapters: List<Chapter>
) : ChapterRepository {
    var getChapterBufferCallCount = 0
    var lastBufferOrderRequested = -1
    var lastBufferSizeRequested = -1

    override fun getChapters(novelId: String, page: Int, perPage: Int): Flow<List<Chapter>> = flowOf(chapters)

    override suspend fun getAllChapters(novelId: String): List<Chapter> = chapters

    override suspend fun getChapterContent(chapterId: String): Chapter {
        return chapters.find { it.id == chapterId }
            ?: throw IllegalStateException("Chapter $chapterId not found")
    }

    override suspend fun getChapterBuffer(novelId: String, currentChapterOrder: Int, bufferSize: Int): List<Chapter> {
        getChapterBufferCallCount++
        lastBufferOrderRequested = currentChapterOrder
        lastBufferSizeRequested = bufferSize
        return chapters.filter {
            it.chapterOrder in (currentChapterOrder - bufferSize)..(currentChapterOrder + bufferSize)
        }
    }
}

/**
 * Fake implementation of [HistoryRepository] for reader tests.
 */
class FakeReaderHistoryRepository : HistoryRepository {
    val savedProgressList = mutableListOf<ReadingProgress>()
    private val progressMap = mutableMapOf<String, ReadingProgress>()

    override fun getHistory(): Flow<List<ReadingProgress>> = flowOf(progressMap.values.toList())

    override suspend fun saveProgress(progress: ReadingProgress) {
        savedProgressList.add(progress)
        progressMap[progress.novelId] = progress
    }

    override suspend fun getProgress(novelId: String): ReadingProgress? = progressMap[novelId]

    override suspend fun deleteProgress(novelId: String) {
        progressMap.remove(novelId)
    }

    override suspend fun clearHistory() {
        progressMap.clear()
        savedProgressList.clear()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeChapterRepository: FakeReaderChapterRepository
    private lateinit var fakeHistoryRepository: FakeReaderHistoryRepository
    private lateinit var fakeDataStore: FakePreferenceDataStore
    private lateinit var readerPreferences: ReaderPreferences
    private lateinit var viewModel: ReaderViewModel

    private val targetNovelId = "tcv-novel-1"

    private val sampleChapters = listOf(
        Chapter(
            id = "tcv-c1",
            rawId = 1L,
            novelId = targetNovelId,
            chapterOrder = 1,
            title = "Chương 1: Khởi đầu",
            rawTitle = "1: Khởi đầu",
            content = "<p>Nội dung chương 1...</p>",
            prevId = null,
            nextId = "tcv-c2",
            source = Source.TRUYENCV
        ),
        Chapter(
            id = "tcv-c2",
            rawId = 2L,
            novelId = targetNovelId,
            chapterOrder = 2,
            title = "Chương 2: Tiến bước",
            rawTitle = "2: Tiến bước",
            content = "<p>Nội dung chương 2...</p>",
            prevId = "tcv-c1",
            nextId = "tcv-c3",
            source = Source.TRUYENCV
        ),
        Chapter(
            id = "tcv-c3",
            rawId = 3L,
            novelId = targetNovelId,
            chapterOrder = 3,
            title = "Chương 3: Đỉnh cao",
            rawTitle = "3: Đỉnh cao",
            content = "<p>Nội dung chương 3...</p>",
            prevId = "tcv-c2",
            nextId = null,
            source = Source.TRUYENCV
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChapterRepository = FakeReaderChapterRepository(sampleChapters)
        fakeHistoryRepository = FakeReaderHistoryRepository()
        fakeDataStore = FakePreferenceDataStore()
        readerPreferences = ReaderPreferences(fakeDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoad_loadsChapterAndPrefetchesBuffer() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c1",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentChapter)
        assertEquals("tcv-c1", state.currentChapter?.id)
        assertEquals("Chương 1: Khởi đầu", state.currentChapter?.title)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(0, state.currentChapterIndex)
        assertEquals(3, state.totalChaptersCount)

        // Verify buffer prefetch was triggered with bufferSize = 6
        assertTrue(fakeChapterRepository.getChapterBufferCallCount >= 1)
        assertEquals(1, fakeChapterRepository.lastBufferOrderRequested)
        assertEquals(6, fakeChapterRepository.lastBufferSizeRequested)
    }

    @Test
    fun testSaveProgressOnChapterLoaded() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c1",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()

        assertTrue(fakeHistoryRepository.savedProgressList.isNotEmpty())
        val lastProgress = fakeHistoryRepository.savedProgressList.last()
        assertEquals(targetNovelId, lastProgress.novelId)
        assertEquals("tcv-c1", lastProgress.chapterId)
        assertEquals(1, lastProgress.chapterOrder)
        assertEquals("Chương 1: Khởi đầu", lastProgress.chapterTitle)
    }

    @Test
    fun testNavigateToNextChapter() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c1",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()

        viewModel.navigateToNextChapter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("tcv-c2", state.currentChapter?.id)
        assertEquals(1, state.currentChapterIndex)

        val lastProgress = fakeHistoryRepository.savedProgressList.last()
        assertEquals("tcv-c2", lastProgress.chapterId)
        assertEquals(2, lastProgress.chapterOrder)
    }

    @Test
    fun testNavigateToPrevChapter() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c2",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()
        assertEquals("tcv-c2", viewModel.uiState.value.currentChapter?.id)

        viewModel.navigateToPrevChapter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("tcv-c1", state.currentChapter?.id)
        assertEquals(0, state.currentChapterIndex)

        val lastProgress = fakeHistoryRepository.savedProgressList.last()
        assertEquals("tcv-c1", lastProgress.chapterId)
        assertEquals(1, lastProgress.chapterOrder)
    }

    @Test
    fun testUpdateReaderSettings_updatesPreferences() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c1",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()

        viewModel.setFontSize(24f)
        viewModel.setFontFamily("Serif")
        viewModel.setLineHeight(1.8f)
        viewModel.setTheme("#2B2B2B", "#E0E0E0")
        viewModel.setReaderMode("PAGE")
        viewModel.setKeepScreenOn(false)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(24f, state.fontSize, 0.01f)
        assertEquals("Serif", state.fontFamily)
        assertEquals(1.8f, state.lineHeight, 0.01f)
        assertEquals("#2B2B2B", state.backgroundColorHex)
        assertEquals("#E0E0E0", state.textColorHex)
        assertEquals("PAGE", state.readerMode)
        assertFalse(state.keepScreenOn)
    }

    @Test
    fun testToggleMenu() = runTest(testDispatcher) {
        viewModel = ReaderViewModel(
            novelId = targetNovelId,
            initialChapterId = "tcv-c1",
            chapterRepository = fakeChapterRepository,
            historyRepository = fakeHistoryRepository,
            readerPreferences = readerPreferences
        )

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isMenuVisible)

        viewModel.toggleMenu()
        assertTrue(viewModel.uiState.value.isMenuVisible)

        viewModel.toggleMenu()
        assertFalse(viewModel.uiState.value.isMenuVisible)

        viewModel.setMenuVisible(true)
        assertTrue(viewModel.uiState.value.isMenuVisible)
    }
}
