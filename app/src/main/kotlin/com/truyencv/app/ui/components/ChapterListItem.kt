package com.truyencv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Row hiển thị một chương trong danh sách chương (Màn hình Chi tiết truyện & Drawer chuyển chương).
 */
@Composable
fun ChapterListItem(
    chapter: Chapter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRead: Boolean = false,
    isCurrent: Boolean = false
) {
    val backgroundColor = when {
        isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surface
    }

    val titleColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isRead -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val titleWeight = when {
        isCurrent -> FontWeight.Bold
        isRead -> FontWeight.Normal
        else -> FontWeight.Medium
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Số thứ tự chương
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${chapter.chapterOrder}",
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimary
                    } else if (isRead) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tiêu đề chương
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = chapter.title.ifBlank { chapter.rawTitle },
                    style = MaterialTheme.typography.bodyMedium,
                    color = titleColor,
                    fontWeight = titleWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isCurrent) {
                    Text(
                        text = "Đang đọc",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Chỉ báo đã tải offline (nếu có)
            if (chapter.isDownloaded) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Đã tải offline",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Offline",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterListItemPreview() {
    TruyenCVTheme {
        Column {
            ChapterListItem(
                chapter = Chapter(
                    id = "tcv-1",
                    rawId = 1L,
                    novelId = "tcv-100",
                    chapterOrder = 1,
                    title = "Chương 1: Hắc Thiết Chi Bảo",
                    rawTitle = "Chương 1: Hắc Thiết Chi Bảo",
                    source = Source.TRUYENCV,
                    isDownloaded = true
                ),
                onClick = {},
                isRead = true
            )
            ChapterListItem(
                chapter = Chapter(
                    id = "tcv-2",
                    rawId = 2L,
                    novelId = "tcv-100",
                    chapterOrder = 2,
                    title = "Chương 2: Thần Bí Thạch Châu",
                    rawTitle = "Chương 2: Thần Bí Thạch Châu",
                    source = Source.TRUYENCV,
                    isDownloaded = false
                ),
                onClick = {},
                isCurrent = true
            )
            ChapterListItem(
                chapter = Chapter(
                    id = "tcv-3",
                    rawId = 3L,
                    novelId = "tcv-100",
                    chapterOrder = 3,
                    title = "Chương 3: Khổ Tu Dưới Trăng",
                    rawTitle = "Chương 3: Khổ Tu Dưới Trăng",
                    source = Source.TRUYENCV,
                    isDownloaded = false
                ),
                onClick = {},
                isRead = false
            )
        }
    }
}
