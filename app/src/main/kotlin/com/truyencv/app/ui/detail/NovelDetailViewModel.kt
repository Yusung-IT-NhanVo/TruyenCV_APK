package com.truyencv.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.repository.BookmarkRepository
import com.truyencv.app.domain.repository.ChapterRepository
import com.truyencv.app.domain.repository.HistoryRepository
import com.truyencv.app.domain.repository.NovelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovelDetailUiState(
    val novelId: String = "",
    val novel: Novel? = null,
    val chapters: List<Chapter> = emptyList(),
    val isBookmarked: Boolean = false,
    val lastProgress: ReadingProgress? = null,
    val isLoadingNovel: Boolean = false,
    val isLoadingChapters: Boolean = false,
    val isSortAsc: Boolean = true,
    val error: String? = null,
    val isDownloading: Boolean = false,
    val downloadMessage: String? = null
) {
    val displayChapters: List<Chapter>
        get() = if (isSortAsc) {
            chapters.sortedBy { it.chapterOrder }
        } else {
            chapters.sortedByDescending { it.chapterOrder }
        }
}

class NovelDetailViewModel(
    val novelId: String,
    private val novelRepository: NovelRepository,
    private val chapterRepository: ChapterRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovelDetailUiState(novelId = novelId))
    val uiState: StateFlow<NovelDetailUiState> = _uiState.asStateFlow()

    init {
        loadNovelDetail()
    }

    fun loadNovelDetail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingNovel = true,
                    isLoadingChapters = true,
                    error = null
                )
            }

            // 1. Tải thông tin chi tiết truyện
            try {
                val novel = novelRepository.getNovelDetail(novelId)
                _uiState.update { it.copy(novel = novel, isLoadingNovel = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingNovel = false,
                        error = e.localizedMessage ?: "Không thể tải thông tin truyện"
                    )
                }
            }

            // 2. Kiểm tra trạng thái đã đánh dấu / theo dõi
            try {
                val bookmarked = bookmarkRepository.isBookmarked(novelId)
                _uiState.update { it.copy(isBookmarked = bookmarked) }
            } catch (_: Exception) {
                // Giữ trạng thái mặc định false nếu lỗi
            }

            // 3. Đọc tiến độ đọc gần nhất
            try {
                val progress = historyRepository.getProgress(novelId)
                _uiState.update { it.copy(lastProgress = progress) }
            } catch (_: Exception) {
                // Không bắt buộc phải có tiến độ
            }

            // 4. Tải danh sách toàn bộ chương
            try {
                val chapters = chapterRepository.getAllChapters(novelId)
                _uiState.update {
                    it.copy(
                        chapters = chapters,
                        isLoadingChapters = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingChapters = false,
                        error = if (it.novel == null) {
                            e.localizedMessage ?: "Lỗi khi tải danh sách chương"
                        } else {
                            it.error // Không ghi đè nếu novel đã tải thành công
                        }
                    )
                }
            }
        }
    }

    fun toggleBookmark() {
        val novel = _uiState.value.novel ?: return
        viewModelScope.launch {
            try {
                val isNowBookmarked = bookmarkRepository.toggleBookmark(novel)
                _uiState.update { it.copy(isBookmarked = isNowBookmarked) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.localizedMessage ?: "Không thể cập nhật theo dõi truyện")
                }
            }
        }
    }

    fun toggleChapterSort() {
        _uiState.update { it.copy(isSortAsc = !it.isSortAsc) }
    }

    fun downloadAllChapters() {
        if (_uiState.value.isDownloading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true, downloadMessage = null) }
            try {
                val all = chapterRepository.getAllChapters(novelId)
                _uiState.update {
                    it.copy(
                        chapters = all,
                        isDownloading = false,
                        downloadMessage = "Đã tải xong toàn bộ ${all.size} chương offline!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        downloadMessage = "Lỗi khi tải chương: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            novelId: String,
            novelRepository: NovelRepository,
            chapterRepository: ChapterRepository,
            bookmarkRepository: BookmarkRepository,
            historyRepository: HistoryRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NovelDetailViewModel(
                    novelId = novelId,
                    novelRepository = novelRepository,
                    chapterRepository = chapterRepository,
                    bookmarkRepository = bookmarkRepository,
                    historyRepository = historyRepository
                ) as T
            }
        }
    }
}
