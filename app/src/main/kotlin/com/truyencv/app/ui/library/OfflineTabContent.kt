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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.SourceBadge
import com.truyencv.app.ui.components.shimmerEffect

/**
 * Tab "Offline" hiển thị danh sách các truyện đã tải dữ liệu thành công về bộ nhớ trong SQLite.
 * Cho phép đọc hoàn toàn khi không có kết nối Internet và quản lý giải phóng bộ nhớ.
 */
@Composable
fun OfflineTabContent(
    offlineNovels: List<OfflineNovelItem>,
    onNovelClick: (String) -> Unit,
    onReadOffline: (String) -> Unit,
    onRemoveOffline: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var novelToDelete by remember { mutableStateOf<OfflineNovelItem?>(null) }

    if (novelToDelete != null) {
        val target = novelToDelete!!
        AlertDialog(
            onDismissRequest = { novelToDelete = null },
            title = {
                Text(text = "Xóa dữ liệu offline?")
            },
            text = {
                Text(
                    text = "Tất cả ${target.downloadedChaptersCount} chương tải offline của bộ truyện \"${target.novel.title}\" sẽ bị xóa khỏi bộ nhớ thiết bị."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveOffline(target.novel.id)
                        novelToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa bản tải")
                }
            },
            dismissButton = {
                TextButton(onClick = { novelToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (offlineNovels.isEmpty()) {
        EmptyStateView(
            modifier = modifier.fillMaxSize(),
            title = "Chưa có truyện offline",
            message = "Bạn chưa tải truyện nào để đọc ngoại tuyến. Hãy tải các chương truyện từ màn hình chi tiết để đọc khi không có mạng!",
            icon = Icons.Default.CloudDownload
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Truyện đã tải (${offlineNovels.size} bộ) • Đọc không cần mạng",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(
                items = offlineNovels,
                key = { it.novel.id }
            ) { item ->
                OfflineNovelCard(
                    item = item,
                    onClick = { onNovelClick(item.novel.id) },
                    onRead = { onReadOffline(item.novel.id) },
                    onDelete = { novelToDelete = item }
                )
            }
        }
    }
}

@Composable
private fun OfflineNovelCard(
    item: OfflineNovelItem,
    onClick: () -> Unit,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val novel = item.novel

    // Ưu tiên coverBase64 để hiển thị offline hoàn toàn
    val imageModel: Any? = remember(novel.thumbUrl, novel.coverBase64) {
        if (!novel.coverBase64.isNullOrBlank()) {
            try {
                android.util.Base64.decode(novel.coverBase64, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        } else if (novel.thumbUrl.isNotBlank()) {
            novel.thumbUrl
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

            // Thông tin truyện offline
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SourceBadge(source = novel.source, small = true)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${item.downloadedChaptersCount} chương đã tải",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = onRead,
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Đọc offline", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Xóa dữ liệu offline",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
