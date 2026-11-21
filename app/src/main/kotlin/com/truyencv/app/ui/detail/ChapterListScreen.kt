package com.truyencv.app.ui.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.ui.components.ChapterListItem
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.SearchBarField
import com.truyencv.app.ui.components.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListScreen(
    novelId: String,
    onBack: () -> Unit,
    onNavigateToReader: (novelId: String, chapterId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NovelDetailViewModel = run {
        val container = LocalAppContainer.current
        viewModel(
            key = "NovelDetail_$novelId",
            factory = NovelDetailViewModel.provideFactory(
                novelId = novelId,
                novelRepository = container.novelRepository,
                chapterRepository = container.chapterRepository,
                bookmarkRepository = container.bookmarkRepository,
                historyRepository = container.historyRepository
            )
        )
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Lọc danh sách chương theo từ khóa tìm kiếm (số chương hoặc tên chương)
    val filteredChapters = remember(uiState.displayChapters, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.displayChapters
        } else {
            val query = searchQuery.trim().lowercase()
            uiState.displayChapters.filter { chapter ->
                chapter.chapterOrder.toString() == query ||
                chapter.title.lowercase().contains(query) ||
                chapter.rawTitle.lowercase().contains(query)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Danh sách chương",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.novel?.let { novel ->
                            Text(
                                text = novel.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    // Nút chuyển đổi chiều sắp xếp ASC ↔ DESC
                    IconButton(onClick = { viewModel.toggleChapterSort() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = if (uiState.isSortAsc) "Sắp xếp mới nhất" else "Sắp xếp cũ nhất",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Thanh tìm kiếm chương theo số hoặc tên
            SearchBarField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                placeholder = "Tìm số chương (VD: 1, 10) hoặc tên chương...",
                onClear = { searchQuery = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Thanh tóm tắt số lượng chương hiển thị
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng: ${filteredChapters.size} chương",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (uiState.isSortAsc) "Thứ tự: Cũ → Mới" else "Thứ tự: Mới → Cũ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            when {
                // Đang tải dữ liệu chương
                uiState.isLoadingChapters && uiState.chapters.isEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(12) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .shimmerEffect(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }

                // Không tìm thấy chương khớp từ khóa
                filteredChapters.isEmpty() && searchQuery.isNotBlank() -> {
                    EmptyStateView(
                        title = "Không tìm thấy chương",
                        message = "Không có chương nào khớp với từ khóa \"$searchQuery\". Hãy thử nhập số chương khác.",
                        actionLabel = "Xóa tìm kiếm",
                        onActionClick = { searchQuery = "" },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Danh sách chương rỗng
                filteredChapters.isEmpty() -> {
                    EmptyStateView(
                        title = "Danh sách chương trống",
                        message = "Hiện chưa có chương nào được cập nhật cho truyện này.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Hiển thị danh sách chương
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = filteredChapters,
                            key = { it.id }
                        ) { chapter ->
                            val isCurrent = chapter.id == uiState.lastProgress?.chapterId
                            val isRead = uiState.lastProgress != null && chapter.chapterOrder <= uiState.lastProgress!!.chapterOrder

                            ChapterListItem(
                                chapter = chapter,
                                isRead = isRead,
                                isCurrent = isCurrent,
                                onClick = { onNavigateToReader(novelId, chapter.id) }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
