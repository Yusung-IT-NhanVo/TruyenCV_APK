package com.truyencv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/**
 * Card hiển thị truyện trong danh sách dạng lưới (Grid) hoặc carousel ngang.
 */
@Composable
fun NovelCard(
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Phần ảnh bìa tỷ lệ 2:3 với badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
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
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                )

                // Badges hiển thị góc trên ảnh
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    SourceBadge(source = novel.source, small = true)
                    StatusBadge(status = novel.status, small = true)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tên truyện (tối đa 2 dòng, cắt đuôi bằng ...)
            Text(
                text = novel.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tác giả
            Text(
                text = novel.author.ifBlank { "Đang cập nhật" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Số chương
            if (novel.totalChapters > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${novel.totalChapters} chương",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NovelCardPreview() {
    TruyenCVTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NovelCard(
                novel = Novel(
                    id = "tcv-1234",
                    rawId = 1234L,
                    title = "Vũ Luyện Điên Cuồng: Nghịch Thiên Chí Tôn",
                    author = "Mạc Mặc",
                    thumbUrl = "https://example.com/cover.jpg",
                    description = "Mô tả truyện ngắn gọn...",
                    totalChapters = 520,
                    views = 150200,
                    status = NovelStatus.ONGOING,
                    source = Source.TRUYENCV
                ),
                onClick = {},
                modifier = Modifier.width(140.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NovelCardDarkPreview() {
    TruyenCVTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            NovelCard(
                novel = Novel(
                    id = "mt-5678",
                    rawId = 5678L,
                    title = "Đại Quản Gia Là Ma Hoàng",
                    author = "Dạ Kiêu",
                    thumbUrl = "https://example.com/cover2.jpg",
                    description = "Ma hoàng Trác Nhất Phàm...",
                    totalChapters = 1314,
                    views = 890000,
                    status = NovelStatus.COMPLETED,
                    source = Source.MOTTRUYEN
                ),
                onClick = {},
                modifier = Modifier.width(140.dp)
            )
        }
    }
}
