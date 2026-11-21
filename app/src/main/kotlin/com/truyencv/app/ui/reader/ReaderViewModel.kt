package com.truyencv.app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.data.local.datastore.ReaderPreferences
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.repository.ChapterRepository
import com.truyencv.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the novel Reader screen.
 */
data class ReaderUiState(
    val novelId: String = "",
    val currentChapter: Chapter? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val fontSize: Float = ReaderPreferences.DEFAULT_FONT_SIZE,
    val fontFamily: String = ReaderPreferences.DEFAULT_FONT_FAMILY,
    val lineHeight: Float = ReaderPreferences.DEFAULT_LINE_HEIGHT,
    val backgroundColorHex: String = ReaderPreferences.DEFAULT_BG_COLOR,
    val textColorHex: String = ReaderPreferences.DEFAULT_TEXT_COLOR,
    val readerMode: String = ReaderPreferences.DEFAULT_READER_MODE,
    val keepScreenOn: Boolean = ReaderPreferences.DEFAULT_KEEP_SCREEN_ON,
    val isMenuVisible: Boolean = false,
    val allChapters: List<Chapter> = emptyList(),
    val currentChapterIndex: Int = 0,
    val scrollPosition: Int = 0
) {
    val totalChaptersCount: Int
        get() = allChapters.size

    val hasNextChapter: Boolean
        get() = currentChapterIndex < allChapters.size - 1 || currentChapter?.nextId != null

    val hasPrevChapter: Boolean
        get() = currentChapterIndex > 0 || currentChapter?.prevId != null
}

/**
 * ViewModel managing the reading experience, chapter content loading,
 * prefetching ±6 buffer chapters, auto-saving reading progress, and reader preferences.
 */
