package com.truyencv.app.ui.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.ErrorStateView
import com.truyencv.app.ui.components.GenreChip
import com.truyencv.app.ui.components.NovelListItem
import com.truyencv.app.ui.components.NovelListItemShimmer
import com.truyencv.app.ui.components.SearchBarField
import com.truyencv.app.ui.components.SourceChipGroup
import com.truyencv.app.ui.theme.TruyenCVTheme
import com.truyencv.app.util.GenreMappingTable

/**
 * Màn hình Tìm Kiếm & Bộ Lọc Nâng Cao theo SRS §5.3.
 */
@Composable
fun SearchScreen(
    initialGenre: String? = null,
    onBack: () -> Unit,
    onSelectNovel: (String) -> Unit,
    onSelectGenre: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.provideFactory(
            novelRepository = LocalAppContainer.current.novelRepository,
            initialGenre = initialGenre
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreenContent(
        uiState = uiState,
        onKeywordChange = viewModel::onKeywordChange,
        onSearch = viewModel::search,
        onGenreChange = viewModel::onGenreChange,
        onStatusChange = viewModel::onStatusChange,
        onChapterRangeChange = viewModel::onChapterRangeChange,
        onSortChange = viewModel::onSortChange,
        onSourceChange = viewModel::onSourceChange,
        onResetFilter = viewModel::resetFilter,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry,
        onToggleFilterSheet = viewModel::setFilterBottomSheetVisible,
        onBack = onBack,
        onSelectNovel = onSelectNovel,
        onSelectGenre = onSelectGenre,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    onKeywordChange: (String) -> Unit,
    onSearch: () -> Unit,
    onGenreChange: (String?) -> Unit,
    onStatusChange: (NovelStatus?) -> Unit,
    onChapterRangeChange: (ChapterFilterRange) -> Unit,
    onSortChange: (SearchSortOption) -> Unit,
    onSourceChange: (Source?) -> Unit,
    onResetFilter: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onToggleFilterSheet: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSelectNovel: (String) -> Unit,
    onSelectGenre: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Lazy load thêm kết quả khi cuộn đến cuối
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && uiState.canLoadMore && !uiState.isLoadingMore && !uiState.isSearching) {
            onLoadMore()
        }
    }

    val isAnyFilterActive = uiState.query.genre != null ||
            uiState.query.status != null ||
            uiState.selectedChapterRange != ChapterFilterRange.ALL ||
            uiState.sortOption != SearchSortOption.MODIFIED ||
            uiState.query.source != null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Header thanh tìm kiếm có nút quay lại & nút mở filter sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    SearchBarField(
                        query = uiState.query.keyword,
                        onQueryChange = onKeywordChange,
                        onSearch = { onSearch() },
                        placeholder = "Tìm kiếm tên truyện, tác giả...",
                        onClear = { onKeywordChange("") },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { onToggleFilterSheet(true) }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Bộ lọc nâng cao",
                            tint = if (isAnyFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dòng Quick Filter Chips cuộn ngang
                SearchQuickFilterBar(
                    uiState = uiState,
                    isAnyFilterActive = isAnyFilterActive,
                    onGenreChange = onGenreChange,
                    onStatusChange = onStatusChange,
                    onSortChange = onSortChange,
                    onSourceChange = onSourceChange,
                    onChapterRangeChange = onChapterRangeChange,
                    onResetFilter = onResetFilter,
                    onSelectGenre = onSelectGenre
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Đang tìm kiếm và chưa có kết quả hiển thị Shimmer
                uiState.isSearching && uiState.results.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        repeat(5) {
                            NovelListItemShimmer()
                        }
                    }
                }

                // Gặp lỗi kết nối
                uiState.error != null && uiState.results.isEmpty() -> {
                    ErrorStateView(
                        title = "Không thể tìm kiếm truyện",
                        message = uiState.error,
                        onRetry = onRetry
                    )
                }

                // Đã tìm kiếm nhưng không tìm thấy kết quả phù hợp
                uiState.hasSearched && uiState.results.isEmpty() && !uiState.isSearching -> {
                    EmptyStateView(
                        title = "Không tìm thấy truyện phù hợp",
                        message = "Thử thay đổi từ khóa hoặc bộ lọc để tìm kiếm truyện khác.",
                        actionLabel = "Đặt lại bộ lọc",
                        onActionClick = onResetFilter
                    )
                }

                // Chưa tìm kiếm và chưa có kết quả (Màn hình mở đầu)
                !uiState.hasSearched && uiState.results.isEmpty() -> {
                    EmptyStateView(
                        title = "Tìm kiếm truyện",
                        message = "Nhập tên truyện, tác giả hoặc chọn thể loại để bắt đầu tìm kiếm.",
                        icon = Icons.Default.Search
                    )
                }

                // Danh sách kết quả tìm kiếm
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(key = "results_header") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kết quả tìm kiếm (${uiState.results.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (isAnyFilterActive) {
                                    TextButton(onClick = onResetFilter) {
                                        Text(
                                            text = "Đặt lại",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        items(uiState.results, key = { it.id }) { novel ->
                            NovelListItem(
                                novel = novel,
                                onClick = { onSelectNovel(novel.id) }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item(key = "load_more_progress") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ModalBottomSheet Bộ lọc nâng cao chi tiết
    if (uiState.isFilterBottomSheetVisible) {
        SearchFilterBottomSheet(
            uiState = uiState,
            onDismiss = { onToggleFilterSheet(false) },
            onSourceChange = onSourceChange,
            onStatusChange = onStatusChange,
            onSortChange = onSortChange,
            onChapterRangeChange = onChapterRangeChange,
            onGenreChange = onGenreChange,
            onResetFilter = onResetFilter,
            onApply = {
                onToggleFilterSheet(false)
                onSearch()
            }
        )
    }
}

/**
 * Thanh Filter Chips cuộn ngang giúp lọc nhanh nguồn, sắp xếp, trạng thái, thể loại.
 */
@Composable
private fun SearchQuickFilterBar(
    uiState: SearchUiState,
    isAnyFilterActive: Boolean,
    onGenreChange: (String?) -> Unit,
    onStatusChange: (NovelStatus?) -> Unit,
    onSortChange: (SearchSortOption) -> Unit,
    onSourceChange: (Source?) -> Unit,
    onChapterRangeChange: (ChapterFilterRange) -> Unit,
    onResetFilter: () -> Unit,
    onSelectGenre: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút đặt lại nếu đang áp dụng bất kỳ bộ lọc nào
        if (isAnyFilterActive) {
            FilterChip(
                selected = false,
                onClick = onResetFilter,
                label = { Text("Đặt lại", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đặt lại",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        // Chip Thể loại đang chọn (nếu có)
        val selectedGenreName = uiState.query.genre?.let { slug ->
            GenreMappingTable.getCanonicalName(slug) ?: slug
        }
        if (selectedGenreName != null) {
            FilterChip(
                selected = true,
                onClick = { onGenreChange(null) },
                label = { Text("Thể loại: $selectedGenreName") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Bỏ chọn thể loại",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        } else {
            FilterChip(
                selected = false,
                onClick = onSelectGenre,
                label = { Text("Thể loại") }
            )
        }

        // Chips Nguồn
        Source.entries.forEach { source ->
            val isSelected = uiState.query.source == source
            FilterChip(
                selected = isSelected,
                onClick = {
                    onSourceChange(if (isSelected) null else source)
                },
                label = { Text(source.label) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }

        // Chips Trạng thái
        NovelStatus.entries.forEach { status ->
            val isSelected = uiState.query.status == status
            FilterChip(
                selected = isSelected,
                onClick = {
                    onStatusChange(if (isSelected) null else status)
                },
                label = { Text(status.label) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }

        // Chips Sắp xếp
        SearchSortOption.entries.forEach { sort ->
            val isSelected = uiState.sortOption == sort
            FilterChip(
                selected = isSelected,
                onClick = { onSortChange(sort) },
                label = { Text(sort.label) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    }
}

/**
 * BottomSheet hiển thị đầy đủ các tiêu chí lọc nâng cao: Nguồn, Trạng thái, Sắp xếp, Số chương, Thể loại.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFilterBottomSheet(
    uiState: SearchUiState,
    onDismiss: () -> Unit,
    onSourceChange: (Source?) -> Unit,
    onStatusChange: (NovelStatus?) -> Unit,
    onSortChange: (SearchSortOption) -> Unit,
    onChapterRangeChange: (ChapterFilterRange) -> Unit,
    onGenreChange: (String?) -> Unit,
    onResetFilter: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bộ Lọc Nâng Cao",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onResetFilter) {
                    Text("Đặt lại", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Nguồn truyện
            Text(
                text = "Nguồn truyện",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SourceChipGroup(
                selectedSource = uiState.query.source,
                onSourceSelected = onSourceChange,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // 2. Trạng thái
            Text(
                text = "Trạng thái",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAllStatus = uiState.query.status == null
                FilterChip(
                    selected = isAllStatus,
                    onClick = { onStatusChange(null) },
                    label = { Text("Tất cả") }
                )
                NovelStatus.entries.forEach { status ->
                    val isSelected = uiState.query.status == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusChange(status) },
                        label = { Text(status.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // 3. Sắp xếp
            Text(
                text = "Sắp xếp theo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchSortOption.entries.forEach { sort ->
                    val isSelected = uiState.sortOption == sort
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSortChange(sort) },
                        label = { Text(sort.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // 4. Số chương
            Text(
                text = "Số chương",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChapterFilterRange.entries.forEach { range ->
                    val isSelected = uiState.selectedChapterRange == range
                    FilterChip(
                        selected = isSelected,
                        onClick = { onChapterRangeChange(range) },
                        label = { Text(range.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút Áp Dụng
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Áp Dụng Bộ Lọc",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    TruyenCVTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                query = SearchQuery(keyword = "Phàm Nhân"),
                results = listOf(
                    Novel(
                        id = "tcv-1",
                        rawId = 1L,
                        title = "Phàm Nhân Tu Tiên",
                        author = "Vong Ngữ",
                        thumbUrl = "",
                        description = "Hàn Lập phi thăng tiên giới...",
                        genres = listOf("Tiên Hiệp", "Tu Chân"),
                        totalChapters = 1395,
                        views = 28000000,
                        status = NovelStatus.COMPLETED,
                        source = Source.TRUYENCV
                    )
                ),
                hasSearched = true
            ),
            onKeywordChange = {},
            onSearch = {},
            onGenreChange = {},
            onStatusChange = {},
            onChapterRangeChange = {},
            onSortChange = {},
            onSourceChange = {},
            onResetFilter = {},
            onLoadMore = {},
            onRetry = {},
            onToggleFilterSheet = {},
            onBack = {},
            onSelectNovel = {},
            onSelectGenre = {}
        )
    }
}
