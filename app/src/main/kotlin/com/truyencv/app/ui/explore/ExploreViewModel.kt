package com.truyencv.app.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.NovelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val selectedSource: Source? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val bannerHotNovels: List<Novel> = emptyList(),
    val recommendedNovels: List<Novel> = emptyList(),
    val genresList: List<Genre> = emptyList(),
    val newUpdateNovels: List<Novel> = emptyList(),
    val newUpdatePage: Int = 1,
    val canLoadMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

class ExploreViewModel(
    private val novelRepository: NovelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadExploreData()
    }

    /**
     * Thay đổi bộ lọc nguồn (Tất cả | TruyenCV.io | MotTruyen) và tải lại toàn bộ nội dung.
     */
    fun selectSource(source: Source?) {
        if (_uiState.value.selectedSource == source && !_uiState.value.isLoading) return
        _uiState.update {
            it.copy(
                selectedSource = source,
                newUpdatePage = 1,
                canLoadMore = true,
                error = null
            )
        }
        loadExploreData()
    }

    /**
     * Kéo xuống để tải lại (Pull-to-refresh).
     */
    fun refresh() {
        _uiState.update {
            it.copy(
                isRefreshing = true,
                newUpdatePage = 1,
                canLoadMore = true,
                error = null
            )
        }
        loadExploreData()
    }

    /**
     * Thử lại khi gặp lỗi.
     */
    fun retry() {
        refresh()
    }

    /**
     * Tải thêm danh sách truyện mới cập nhật khi cuộn đến cuối trang (Infinite scroll).
     */
    fun loadMoreNewUpdates() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.canLoadMore) return

        val nextPage = currentState.newUpdatePage + 1
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                val moreNovels = novelRepository.getNewUpdates(currentState.selectedSource, nextPage)
                    .firstOrNull() ?: emptyList()

                _uiState.update { state ->
                    val combined = state.newUpdateNovels + moreNovels
                    state.copy(
                        newUpdateNovels = combined,
                        newUpdatePage = nextPage,
                        canLoadMore = moreNovels.isNotEmpty(),
                        isLoadingMore = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    private fun loadExploreData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            val isPullRefresh = _uiState.value.isRefreshing
            _uiState.update {
                it.copy(
                    isLoading = !isPullRefresh && it.newUpdateNovels.isEmpty(),
                    error = null
                )
            }

            val currentSource = _uiState.value.selectedSource

            try {
                coroutineScope {
                    val hotDeferred = async {
                        runCatching {
                            novelRepository.getHotNovels(currentSource, 1).firstOrNull() ?: emptyList()
                        }.getOrDefault(emptyList())
                    }
                    val recDeferred = async {
                        runCatching {
                            novelRepository.getRecommended(currentSource, 1).firstOrNull() ?: emptyList()
                        }.getOrDefault(emptyList())
                    }
                    val genresDeferred = async {
                        runCatching {
                            novelRepository.getGenres(currentSource).firstOrNull() ?: emptyList()
                        }.getOrDefault(emptyList())
                    }
                    val newUpdatesDeferred = async {
                        runCatching {
                            novelRepository.getNewUpdates(currentSource, 1).firstOrNull() ?: emptyList()
                        }.getOrDefault(emptyList())
                    }

                    val hot = hotDeferred.await()
                    val rec = recDeferred.await()
                    val genres = genresDeferred.await()
                    val newUpdates = newUpdatesDeferred.await()

                    val allEmpty = hot.isEmpty() && rec.isEmpty() && newUpdates.isEmpty() && genres.isEmpty()

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            bannerHotNovels = hot,
                            recommendedNovels = rec,
                            genresList = genres,
                            newUpdateNovels = newUpdates,
                            newUpdatePage = 1,
                            canLoadMore = newUpdates.isNotEmpty(),
                            error = if (allEmpty) "Không thể tải dữ liệu từ máy chủ. Vui lòng kiểm tra kết nối mạng." else null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.localizedMessage ?: "Đã xảy ra sự cố khi kết nối đến máy chủ."
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(novelRepository: NovelRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExploreViewModel(novelRepository) as T
                }
            }
    }
}
