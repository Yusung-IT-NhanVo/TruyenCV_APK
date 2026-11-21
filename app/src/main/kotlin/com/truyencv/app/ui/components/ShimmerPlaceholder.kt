package com.truyencv.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truyencv.app.ui.theme.ShimmerBaseDark
import com.truyencv.app.ui.theme.ShimmerBaseLight
import com.truyencv.app.ui.theme.ShimmerHighlightDark
import com.truyencv.app.ui.theme.ShimmerHighlightLight
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Hiệu ứng Shimmer Skeleton lấp lánh khi tải dữ liệu
 */
@Composable
fun Modifier.shimmerEffect(shape: Shape = RoundedCornerShape(8.dp)): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) ShimmerBaseDark else ShimmerBaseLight
    val highlightColor = if (isDark) ShimmerHighlightDark else ShimmerHighlightLight

    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(translateAnim - 400f, translateAnim - 400f),
        end = Offset(translateAnim, translateAnim)
    )

    return this
        .clip(shape)
        .background(brush)
}

/**
 * Skeleton placeholder cho NovelCard (dạng lưới / carousel)
 */
@Composable
fun NovelCardShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(136.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ảnh bìa 2:3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .shimmerEffect(RoundedCornerShape(8.dp))
            )
            // Tên truyện dòng 1
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
            // Tên truyện dòng 2
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
            // Tác giả
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(11.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Skeleton placeholder cho NovelListItem (danh sách dọc)
 */
@Composable
fun NovelListItemShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh bìa
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(108.dp)
                    .shimmerEffect(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tên truyện
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(16.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
                // Tác giả
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
                // Badges row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(20.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(20.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp))
                    )
                }
                // Stats row
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(12.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Skeleton placeholder cho Banner carousel
 */
@Composable
fun BannerShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shimmerEffect(RoundedCornerShape(16.dp))
    )
}

@Preview(showBackground = true)
@Composable
private fun ShimmerPreview() {
    TruyenCVTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BannerShimmer()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NovelCardShimmer()
                NovelCardShimmer()
            }
            NovelListItemShimmer()
        }
    }
}
