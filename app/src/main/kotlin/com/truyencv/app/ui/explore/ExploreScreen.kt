package com.truyencv.app.ui.explore

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.components.BannerShimmer
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.ErrorStateView
import com.truyencv.app.ui.components.GenreFlowRow
import com.truyencv.app.ui.components.HorizontalCarousel
import com.truyencv.app.ui.components.NovelCardShimmer
import com.truyencv.app.ui.components.NovelHorizontalRow
import com.truyencv.app.ui.components.NovelListItem
import com.truyencv.app.ui.components.NovelListItemShimmer
import com.truyencv.app.ui.components.SearchBarField
import com.truyencv.app.ui.components.SourceChipGroup
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Màn hình Khám Phá (Home / Explore) theo SRS §5.1.
 * Bố cục feed cuộn dọc tích hợp Pull-to-Refresh, Carousel Banner, Đề cử, Thể loại nhanh, Mới cập nhật và Lazy loading.
 */
@Composable
fun ExploreScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToGenre: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModel.provideFactory(LocalAppContainer.current.novelRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    ExploreScreenContent(
        uiState = uiState,
        onSelectSource = viewModel::selectSource,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMoreNewUpdates,
        onRetry = viewModel::retry,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToGenre = onNavigateToGenre,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreenContent(
    uiState: ExploreUiState,
    onSelectSource: (Source?) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToGenre: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Tự động tải thêm khi cuộn đến gần cuối danh sách
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && uiState.canLoadMore && !uiState.isLoadingMore && !uiState.isLoading) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        when {
            // Trạng thái Loading ban đầu khi chưa có dữ liệu
            uiState.isLoading && uiState.newUpdateNovels.isEmpty() -> {
                ExploreSkeletonView(
                    selectedSource = uiState.selectedSource,
                    onSelectSource = onSelectSource,
                    onNavigateToSearch = onNavigateToSearch
                )
            }

            // Trạng thái Lỗi kết nối khi danh sách trống
            uiState.error != null && uiState.newUpdateNovels.isEmpty() && uiState.bannerHotNovels.isEmpty() -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBarField(
                        query = "",
                        onQueryChange = {},
                        onSearch = {},
                        readOnly = true,
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    SourceChipGroup(
                        selectedSource = uiState.selectedSource,
                        onSourceSelected = onSelectSource
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateView(
                            title = "Không thể tải khám phá",
                            message = uiState.error,
                            onRetry = onRetry
                        )
                    }
                }
            }

            // Trạng thái Trống dữ liệu
            !uiState.isLoading && uiState.newUpdateNovels.isEmpty() && uiState.bannerHotNovels.isEmpty() && uiState.recommendedNovels.isEmpty() -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBarField(
                        query = "",
                        onQueryChange = {},
                        onSearch = {},
                        readOnly = true,
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    SourceChipGroup(
                        selectedSource = uiState.selectedSource,
                        onSourceSelected = onSelectSource
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateView(
                            title = "Không có truyện nào",
                            message = "Hiện tại chưa có dữ liệu từ nguồn đã chọn.",
                            actionLabel = "Tải lại",
                            onActionClick = onRefresh
                        )
                    }
                }
            }

            // Danh sách nội dung thực tế
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. Thanh tìm kiếm (chế độ readOnly để tap mở SearchScreen)
                    item(key = "search_bar") {
                        SearchBarField(
                            query = "",
                            onQueryChange = {},
                            onSearch = {},
                            readOnly = true,
                            onClick = onNavigateToSearch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // 2. Bộ chọn nguồn (Tất cả | TruyenCV.io | MotTruyen)
                    item(key = "source_chips") {
                        SourceChipGroup(
                            selectedSource = uiState.selectedSource,
                            onSourceSelected = onSelectSource
                        )
                    }

                    // 3. Banner Truyện Hot (Carousel tự động cuộn)
                    if (uiState.bannerHotNovels.isNotEmpty()) {
                        item(key = "banner_hot") {
                            HorizontalCarousel(
                                novels = uiState.bannerHotNovels,
                                onNovelClick = { onNavigateToDetail(it.id) },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // 4. Section "Đề cử" (NovelHorizontalRow cuộn ngang)
                    if (uiState.recommendedNovels.isNotEmpty()) {
                        item(key = "recommended_row") {
                            NovelHorizontalRow(
                                title = "Đề cử nổi bật",
                                novels = uiState.recommendedNovels,
                                onNovelClick = { onNavigateToDetail(it.id) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    // 5. Section "Thể loại nhanh" (GenreFlowRow dạng flow không xung đột cuộn)
                    if (uiState.genresList.isNotEmpty()) {
                        item(key = "genres_section") {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Thể loại nhanh",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    TextButton(onClick = { onNavigateToGenre("") }) {
                                        Text(
                                            text = "Xem tất cả",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                GenreFlowRow(
                                    genres = uiState.genresList.take(8),
                                    selectedGenreSlug = null,
                                    onGenreClick = { onNavigateToGenre(it.slug) }
                                )
                            }
                        }
                    }

                    // 6. Section "Mới cập nhật" Header
                    if (uiState.newUpdateNovels.isNotEmpty()) {
                        item(key = "new_updates_header") {
                            Text(
                                text = "Mới cập nhật",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                            )
                        }

                        // Danh sách truyện mới cập nhật
                        items(uiState.newUpdateNovels, key = { it.id }) { novel ->
                            NovelListItem(
                                novel = novel,
                                onClick = { onNavigateToDetail(novel.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 7. Loading indicator khi cuộn cuối tải thêm trang mới
                    if (uiState.isLoadingMore) {
                        item(key = "loading_more_indicator") {
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

/**
 * Shimmer placeholder hiển thị khung xương khi tải trang Khám Phá ban đầu.
 */
@Composable
private fun ExploreSkeletonView(
    selectedSource: Source?,
    onSelectSource: (Source?) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            SearchBarField(
                query = "",
                onQueryChange = {},
                onSearch = {},
                readOnly = true,
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            SourceChipGroup(
                selectedSource = selectedSource,
                onSourceSelected = onSelectSource
            )
        }

        item {
            BannerShimmer()
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Đang tải đề cử...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(3) {
                    NovelCardShimmer()
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Mới cập nhật",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(4) {
            NovelListItemShimmer()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreScreenPreview() {
    TruyenCVTheme {
        ExploreScreenContent(
            uiState = ExploreUiState(
                isLoading = false,
                bannerHotNovels = listOf(
                    Novel(
                        id = "tcv-1",
                        rawId = 1L,
                        title = "Phàm Nhân Tu Tiên",
                        author = "Vong Ngữ",
                        thumbUrl = "",
                        description = "Mô tả truyện",
                        genres = listOf("Tiên Hiệp"),
                        totalChapters = 1500,
                        views = 200000,
                        status = NovelStatus.COMPLETED,
                        source = Source.TRUYENCV
                    )
                ),
                recommendedNovels = listOf(
                    Novel(
                        id = "mt-2",
                        rawId = 2L,
                        title = "Đấu La Đại Lục",
                        author = "Đường Gia Tam Thiếu",
                        thumbUrl = "",
                        description = "Mô tả truyện",
                        genres = listOf("Huyền Huyễn"),
                        totalChapters = 1200,
                        views = 150000,
                        status = NovelStatus.ONGOING,
                        source = Source.MOTTRUYEN
                    )
                ),
                genresList = listOf(
                    Genre(id = "1", name = "Tiên Hiệp", slug = "tien-hiep", count = 350, source = Source.TRUYENCV),
                    Genre(id = "2", name = "Huyền Huyễn", slug = "huyen-huyen", count = 480, source = Source.TRUYENCV)
                ),
                newUpdateNovels = listOf(
                    Novel(
                        id = "tcv-1",
                        rawId = 1L,
                        title = "Phàm Nhân Tu Tiên",
                        author = "Vong Ngữ",
                        thumbUrl = "",
                        description = "Mô tả truyện",
                        genres = listOf("Tiên Hiệp"),
                        totalChapters = 1500,
                        views = 200000,
                        status = NovelStatus.COMPLETED,
                        source = Source.TRUYENCV
                    )
                )
            ),
            onSelectSource = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {},
            onNavigateToSearch = {},
            onNavigateToDetail = {},
            onNavigateToGenre = {}
        )
    }
}
