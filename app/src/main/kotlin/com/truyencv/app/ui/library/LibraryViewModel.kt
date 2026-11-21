package com.truyencv.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.data.local.db.ChapterDao
import com.truyencv.app.data.local.db.DownloadTaskDao
import com.truyencv.app.data.local.db.DownloadTaskEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.mapper.TruyenCvMapper
import com.truyencv.app.di.AppContainer
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.BookmarkRepository
import com.truyencv.app.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 4 tab nội bộ của Tủ truyện theo SRS §5.8:
 * - 📚 Theo dõi (Bookmarks)
 * - 🕒 Lịch sử (Reading History)
 * - 💾 Offline (Downloaded Novels)
 * - ⏳ Đang tải (Download Queue & Progress)
 */
enum class LibraryTab(val title: String, val emoji: String) {
    FOLLOW("Theo dõi", "📚"),
    HISTORY("Lịch sử", "🕒"),
    OFFLINE("Offline", "💾"),
    DOWNLOADING("Đang tải", "⏳")
}

/**
 * Mục dữ liệu hiển thị trong tab Lịch sử đọc, kết hợp tiến độ đọc và thông tin truyện.
 */
data class ReadingHistoryItem(
    val novelId: String,
    val novelTitle: String,
    val novelAuthor: String,
    val thumbUrl: String,
    val coverBase64: String? = null,
    val chapterId: String,
    val chapterOrder: Int,
    val chapterTitle: String,
    val scrollPosition: Int = 0,
    val lastReadTime: Long = 0L,
    val source: Source = Source.TRUYENCV
)

/**
 * Mục truyện tải offline kèm số lượng chương khả dụng trong SQLite local.
 */
data class OfflineNovelItem(
    val novel: Novel,
    val downloadedChaptersCount: Int
)

/**
 * Toàn bộ UI state của màn hình Tủ truyện.
 */
data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.FOLLOW,
    val bookmarks: List<Novel> = emptyList(),
    val history: List<ReadingHistoryItem> = emptyList(),
    val offlineNovels: List<OfflineNovelItem> = emptyList(),
    val downloadTasks: List<DownloadTaskEntity> = emptyList(),
    val isLoading: Boolean = false
)

class LibraryViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val historyRepository: HistoryRepository,
    private val novelDao: NovelDao,
    private val chapterDao: ChapterDao,
    private val downloadTaskDao: DownloadTaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LibraryTab.FOLLOW)
    val selectedTab: StateFlow<LibraryTab> = _selectedTab.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)

    val bookmarks: StateFlow<List<Novel>> = bookmarkRepository.getBookmarks()
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val history: StateFlow<List<ReadingHistoryItem>> = historyRepository.getHistory()
        .map { progressList ->
            withContext(ioDispatcher) {
                progressList.map { progress ->
                    val novelEntity = novelDao.getById(progress.novelId)
                    if (novelEntity != null) {
                        val novel = TruyenCvMapper.entityToNovel(novelEntity)
                        ReadingHistoryItem(
                            novelId = progress.novelId,
                            novelTitle = novel.title,
                            novelAuthor = novel.author,
                            thumbUrl = novel.thumbUrl,
                            coverBase64 = novel.coverBase64,
                            chapterId = progress.chapterId,
                            chapterOrder = progress.chapterOrder,
                            chapterTitle = progress.chapterTitle.ifBlank { "Chương ${progress.chapterOrder}" },
                            scrollPosition = progress.scrollPosition,
                            lastReadTime = progress.lastReadTime,
                            source = novel.source
                        )
                    } else {
                        ReadingHistoryItem(
                            novelId = progress.novelId,
                            novelTitle = "Truyện #${progress.novelId}",
                            novelAuthor = "Đang cập nhật",
                            thumbUrl = "",
                            coverBase64 = null,
                            chapterId = progress.chapterId,
                            chapterOrder = progress.chapterOrder,
                            chapterTitle = progress.chapterTitle.ifBlank { "Chương ${progress.chapterOrder}" },
                            scrollPosition = progress.scrollPosition,
                            lastReadTime = progress.lastReadTime,
                            source = Source.fromId(progress.novelId)
                        )
                    }
                }
            }
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val offlineNovels: StateFlow<List<OfflineNovelItem>> = combine(
        novelDao.getAll(),
        downloadTaskDao.getAllTasks(),
        _refreshTrigger
    ) { entities, _, _ ->
        withContext(ioDispatcher) {
            entities.mapNotNull { entity ->
                val downloadedIds = chapterDao.getDownloadedChapterIds(entity.id)
                if (downloadedIds.isNotEmpty()) {
                    OfflineNovelItem(
                        novel = TruyenCvMapper.entityToNovel(entity),
                        downloadedChaptersCount = downloadedIds.size
                    )
                } else {
                    null
                }
            }
        }
    }
    .flowOn(ioDispatcher)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val downloadTasks: StateFlow<List<DownloadTaskEntity>> = downloadTaskDao.getAllTasks()
        .map { tasks ->
            tasks.filter { it.status != "COMPLETED" }
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<LibraryUiState> = combine(
        _selectedTab,
        bookmarks,
        history,
        offlineNovels,
        downloadTasks
    ) { tab, bm, hist, off, dl ->
        LibraryUiState(
            selectedTab = tab,
            bookmarks = bm,
            history = hist,
            offlineNovels = off,
            downloadTasks = dl,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState(isLoading = true)
    )

    fun selectTab(tab: LibraryTab) {
        _selectedTab.value = tab
    }

    fun removeBookmark(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            bookmarkRepository.removeBookmark(novelId)
        }
    }

    fun removeHistory(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            historyRepository.deleteProgress(novelId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(ioDispatcher) {
            historyRepository.clearHistory()
        }
    }

    fun removeOfflineNovel(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            chapterDao.clearDownloadedContent(novelId)
            downloadTaskDao.delete(novelId)
            _refreshTrigger.value += 1
        }
    }

    fun pauseDownload(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            val task = downloadTaskDao.getTask(novelId)
            if (task != null) {
                downloadTaskDao.updateProgress(
                    novelId = novelId,
                    downloadedChapters = task.downloadedChapters,
                    status = "PAUSED",
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    fun resumeDownload(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            val task = downloadTaskDao.getTask(novelId)
            if (task != null) {
                downloadTaskDao.updateProgress(
                    novelId = novelId,
                    downloadedChapters = task.downloadedChapters,
                    status = "QUEUED",
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    fun cancelDownload(novelId: String) {
        viewModelScope.launch(ioDispatcher) {
            downloadTaskDao.delete(novelId)
            chapterDao.clearDownloadedContent(novelId)
            _refreshTrigger.value += 1
        }
    }

    companion object {
        fun provideFactory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LibraryViewModel(
                        bookmarkRepository = appContainer.bookmarkRepository,
                        historyRepository = appContainer.historyRepository,
                        novelDao = appContainer.database.novelDao(),
                        chapterDao = appContainer.database.chapterDao(),
                        downloadTaskDao = appContainer.database.downloadTaskDao()
                    ) as T
                }
            }
    }
}
