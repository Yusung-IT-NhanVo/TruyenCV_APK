package com.truyencv.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.theme.TruyenCVTheme
import kotlinx.coroutines.delay

/**
 * Banner Carousel tự động cuộn (Auto-Scroll) cho phần Truyện Hot / Nổi Bật.
 */
@Composable
fun HorizontalCarousel(
    novels: List<Novel>,
    onNovelClick: (Novel) -> Unit,
    modifier: Modifier = Modifier,
    autoScrollDelayMs: Long = 4000L
) {
    if (novels.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { novels.size })

    // Auto-scroll effect sau mỗi khoảng thời gian định kỳ
    LaunchedEffect(pagerState, novels.size, autoScrollDelayMs) {
        if (novels.size > 1) {
            while (true) {
                delay(autoScrollDelayMs)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % novels.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            val novel = novels[page]
            CarouselBannerCard(
                novel = novel,
                onClick = { onNovelClick(novel) }
            )
        }

        // Indicators chấm tròn hiển thị trang hiện tại
        if (novels.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(novels.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 20.dp else 6.dp,
                        label = "indicatorWidth"
                    )

                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                }
                            )
                    )
                }
            }
        }
    }
}

/**
 * Item Card hiển thị bên trong Banner Carousel với Scrim tối mờ và thông tin nổi bật.
 */
@Composable
private fun CarouselBannerCard(
    novel: Novel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageModel: Any? = remember(novel.thumbUrl, novel.coverBase64) {
        if (novel.thumbUrl.isNotBlank()) {
            novel.thumbUrl
        } else if (!novel.coverBase64.isNullOrBlank()) {
            try {
                android.util.Base64.decode(novel.coverBase64, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ảnh bìa nền
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
                            .shimmerEffect(RoundedCornerShape(16.dp))
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
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )

            // Lớp Gradient Scrim chuyển dần sang tối ở đáy để tôn text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = 0f
                        )
                    )
            )

            // Badges ở góc trên
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SourceBadge(source = novel.source)
                StatusBadge(status = novel.status)
            }

            // Nội dung tiêu đề, tác giả và thể loại ở đáy
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = novel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = novel.author.ifBlank { "Tác giả đang cập nhật" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (novel.totalChapters > 0) {
                        Text(
                            text = "•",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${novel.totalChapters} chương",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Thể loại (hiển thị tối đa 2 thể loại đầu tiên)
                if (novel.genres.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        novel.genres.take(2).forEach { genreName ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = genreName,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
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
 * Hàng cuộn ngang hiển thị các NovelCard (dùng cho mục Truyện Đề Cử, Truyện Mới, v.v.)
 */
@Composable
fun NovelHorizontalRow(
    title: String,
    novels: List<Novel>,
    onNovelClick: (Novel) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null
) {
    if (novels.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        // Tiêu đề mục & Nút "Xem tất cả"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (onSeeAllClick != null) {
                TextButton(onClick = onSeeAllClick) {
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Danh sách cuộn ngang
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(novels, key = { it.id }) { novel ->
                NovelCard(
                    novel = novel,
                    onClick = { onNovelClick(novel) },
                    modifier = Modifier.width(136.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalCarouselPreview() {
    TruyenCVTheme {
        Box(modifier = Modifier.padding(vertical = 12.dp)) {
            HorizontalCarousel(
                novels = listOf(
                    Novel(
                        id = "tcv-1",
                        rawId = 1L,
                        title = "Phàm Nhân Tu Tiên Chi Tiên Giới Thiên",
                        author = "Vong Ngữ",
                        thumbUrl = "https://example.com/cover1.jpg",
                        description = "Hàn Lập phi thăng tiên giới...",
                        genres = listOf("Tiên Hiệp", "Tu Chân"),
                        totalChapters = 1395,
                        views = 28000000,
                        status = NovelStatus.COMPLETED,
                        source = Source.TRUYENCV
                    ),
                    Novel(
                        id = "mt-2",
                        rawId = 2L,
                        title = "Đấu La Đại Lục V: Trùng Sinh Đường Tam",
                        author = "Đường Gia Tam Thiếu",
                        thumbUrl = "https://example.com/cover2.jpg",
                        description = "Đường Tam vì tìm kiếm Tiểu Vũ...",
                        genres = listOf("Huyền Huyễn", "Dị Giới"),
                        totalChapters = 1180,
                        views = 15000000,
                        status = NovelStatus.ONGOING,
                        source = Source.MOTTRUYEN
                    )
                ),
                onNovelClick = {}
            )
        }
    }
}
