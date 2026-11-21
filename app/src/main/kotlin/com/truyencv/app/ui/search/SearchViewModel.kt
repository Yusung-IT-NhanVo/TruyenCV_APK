package com.truyencv.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.NovelRepository
import com.truyencv.app.util.GenreMappingTable
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Khoảng số chương lọc truyện (SRS §5.3).
 */
enum class ChapterFilterRange(
    val label: String,
    val minChapters: Int?,
    val maxChapters: Int?
) {
    ALL("Tất cả", null, null),
    LESS_THAN_100("< 100", null, 100),
    FROM_100_TO_300("100 - 300", 100, 300),
    FROM_300_TO_600("300 - 600", 300, 600),
    FROM_600_TO_1000("600 - 1000", 600, 1000),
    MORE_THAN_1000("> 1000", 1000, null)
}

/**
 * Tùy chọn sắp xếp tìm kiếm (SRS §5.3).
 */
enum class SearchSortOption(val value: String, val label: String) {
    MODIFIED("modified", "Mới cập nhật"),
    DATE("date", "Mới đăng"),
    VIEWS("views", "Lượt đọc"),
    RATING("rating", "Đánh giá")
}

data class SearchUiState(
    val query: SearchQuery = SearchQuery(),
    val selectedChapterRange: ChapterFilterRange = ChapterFilterRange.ALL,
    val sortOption: SearchSortOption = SearchSortOption.MODIFIED,
    val isSearching: Boolean = false,
    val results: List<Novel> = emptyList(),
    val page: Int = 1,
    val canLoadMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val availableGenres: List<Genre> = emptyList(),
    val isFilterBottomSheetVisible: Boolean = false
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val novelRepository: NovelRepository,
    initialGenre: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(
            query = SearchQuery(
                genre = initialGenre
            )
        )
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val keywordInputFlow = MutableStateFlow("")
    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadGenres()

        // Debounce từ khóa nhập liệu (400ms) để tự động tìm kiếm
        viewModelScope.launch {
            keywordInputFlow
                .debounce(400L)
                .distinctUntilChanged()
                .collect { kw ->
                    val currentState = _uiState.value
                    if (kw.isNotBlank() || currentState.query.genre != null) {
                        performSearch(page = 1)
                    } else if (kw.isBlank() && currentState.query.genre == null && !currentState.hasSearched) {
                        _uiState.update { it.copy(results = emptyList(), isSearching = false, error = null) }
                    }
                }
        }

        // Nếu mở từ màn Thể loại với initialGenre thì tìm kiếm ngay
        if (!initialGenre.isNullOrBlank()) {
            performSearch(page = 1)
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                val fetchedGenres = novelRepository.getGenres(null).firstOrNull() ?: emptyList()
                val genres = if (fetchedGenres.isNotEmpty()) {
                    fetchedGenres
                } else {
                    GenreMappingTable.getAllCanonicalGenres().map {
                        Genre(id = it.slug, name = it.name, slug = it.slug, count = 0, source = null)
                    }
                }
                _uiState.update { it.copy(availableGenres = genres) }
            } catch (_: Exception) {
                val fallbackGenres = GenreMappingTable.getAllCanonicalGenres().map {
                    Genre(id = it.slug, name = it.name, slug = it.slug, count = 0, source = null)
                }
                _uiState.update { it.copy(availableGenres = fallbackGenres) }
            }
        }
    }

    /**
     * Cập nhật từ khóa tìm kiếm khi người dùng gõ phím.
     */
    fun onKeywordChange(keyword: String) {
        _uiState.update { it.copy(query = it.query.copy(keyword = keyword)) }
        keywordInputFlow.value = keyword
    }

    /**
     * Thay đổi bộ lọc thể loại.
     */
    fun onGenreChange(genreSlug: String?) {
        val newGenre = if (genreSlug.isNullOrBlank()) null else genreSlug
        _uiState.update {
            it.copy(query = it.query.copy(genre = newGenre))
        }
        performSearch(page = 1)
    }

    /**
     * Thay đổi bộ lọc trạng thái (Đang ra / Hoàn thành / Tất cả).
     */
    fun onStatusChange(status: NovelStatus?) {
        _uiState.update {
            it.copy(query = it.query.copy(status = status))
        }
        performSearch(page = 1)
    }

    /**
     * Thay đổi bộ lọc số chương.
     */
    fun onChapterRangeChange(range: ChapterFilterRange) {
        _uiState.update {
            it.copy(
                selectedChapterRange = range,
                query = it.query.copy(
                    minChapters = range.minChapters,
                    maxChapters = range.maxChapters
                )
            )
        }
        performSearch(page = 1)
    }

    /**
     * Thay đổi tiêu chí sắp xếp.
     */
    fun onSortChange(sort: SearchSortOption) {
        _uiState.update {
            it.copy(
                sortOption = sort,
                query = it.query.copy(sort = sort.value)
            )
        }
        performSearch(page = 1)
    }

    /**
     * Thay đổi nguồn tìm kiếm (Tất cả / TruyenCV / MotTruyen).
     */
    fun onSourceChange(source: Source?) {
        _uiState.update {
            it.copy(query = it.query.copy(source = source))
        }
        performSearch(page = 1)
    }

    /**
     * Kích hoạt tìm kiếm ngay lập tức (khi nhấn Enter trên bàn phím).
     */
    fun search() {
        performSearch(page = 1)
    }

    /**
     * Xóa sạch các bộ lọc về mặc định ban đầu.
     */
    fun resetFilter() {
        keywordInputFlow.value = ""
        _uiState.update {
            it.copy(
                query = SearchQuery(),
                selectedChapterRange = ChapterFilterRange.ALL,
                sortOption = SearchSortOption.MODIFIED,
                results = emptyList(),
                page = 1,
                canLoadMore = true,
                isLoadingMore = false,
                error = null,
                hasSearched = false
            )
        }
    }

    /**
     * Hiển thị hoặc ẩn BottomSheet bộ lọc nâng cao.
     */
    fun setFilterBottomSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(isFilterBottomSheetVisible = visible) }
    }

    /**
     * Tải thêm kết quả tìm kiếm trang tiếp theo khi cuộn đến cuối.
     */
    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isSearching || currentState.isLoadingMore || !currentState.canLoadMore) return

        val nextPage = currentState.page + 1
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                val moreNovels = novelRepository.searchNovels(currentState.query.copy(page = nextPage))
                    .firstOrNull() ?: emptyList()

                _uiState.update { state ->
                    val combined = state.results + moreNovels
                    state.copy(
                        results = combined,
                        page = nextPage,
                        canLoadMore = moreNovels.isNotEmpty(),
                        isLoadingMore = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    fun retry() {
        performSearch(page = _uiState.value.page)
    }

    private fun performSearch(page: Int) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    page = page,
                    error = null,
                    hasSearched = true
                )
            }

            try {
                val searchQuery = _uiState.value.query.copy(page = page)
                val novels = novelRepository.searchNovels(searchQuery).firstOrNull() ?: emptyList()

                _uiState.update { state ->
                    state.copy(
                        isSearching = false,
                        results = novels,
                        canLoadMore = novels.isNotEmpty(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isSearching = false,
                        error = e.localizedMessage ?: "Không thể tìm kiếm truyện. Vui lòng thử lại."
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            novelRepository: NovelRepository,
            initialGenre: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(novelRepository, initialGenre) as T
            }
        }
    }
}
