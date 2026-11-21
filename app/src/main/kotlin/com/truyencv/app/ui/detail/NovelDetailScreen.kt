package com.truyencv.app.ui.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.ui.components.ChapterListItem
import com.truyencv.app.ui.components.ErrorStateView
import com.truyencv.app.ui.components.GenreChip
import com.truyencv.app.ui.components.SourceBadge
import com.truyencv.app.ui.components.StatusBadge
import com.truyencv.app.ui.components.formatViews
import com.truyencv.app.ui.components.shimmerEffect
import com.truyencv.app.util.TextSanitizer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NovelDetailScreen(
    novelId: String,
    onBack: () -> Unit,
    onNavigateToReader: (novelId: String, chapterId: String) -> Unit,
    onNavigateToFullChapterList: (novelId: String) -> Unit,
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.downloadMessage) {
        uiState.downloadMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.novel?.title ?: "Chi Tiết Truyện",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
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
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Default.BookmarkBorder
                            },
                            contentDescription = if (uiState.isBookmarked) "Bỏ theo dõi" else "Theo dõi",
                            tint = if (uiState.isBookmarked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            // Skeleton Shimmer khi đang tải thông tin truyện
            uiState.isLoadingNovel && uiState.novel == null -> {
                NovelDetailShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // Giao diện báo lỗi
            uiState.error != null && uiState.novel == null -> {
                ErrorStateView(
                    title = "Không thể tải chi tiết truyện",
                    message = uiState.error ?: "Đã xảy ra sự cố khi kết nối đến máy chủ.",
                    retryLabel = "Thử lại",
                    onRetry = { viewModel.loadNovelDetail() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // Hiển thị nội dung chi tiết truyện
            uiState.novel != null -> {
                val novel = uiState.novel!!
                var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

                val targetChapterId = remember(uiState.lastProgress, uiState.chapters) {
                    uiState.lastProgress?.chapterId
                        ?: uiState.chapters.minByOrNull { it.chapterOrder }?.id
                        ?: ""
                }

                val readButtonText = if (uiState.lastProgress != null) {
                    "Đọc tiếp (C.${uiState.lastProgress?.chapterOrder})"
                } else {
                    "Đọc từ đầu"
                }

                // Lấy tối đa 20 chương để hiển thị preview
                val previewChapters = remember(uiState.displayChapters) {
                    uiState.displayChapters.take(20)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Header: Ảnh bìa, Tên truyện, Tác giả, Badges, Thể loại, Thống kê
                    item {
                        NovelHeaderSection(novel = novel)
                    }

                    // 2. Thanh hành động chính: Đọc tiếp/từ đầu, Theo dõi, Tải xuống
                    item {
                        NovelActionsSection(
                            readButtonText = readButtonText,
                            isBookmarked = uiState.isBookmarked,
                            isDownloading = uiState.isDownloading,
                            onReadClick = { onNavigateToReader(novelId, targetChapterId) },
                            onBookmarkClick = { viewModel.toggleBookmark() },
                            onDownloadClick = { viewModel.downloadAllChapters() }
                        )
                    }

                    // 3. Phần Giới thiệu truyện: Expandable Text (strip HTML)
                    item {
                        NovelDescriptionSection(
                            description = novel.description,
                            isExpanded = isDescriptionExpanded,
                            onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded }
                        )
                    }

                    // 4. Header Danh Sách Chương
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Danh sách chương (${novel.totalChapters.coerceAtLeast(uiState.chapters.size)} chương)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Nút đảo chiều ASC ↔ DESC
                                TextButton(
                                    onClick = { viewModel.toggleChapterSort() },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (uiState.isSortAsc) "Cũ nhất" else "Mới nhất",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                // Nút Xem tất cả
                                TextButton(
                                    onClick = { onNavigateToFullChapterList(novelId) },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "Xem tất cả",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 5. Danh sách chương preview (10 - 20 chương)
                    if (uiState.isLoadingChapters && uiState.chapters.isEmpty()) {
                        items(5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .shimmerEffect(RoundedCornerShape(8.dp))
                            )
                        }
                    } else {
                        items(
                            items = previewChapters,
                            key = { it.id }
                        ) { chapter ->
                            val isCurrent = chapter.id == uiState.lastProgress?.chapterId
                            val isRead = uiState.lastProgress != null && chapter.chapterOrder <= uiState.lastProgress!!.chapterOrder

                            ChapterListItem(
                                chapter = chapter,
                                isRead = isRead,
                                isCurrent = isCurrent,
                                onClick = { onNavigateToReader(novelId, chapter.id) },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            )
                        }

                        // Nút xem toàn bộ nếu truyện có nhiều hơn preview
                        if (uiState.chapters.size > 20) {
                            item {
                                OutlinedButton(
                                    onClick = { onNavigateToFullChapterList(novelId) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Xem toàn bộ ${uiState.chapters.size} chương")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Phần thông tin Header của truyện
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NovelHeaderSection(novel: Novel) {
    val context = LocalContext.current
    val imageModel: Any? = remember(novel.thumbUrl, novel.coverBase64) {
        val sanitizedUrl = TextSanitizer.sanitizeCoverUrl(novel.thumbUrl)
        if (sanitizedUrl.isNotBlank() && sanitizedUrl != TextSanitizer.DEFAULT_COVER_URL) {
            sanitizedUrl
        } else if (!novel.coverBase64.isNullOrBlank()) {
            try {
                android.util.Base64.decode(novel.coverBase64, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                sanitizedUrl
            }
        } else {
            sanitizedUrl
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Ảnh bìa truyện
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = novel.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmerEffect()
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }

            // Cột thông tin: Tên, Tác giả, Badges, Lượt xem
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = novel.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )

                Text(
                    text = "Tác giả: ${novel.author.ifBlank { "Đang cập nhật" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Badges nguồn & trạng thái
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SourceBadge(source = novel.source)
                    StatusBadge(status = novel.status)
                }

                // Thống kê số chương & lượt xem
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${novel.totalChapters} chương",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (novel.views > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${formatViews(novel.views)} lượt xem",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Thể loại truyện (Chips)
        if (novel.genres.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                novel.genres.forEach { genreName ->
                    GenreChip(
                        name = genreName,
                        selected = false,
                        onClick = {}
                    )
                }
            }
        }
    }
}

/**
 * Thanh nút bấm hành động chính (Đọc, Theo dõi, Tải về)
 */
@Composable
private fun NovelActionsSection(
    readButtonText: String,
    isBookmarked: Boolean,
    isDownloading: Boolean,
    onReadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Đọc
        Button(
            onClick = onReadClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = readButtonText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Nút Theo dõi
        OutlinedButton(
            onClick = onBookmarkClick,
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isBookmarked) "Đã lưu" else "Theo dõi",
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Nút Tải truyện offline
        OutlinedButton(
            onClick = onDownloadClick,
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isDownloading
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Tải về",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Phần Mô tả / Giới thiệu truyện có thể mở rộng / thu gọn
 */
@Composable
private fun NovelDescriptionSection(
    description: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val cleanDescription = remember(description) {
        TextSanitizer.cleanHtml(description).ifBlank { "Nội dung truyện đang được cập nhật." }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .animateContentSize()
        ) {
            Text(
                text = "Giới thiệu truyện",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cleanDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )

            if (cleanDescription.length > 150) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isExpanded) "Thu gọn ▲" else "Xem thêm ▼",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onToggleExpand)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Placeholder shimmer khi chi tiết truyện đang tải
 */
@Composable
private fun NovelDetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
                    .shimmerEffect(RoundedCornerShape(12.dp))
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(24.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(20.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shimmerEffect(RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .shimmerEffect(RoundedCornerShape(12.dp))
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .shimmerEffect(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
