package com.truyencv.app.ui.library

import com.truyencv.app.data.local.db.ChapterDao
import com.truyencv.app.data.local.db.ChapterEntity
import com.truyencv.app.data.local.db.DownloadTaskDao
import com.truyencv.app.data.local.db.DownloadTaskEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.local.db.NovelEntity
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.BookmarkRepository
import com.truyencv.app.domain.repository.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeBookmarkRepo: FakeBookmarkRepository
    private lateinit var fakeHistoryRepo: FakeHistoryRepository
    private lateinit var fakeNovelDao: FakeNovelDao
    private lateinit var fakeChapterDao: FakeChapterDao
    private lateinit var fakeDownloadTaskDao: FakeDownloadTaskDao

    private lateinit var viewModel: LibraryViewModel

    @Before
    fun setup() {
        fakeBookmarkRepo = FakeBookmarkRepository()
        fakeHistoryRepo = FakeHistoryRepository()
        fakeNovelDao = FakeNovelDao()
        fakeChapterDao = FakeChapterDao()
        fakeDownloadTaskDao = FakeDownloadTaskDao()

        viewModel = LibraryViewModel(
            bookmarkRepository = fakeBookmarkRepo,
            historyRepository = fakeHistoryRepo,
            novelDao = fakeNovelDao,
            chapterDao = fakeChapterDao,
            downloadTaskDao = fakeDownloadTaskDao,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun testInitialTabIsFollow() {
        assertEquals(LibraryTab.FOLLOW, viewModel.selectedTab.value)
    }

    @Test
    fun testSelectTab() {
        viewModel.selectTab(LibraryTab.HISTORY)
        assertEquals(LibraryTab.HISTORY, viewModel.selectedTab.value)

        viewModel.selectTab(LibraryTab.OFFLINE)
        assertEquals(LibraryTab.OFFLINE, viewModel.selectedTab.value)

        viewModel.selectTab(LibraryTab.DOWNLOADING)
        assertEquals(LibraryTab.DOWNLOADING, viewModel.selectedTab.value)
    }

    @Test
    fun testLoadBookmarks() = runTest(testDispatcher) {
        val sampleNovel = Novel(
            id = "tcv-101",
            rawId = 101L,
            title = "Vạn Cổ Đệ Nhất Thần",
            author = "Phong Thanh Dương",
            thumbUrl = "https://static.cdnno.com/poster/101.jpg",
            description = "Mô tả truyện",
            genres = listOf("Tiên Hiệp"),
            totalChapters = 500,
            views = 12000L,
            status = NovelStatus.ONGOING,
            source = Source.TRUYENCV
        )
        fakeBookmarkRepo.emitBookmarks(listOf(sampleNovel))

        val result = viewModel.bookmarks.first { it.isNotEmpty() }
        assertEquals(1, result.size)
        assertEquals("tcv-101", result[0].id)
        assertEquals("Vạn Cổ Đệ Nhất Thần", result[0].title)
    }

    @Test
    fun testRemoveBookmark() = runTest(testDispatcher) {
        viewModel.removeBookmark("tcv-101")
        assertTrue(fakeBookmarkRepo.removedIds.contains("tcv-101"))
    }

    @Test
    fun testLoadHistoryCombinesWithNovelDao() = runTest(testDispatcher) {
        // Given reading history & novel entity in Room
        val progress = ReadingProgress(
            novelId = "tcv-200",
            chapterId = "tcv-c50",
            chapterOrder = 50,
            chapterTitle = "Chương 50: Đại chiến",
            scrollPosition = 120,
            lastReadTime = System.currentTimeMillis()
        )
        fakeHistoryRepo.emitHistory(listOf(progress))

        fakeNovelDao.insert(
            NovelEntity(
                id = "tcv-200",
                rawId = 200L,
                title = "Đấu Phá Thương Khung",
                author = "Thiên Tằm Thổ Đậu",
                thumbUrl = "https://thumb.com/200.jpg",
                description = "Mô tả",
                genresJson = "[\"Huyền Huyễn\"]",
                totalChapters = 1600,
                status = "COMPLETED",
                source = "TRUYENCV"
            )
        )

        val historyResult = viewModel.history.first()
        assertEquals(1, historyResult.size)
        assertEquals("tcv-200", historyResult[0].novelId)
        assertEquals("Đấu Phá Thương Khung", historyResult[0].novelTitle)
        assertEquals("Chương 50: Đại chiến", historyResult[0].chapterTitle)
        assertEquals(50, historyResult[0].chapterOrder)
    }

    @Test
    fun testRemoveHistory() = runTest(testDispatcher) {
        viewModel.removeHistory("tcv-200")
        assertTrue(fakeHistoryRepo.deletedNovelIds.contains("tcv-200"))
    }

    @Test
    fun testClearAllHistory() = runTest(testDispatcher) {
        viewModel.clearAllHistory()
        assertTrue(fakeHistoryRepo.isCleared)
    }

    @Test
    fun testLoadOfflineNovels() = runTest(testDispatcher) {
        val novel = NovelEntity(
            id = "tcv-300",
            rawId = 300L,
            title = "Phàm Nhân Tu Tiên",
            author = "Vong Ngữ",
            thumbUrl = "https://thumb.com/300.jpg",
            description = "Truyện tiên hiệp kinh điển",
            genresJson = "[\"Tiên Hiệp\"]",
            totalChapters = 2400,
            status = "COMPLETED",
            source = "TRUYENCV"
        )
        fakeNovelDao.insert(novel)
        fakeChapterDao.setDownloadedChapterIds("tcv-300", listOf("tcv-c1", "tcv-c2", "tcv-c3"))

        val offlineResult = viewModel.offlineNovels.first()
        assertEquals(1, offlineResult.size)
        assertEquals("tcv-300", offlineResult[0].novel.id)
        assertEquals(3, offlineResult[0].downloadedChaptersCount)
    }

    @Test
    fun testRemoveOfflineNovel() = runTest(testDispatcher) {
        viewModel.removeOfflineNovel("tcv-300")
        assertTrue(fakeChapterDao.clearedNovelIds.contains("tcv-300"))
        assertTrue(fakeDownloadTaskDao.deletedNovelIds.contains("tcv-300"))
    }

    @Test
    fun testLoadDownloadTasksFiltersCompleted() = runTest(testDispatcher) {
        val downloadingTask = DownloadTaskEntity(
            novelId = "tcv-401",
            novelTitle = "Toàn Chức Cao Thủ",
            thumbUrl = "https://thumb.com/401.jpg",
            totalChapters = 100,
            downloadedChapters = 25,
            status = "DOWNLOADING"
        )
        val completedTask = DownloadTaskEntity(
            novelId = "tcv-402",
            novelTitle = "Vũ Động Càn Khôn",
            thumbUrl = "https://thumb.com/402.jpg",
            totalChapters = 200,
            downloadedChapters = 200,
            status = "COMPLETED"
        )

        fakeDownloadTaskDao.emitTasks(listOf(downloadingTask, completedTask))

        val result = viewModel.downloadTasks.first()
        assertEquals(1, result.size)
        assertEquals("tcv-401", result[0].novelId)
        assertEquals("DOWNLOADING", result[0].status)
    }

    @Test
    fun testPauseDownload() = runTest(testDispatcher) {
        val task = DownloadTaskEntity(
            novelId = "tcv-500",
            novelTitle = "Tru Tiên",
            thumbUrl = "https://thumb.com/500.jpg",
            totalChapters = 80,
            downloadedChapters = 10,
            status = "DOWNLOADING"
        )
        fakeDownloadTaskDao.insertOrReplace(task)

        viewModel.pauseDownload("tcv-500")

        val updatedTask = fakeDownloadTaskDao.getTask("tcv-500")
        assertNotNull(updatedTask)
        assertEquals("PAUSED", updatedTask?.status)
    }

    @Test
    fun testResumeDownload() = runTest(testDispatcher) {
        val task = DownloadTaskEntity(
            novelId = "tcv-500",
            novelTitle = "Tru Tiên",
            thumbUrl = "https://thumb.com/500.jpg",
            totalChapters = 80,
            downloadedChapters = 10,
            status = "PAUSED"
        )
        fakeDownloadTaskDao.insertOrReplace(task)

        viewModel.resumeDownload("tcv-500")

        val updatedTask = fakeDownloadTaskDao.getTask("tcv-500")
        assertNotNull(updatedTask)
        assertEquals("QUEUED", updatedTask?.status)
    }

    @Test
    fun testCancelDownload() = runTest(testDispatcher) {
        viewModel.cancelDownload("tcv-500")
        assertTrue(fakeDownloadTaskDao.deletedNovelIds.contains("tcv-500"))
        assertTrue(fakeChapterDao.clearedNovelIds.contains("tcv-500"))
    }

    @Test
    fun testRelativeTimeFormatting() {
        val now = System.currentTimeMillis()
        assertEquals("Vừa xong", formatRelativeTime(now - 10_000))
        assertEquals("10 phút trước", formatRelativeTime(now - 10 * 60 * 1000))
        assertEquals("3 giờ trước", formatRelativeTime(now - 3 * 3600 * 1000))
        assertEquals("Hôm qua", formatRelativeTime(now - 25 * 3600 * 1000))
        assertEquals("Gần đây", formatRelativeTime(0L))
    }
}

// ==========================================
// Test Fakes
// ==========================================

class FakeBookmarkRepository : BookmarkRepository {
    private val flow = MutableStateFlow<List<Novel>>(emptyList())
    val removedIds = mutableListOf<String>()

    fun emitBookmarks(list: List<Novel>) {
        flow.value = list
    }

    override fun getBookmarks(): Flow<List<Novel>> = flow

    override suspend fun toggleBookmark(novel: Novel): Boolean = true

    override suspend fun isBookmarked(novelId: String): Boolean = false

    override suspend fun removeBookmark(novelId: String) {
        removedIds.add(novelId)
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val flow = MutableStateFlow<List<ReadingProgress>>(emptyList())
    val deletedNovelIds = mutableListOf<String>()
    var isCleared = false

    fun emitHistory(list: List<ReadingProgress>) {
        flow.value = list
    }

    override fun getHistory(): Flow<List<ReadingProgress>> = flow

    override suspend fun saveProgress(progress: ReadingProgress) {}

    override suspend fun getProgress(novelId: String): ReadingProgress? = null

    override suspend fun deleteProgress(novelId: String) {
        deletedNovelIds.add(novelId)
    }

    override suspend fun clearHistory() {
        isCleared = true
    }
}

class FakeNovelDao : NovelDao {
    private val memory = mutableMapOf<String, NovelEntity>()
    private val flow = MutableStateFlow<List<NovelEntity>>(emptyList())

    override suspend fun getById(id: String): NovelEntity? = memory[id]

    override fun getAll(): Flow<List<NovelEntity>> = flow

    override suspend fun insert(novel: NovelEntity) {
        memory[novel.id] = novel
        flow.value = memory.values.toList()
    }

    override suspend fun insertAll(novels: List<NovelEntity>) {
        novels.forEach { memory[it.id] = it }
        flow.value = memory.values.toList()
    }

    override suspend fun deleteById(id: String) {
        memory.remove(id)
        flow.value = memory.values.toList()
    }

    override suspend fun updateCoverBase64(id: String, base64: String) {
        memory[id]?.let {
            memory[id] = it.copy(coverBase64 = base64)
        }
    }
}

class FakeChapterDao : ChapterDao {
    private val downloadedIds = mutableMapOf<String, List<String>>()
    val clearedNovelIds = mutableListOf<String>()

    fun setDownloadedChapterIds(novelId: String, ids: List<String>) {
        downloadedIds[novelId] = ids
    }

    override fun getChaptersForNovel(novelId: String): Flow<List<ChapterEntity>> {
        return MutableStateFlow(emptyList())
    }

    override suspend fun getChaptersListForNovel(novelId: String): List<ChapterEntity> = emptyList()

    override suspend fun getChaptersPaged(novelId: String, limit: Int, offset: Int): List<ChapterEntity> = emptyList()

    override suspend fun getChapterById(id: String): ChapterEntity? = null

    override suspend fun getChaptersByOrderRange(novelId: String, minOrder: Int, maxOrder: Int): List<ChapterEntity> = emptyList()

    override suspend fun getDownloadedChapterIds(novelId: String): List<String> {
        return downloadedIds[novelId] ?: emptyList()
    }

    override suspend fun getChapterCount(novelId: String): Int = 0

    override suspend fun insert(chapter: ChapterEntity) {}

    override suspend fun insertAll(chapters: List<ChapterEntity>) {}

    override suspend fun updateContent(id: String, content: String, isDownloaded: Boolean, downloadedAt: Long) {}

    override suspend fun deleteByNovelId(novelId: String) {}

    override suspend fun clearDownloadedContent(novelId: String) {
        clearedNovelIds.add(novelId)
        downloadedIds.remove(novelId)
    }
}

class FakeDownloadTaskDao : DownloadTaskDao {
    private val tasksMap = mutableMapOf<String, DownloadTaskEntity>()
    private val flow = MutableStateFlow<List<DownloadTaskEntity>>(emptyList())
    val deletedNovelIds = mutableListOf<String>()

    fun emitTasks(tasks: List<DownloadTaskEntity>) {
        tasks.forEach { tasksMap[it.novelId] = it }
        flow.value = tasksMap.values.toList()
    }

    override fun getAllTasks(): Flow<List<DownloadTaskEntity>> = flow

    override suspend fun getTask(novelId: String): DownloadTaskEntity? = tasksMap[novelId]

    override suspend fun insertOrReplace(task: DownloadTaskEntity) {
        tasksMap[task.novelId] = task
        flow.value = tasksMap.values.toList()
    }

    override suspend fun updateProgress(
        novelId: String,
        downloadedChapters: Int,
        status: String,
        updatedAt: Long
    ) {
        val existing = tasksMap[novelId]
        if (existing != null) {
            tasksMap[novelId] = existing.copy(
                downloadedChapters = downloadedChapters,
                status = status,
                updatedAt = updatedAt
            )
            flow.value = tasksMap.values.toList()
        }
    }

    override suspend fun delete(novelId: String) {
        deletedNovelIds.add(novelId)
        tasksMap.remove(novelId)
        flow.value = tasksMap.values.toList()
    }
}
