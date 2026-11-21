package com.truyencv.app.ui.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.ErrorStateView
import com.truyencv.app.ui.components.NovelListItem
import com.truyencv.app.ui.components.NovelListItemShimmer
import com.truyencv.app.ui.components.SourceChipGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = run {
        val container = LocalAppContainer.current
        viewModel(factory = RankingViewModel.provideFactory(container.novelRepository))
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // TabRow 4 kỳ xếp hạng: Ngày | Tuần | Tháng | Tổng
        PrimaryTabRow(
            selectedTabIndex = RankingPeriod.entries.indexOf(uiState.selectedPeriod),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            RankingPeriod.entries.forEach { period ->
                Tab(
                    selected = uiState.selectedPeriod == period,
                    onClick = { viewModel.selectPeriod(period) },
                    text = {
                        Text(
                            text = period.label,
                            fontWeight = if (uiState.selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Bộ chọn lọc theo nguồn (Tất cả | TruyenCV.io | MotTruyen)
        SourceChipGroup(
            selectedSource = uiState.selectedSource,
            onSourceSelected = { viewModel.selectSource(it) },
            modifier = Modifier.fillMaxWidth()
        )

        // Vùng hiển thị nội dung dạng Pull-to-Refresh
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                // Trạng thái đang tải lần đầu: hiển thị shimmer skeleton
                uiState.isLoading && uiState.rankingNovels.isEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(7) {
                            NovelListItemShimmer()
                        }
                    }
                }

                // Trạng thái xảy ra lỗi và chưa có dữ liệu trong danh sách
                uiState.error != null && uiState.rankingNovels.isEmpty() -> {
                    ErrorStateView(
                        title = "Không thể tải bảng xếp hạng",
                        message = uiState.error ?: "Đã xảy ra lỗi kết nối.",
                        retryLabel = "Thử lại",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Trạng thái danh sách rỗng sau khi tải xong
                !uiState.isLoading && uiState.rankingNovels.isEmpty() -> {
                    EmptyStateView(
                        title = "Chưa có dữ liệu xếp hạng",
                        message = "Hiện chưa có truyện nào được xếp hạng trong kỳ ${uiState.selectedPeriod.label.lowercase()}.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Danh sách truyện xếp hạng
                else -> {
                    val listState = rememberLazyListState()

                    // Phát hiện khi cuộn gần cuối danh sách để kích hoạt loadMore
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val totalItems = listState.layoutInfo.totalItemsCount
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            totalItems > 0 && lastVisible >= totalItems - 3
                        }
                    }

                    LaunchedEffect(shouldLoadMore.value) {
                        if (shouldLoadMore.value && uiState.canLoadMore && !uiState.isLoading && !uiState.isLoadingMore) {
                            viewModel.loadMore()
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.rankingNovels,
                            key = { _, novel -> novel.id }
                        ) { index, novel ->
                            NovelListItem(
                                novel = novel,
                                onClick = { onNavigateToDetail(novel.id) },
                                rankingNumber = index + 1
                            )
                        }

                        // Chỉ báo đang tải thêm trang tiếp theo ở cuối danh sách
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
