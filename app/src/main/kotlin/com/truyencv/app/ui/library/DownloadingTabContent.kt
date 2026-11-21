package com.truyencv.app.ui.library

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.truyencv.app.data.local.db.DownloadTaskEntity
import com.truyencv.app.ui.components.EmptyStateView
import com.truyencv.app.ui.components.shimmerEffect

/**
 * Tab "Đang tải" hiển thị hàng đợi và tiến trình tải truyện offline theo thời gian thực.
 * Cung cấp các thao tác Tạm dừng (Pause), Tiếp tục (Resume) và Hủy tác vụ (Cancel).
 */
@Composable
fun DownloadingTabContent(
    downloadTasks: List<DownloadTaskEntity>,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToCancel by remember { mutableStateOf<DownloadTaskEntity?>(null) }

    if (taskToCancel != null) {
        val target = taskToCancel!!
        AlertDialog(
            onDismissRequest = { taskToCancel = null },
            title = {
                Text(text = "Hủy tác vụ tải?")
            },
            text = {
                Text(text = "Bạn có chắc chắn muốn hủy tải truyện \"${target.novelTitle}\"? Các chương đã tải dở sẽ được dọn dẹp.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancel(target.novelId)
                        taskToCancel = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hủy tải")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToCancel = null }) {
                    Text("Đóng")
                }
            }
        )
    }

    if (downloadTasks.isEmpty()) {
        EmptyStateView(
            modifier = modifier.fillMaxSize(),
            title = "Không có tác vụ tải",
            message = "Hiện tại không có truyện nào đang tải hoặc trong hàng đợi.",
            icon = Icons.Default.DownloadDone
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Hàng đợi tải (${downloadTasks.size} tác vụ)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(
                items = downloadTasks,
                key = { it.novelId }
            ) { task ->
                DownloadTaskCard(
                    task = task,
                    onPause = { onPause(task.novelId) },
                    onResume = { onResume(task.novelId) },
                    onCancel = { taskToCancel = task }
                )
            }
        }
    }
}

@Composable
private fun DownloadTaskCard(
    task: DownloadTaskEntity,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progress = if (task.totalChapters > 0) {
        (task.downloadedChapters.toFloat() / task.totalChapters).coerceIn(0f, 1f)
    } else {
        0f
    }
    val percent = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ảnh bìa thu nhỏ
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(86.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(task.thumbUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = task.novelTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerEffect(RoundedCornerShape(6.dp))
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
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Thông tin tiến trình tải
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.novelTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    DownloadStatusBadge(status = task.status)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Đã tải: ${task.downloadedChapters} / ${task.totalChapters} chương ($percent%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Nhóm nút điều khiển Tạm dừng / Tiếp tục / Hủy
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.status == "DOWNLOADING") {
                        OutlinedIconButton(
                            onClick = onPause,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Tạm dừng",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        OutlinedIconButton(
                            onClick = onResume,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Tiếp tục",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hủy tác vụ",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thanh tiến trình
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when (task.status) {
                    "PAUSED" -> MaterialTheme.colorScheme.tertiary
                    "FAILED" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun DownloadStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor) = when (status) {
        "DOWNLOADING" -> Triple("Đang tải...", Color(0xFFE3F2FD), Color(0xFF1976D2))
        "PAUSED" -> Triple("Đã tạm dừng", Color(0xFFFFF3E0), Color(0xFFE65100))
        "QUEUED" -> Triple("Đang chờ...", Color(0xFFEDE7F6), Color(0xFF5E35B1))
        "FAILED" -> Triple("Tải lỗi", Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Triple(status, Color(0xFFECEFF1), Color(0xFF455A64))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
