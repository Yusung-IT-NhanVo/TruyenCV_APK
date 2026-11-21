package com.truyencv.app.ui.library

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.SourceBadge
import com.truyencv.app.ui.components.StatusBadge
import com.truyencv.app.ui.components.shimmerEffect

/**
 * Tab "Theo dõi" hiển thị danh sách truyện đã lưu bookmark trong tủ truyện cá nhân.
 */
@Composable
fun FollowTabContent(
    novels: List<Novel>,
    onNovelClick: (String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (novels.isEmpty()) {
        EmptyStateView(
            modifier = modifier.fillMaxSize(),
            title = "Tủ truyện trống",
            message = "Hãy theo dõi những bộ truyện yêu thích để nhận thông báo và đọc nhanh nhất!",
            icon = Icons.Default.BookmarkBorder
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = novels,
                key = { it.id }
            ) { novel ->
                FollowedNovelCard(
                    novel = novel,
                    onClick = { onNovelClick(novel.id) },
                    onRemove = { onRemoveBookmark(novel.id) }
                )
            }
        }
    }
}

@Composable
private fun FollowedNovelCard(
    novel: Novel,
    onClick: () -> Unit,
    onRemove: () -> Unit,
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh bìa truyện
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

            // Nội dung thông tin
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            ) {
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

                Text(
                    text = novel.author.ifBlank { "Tác giả đang cập nhật" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Badges nguồn & trạng thái
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SourceBadge(source = novel.source, small = true)
                    StatusBadge(status = novel.status, small = true)
                }

                if (novel.totalChapters > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${novel.totalChapters} chương",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Nút Bỏ theo dõi
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkRemove,
                    contentDescription = "Bỏ theo dõi",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}
