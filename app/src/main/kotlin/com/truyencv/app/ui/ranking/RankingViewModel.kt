package com.truyencv.app.ui.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.NovelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RankingUiState(
    val selectedPeriod: RankingPeriod = RankingPeriod.DAY,
    val selectedSource: Source? = null,
    val rankingNovels: List<Novel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val canLoadMore: Boolean = true
)

class RankingViewModel(
    private val novelRepository: NovelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadRanking(page = 1, isRefresh = false)
    }

    fun selectPeriod(period: RankingPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update { it.copy(selectedPeriod = period, page = 1, canLoadMore = true) }
        loadRanking(page = 1, isRefresh = false)
    }

    fun selectSource(source: Source?) {
        if (_uiState.value.selectedSource == source) return
        _uiState.update { it.copy(selectedSource = source, page = 1, canLoadMore = true) }
        loadRanking(page = 1, isRefresh = false)
    }

    fun refresh() {
        loadRanking(page = 1, isRefresh = true)
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.canLoadMore) return
        val nextPage = currentState.page + 1

        if (loadMoreJob?.isActive == true) return
        loadMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, error = null) }
            novelRepository.getRanking(
                period = currentState.selectedPeriod,
                source = currentState.selectedSource,
                page = nextPage
            ).catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        error = throwable.localizedMessage ?: "Lỗi khi tải thêm bảng xếp hạng"
                    )
                }
            }.collect { newNovels ->
                _uiState.update { current ->
                    val existingIds = current.rankingNovels.map { novel -> novel.id }.toSet()
                    val distinctNewNovels = newNovels.filter { novel -> novel.id !in existingIds }
                    current.copy(
                        rankingNovels = current.rankingNovels + distinctNewNovels,
                        page = nextPage,
                        canLoadMore = newNovels.isNotEmpty(),
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    private fun loadRanking(page: Int, isRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null,
                    page = page
                )
            }
            val period = _uiState.value.selectedPeriod
            val source = _uiState.value.selectedSource

            novelRepository.getRanking(period = period, source = source, page = page)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = throwable.localizedMessage ?: "Đã xảy ra lỗi khi tải bảng xếp hạng"
                        )
                    }
                }
                .collect { novels ->
                    _uiState.update {
                        it.copy(
                            rankingNovels = novels,
                            isLoading = false,
                            isRefreshing = false,
                            canLoadMore = novels.isNotEmpty(),
                            error = null
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
                    return RankingViewModel(novelRepository) as T
                }
            }
    }
}
