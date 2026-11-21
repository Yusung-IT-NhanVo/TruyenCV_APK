package com.truyencv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.util.Locale

/**
 * Item hiển thị truyện dạng dòng trong danh sách dọc (Explore, Search, Ranking, Library).
 */
@Composable
fun NovelListItem(
    novel: Novel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rankingNumber: Int? = null
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Huy hiệu xếp hạng (nếu có)
            if (rankingNumber != null) {
                RankingBadge(rank = rankingNumber)
                Spacer(modifier = Modifier.width(10.dp))
            }

            // Ảnh bìa
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(112.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                                .shimmerEffect(RoundedCornerShape(8.dp))
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
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Cột thông tin truyện
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Tên truyện
                Text(
                    text = novel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tác giả
                Text(
                    text = novel.author.ifBlank { "Tác giả đang cập nhật" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Badges: Nguồn + Trạng thái + Thể loại
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SourceBadge(source = novel.source, small = true)
                    StatusBadge(status = novel.status, small = true)

                    if (novel.genres.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = novel.genres.first(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Thống kê: Số chương + Lượt xem
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (novel.totalChapters > 0) {
                        Text(
                            text = "${novel.totalChapters} chương",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (novel.views > 0) {
                        Text(
                            text = "${formatViews(novel.views)} lượt xem",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

/**
 * Huy hiệu thứ hạng số cho màn hình Bảng Xếp Hạng (BXH)
 */
@Composable
private fun RankingBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (rank) {
        1 -> Pair(Color(0xFFFFD700), Color(0xFF5D4037)) // Vàng
        2 -> Pair(Color(0xFFC0C0C0), Color(0xFF37474F)) // Bạc
        3 -> Pair(Color(0xFFCD7F32), Color(0xFFFFFFFF)) // Đồng
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Định dạng số lượt xem thân thiện (VD: 1.5K, 2.3M)
 */
fun formatViews(views: Long): String {
    return when {
        views >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", views / 1_000_000.0)
        views >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", views / 1_000.0)
        else -> views.toString()
    }
}

@Preview(showBackground = true)
@Composable
private fun NovelListItemPreview() {
    TruyenCVTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            NovelListItem(
                novel = Novel(
                    id = "tcv-123",
                    rawId = 123L,
                    title = "Nhất Niệm Vĩnh Hằng: Phong Bạo Tái Khởi",
                    author = "Nhĩ Căn",
                    thumbUrl = "https://example.com/cover.jpg",
                    description = "Bạch Tiểu Thuần một lòng hướng đạo...",
                    genres = listOf("Tiên Hiệp", "Hài Hước"),
                    totalChapters = 1314,
                    views = 4850000,
                    status = NovelStatus.COMPLETED,
                    source = Source.TRUYENCV
                ),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NovelListItemWithRankingPreview() {
    TruyenCVTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            NovelListItem(
                novel = Novel(
                    id = "mt-456",
                    rawId = 456L,
                    title = "Thế Giới Hoàn Mỹ: Độc Đoán Vạn Cổ",
                    author = "Thần Đồng",
                    thumbUrl = "https://example.com/cover.jpg",
                    description = "Một hạt bụi có thể lấp biển...",
                    genres = listOf("Huyền Huyễn"),
                    totalChapters = 2018,
                    views = 9850000,
                    status = NovelStatus.ONGOING,
                    source = Source.MOTTRUYEN
                ),
                onClick = {},
                rankingNumber = 1
            )
        }
    }
}