class ReaderViewModel(
    val novelId: String,
    val initialChapterId: String,
    private val chapterRepository: ChapterRepository,
    private val historyRepository: HistoryRepository,
    private val readerPreferences: ReaderPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReaderUiState(
            novelId = novelId,
            isLoading = true
        )
    )
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
        loadInitialData()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            readerPreferences.fontSize.collect { size ->
                _uiState.update { it.copy(fontSize = size) }
            }
        }
        viewModelScope.launch {
            readerPreferences.fontFamily.collect { family ->
                _uiState.update { it.copy(fontFamily = family) }
            }
        }
        viewModelScope.launch {
            readerPreferences.lineHeight.collect { height ->
                _uiState.update { it.copy(lineHeight = height) }
            }
        }
        viewModelScope.launch {
            readerPreferences.backgroundColor.collect { bg ->
                _uiState.update { it.copy(backgroundColorHex = bg) }
            }
        }
        viewModelScope.launch {
            readerPreferences.textColor.collect { text ->
                _uiState.update { it.copy(textColorHex = text) }
            }
        }
        viewModelScope.launch {
            readerPreferences.readerMode.collect { mode ->
                _uiState.update { it.copy(readerMode = mode) }
            }
        }
        viewModelScope.launch {
            readerPreferences.keepScreenOn.collect { keepOn ->
                _uiState.update { it.copy(keepScreenOn = keepOn) }
            }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val chapters = chapterRepository.getAllChapters(novelId)
                _uiState.update { it.copy(allChapters = chapters) }

                val targetChapterId = when {
                    chapters.any { it.id == initialChapterId } -> initialChapterId
                    chapters.isNotEmpty() -> chapters.first().id
                    initialChapterId.isNotBlank() && !initialChapterId.matches(Regex("^\\d+$")) -> initialChapterId
                    else -> ""
                }

                if (targetChapterId.isNotBlank()) {
                    loadChapterInternal(targetChapterId)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Không tìm thấy danh sách chương cho truyện này"
                        )
                    }
                }
            } catch (e: Exception) {
                // If fetching full list failed, attempt loading initial chapter directly
                if (initialChapterId.isNotBlank()) {
                    loadChapterInternal(initialChapterId)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "Lỗi tải thông tin chương"
                        )
                    }
                }
            }
        }
    }

    fun navigateToChapter(chapterId: String) {
        if (chapterId.isBlank()) return
        viewModelScope.launch {
            loadChapterInternal(chapterId)
        }
    }

    fun navigateToChapterByIndex(index: Int) {
        val chapters = _uiState.value.allChapters
        if (index in chapters.indices) {
            navigateToChapter(chapters[index].id)
        }
    }

    fun navigateToNextChapter() {
        val state = _uiState.value
        val nextFromList = state.allChapters.getOrNull(state.currentChapterIndex + 1)?.id
        val nextId = nextFromList ?: state.currentChapter?.nextId
        if (!nextId.isNullOrBlank()) {
            navigateToChapter(nextId)
        }
    }

    fun navigateToPrevChapter() {
        val state = _uiState.value
        val prevFromList = if (state.currentChapterIndex > 0) {
            state.allChapters.getOrNull(state.currentChapterIndex - 1)?.id
        } else {
            null
        }
        val prevId = prevFromList ?: state.currentChapter?.prevId
        if (!prevId.isNullOrBlank()) {
            navigateToChapter(prevId)
        }
    }

    private suspend fun loadChapterInternal(chapterId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val chapter = chapterRepository.getChapterContent(chapterId)
            val chapters = _uiState.value.allChapters
            val foundIndex = chapters.indexOfFirst { it.id == chapterId }
            val currentIndex = if (foundIndex >= 0) foundIndex else _uiState.value.currentChapterIndex

            _uiState.update {
                it.copy(
                    currentChapter = chapter,
                    currentChapterIndex = currentIndex,
                    isLoading = false,
                    error = null
                )
            }

            // Automatically persist progress to history repository
            val progress = ReadingProgress(
                novelId = novelId,
                chapterId = chapter.id,
                chapterOrder = chapter.chapterOrder,
                chapterTitle = chapter.title,
                scrollPosition = 0,
                lastReadTime = System.currentTimeMillis()
            )
            historyRepository.saveProgress(progress)

            // Prefetch buffer ±6 chapters in background
            prefetchChapterBuffer(chapter.chapterOrder)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Không thể tải nội dung chương"
                )
            }
        }
    }

    private fun prefetchChapterBuffer(currentOrder: Int) {
        viewModelScope.launch {
            try {
                chapterRepository.getChapterBuffer(
                    novelId = novelId,
                    currentChapterOrder = currentOrder,
                    bufferSize = 6
                )
            } catch (_: Exception) {
                // Background prefetch errors are non-blocking
            }
        }
    }

    fun setFontSize(size: Float) {
        val clamped = size.coerceIn(12f, 36f)
        viewModelScope.launch {
            readerPreferences.setFontSize(clamped)
        }
    }

    fun setFontFamily(family: String) {
        viewModelScope.launch {
            readerPreferences.setFontFamily(family)
        }
    }

    fun setLineHeight(height: Float) {
        val clamped = height.coerceIn(1.2f, 2.5f)
        viewModelScope.launch {
            readerPreferences.setLineHeight(clamped)
        }
    }

    fun setTheme(bgHex: String, textHex: String) {
        viewModelScope.launch {
            readerPreferences.setBackgroundColor(bgHex)
            readerPreferences.setTextColor(textHex)
        }
    }

    fun setReaderMode(mode: String) {
        viewModelScope.launch {
            readerPreferences.setReaderMode(mode)
        }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            readerPreferences.setKeepScreenOn(enabled)
        }
    }

    fun toggleMenu() {
        _uiState.update { it.copy(isMenuVisible = !it.isMenuVisible) }
    }

    fun setMenuVisible(visible: Boolean) {
        _uiState.update { it.copy(isMenuVisible = visible) }
    }

    fun updateScrollPosition(position: Int) {
        _uiState.update { it.copy(scrollPosition = position) }
        val current = _uiState.value.currentChapter ?: return
        viewModelScope.launch {
            try {
                historyRepository.saveProgress(
                    ReadingProgress(
                        novelId = novelId,
                        chapterId = current.id,
                        chapterOrder = current.chapterOrder,
                        chapterTitle = current.title,
                        scrollPosition = position,
                        lastReadTime = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {
                // Ignore transient history save failures on scroll
            }
        }
    }

    companion object {
        fun provideFactory(
            novelId: String,
            initialChapterId: String,
            chapterRepository: ChapterRepository,
            historyRepository: HistoryRepository,
            readerPreferences: ReaderPreferences
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(
                    novelId = novelId,
                    initialChapterId = initialChapterId,
                    chapterRepository = chapterRepository,
                    historyRepository = historyRepository,
                    readerPreferences = readerPreferences
                ) as T
            }
        }
    }
}
