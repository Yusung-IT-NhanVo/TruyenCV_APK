package com.truyencv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.theme.SourceMotTruyenContainer
import com.truyencv.app.ui.theme.SourceMotTruyenOnContainer
import com.truyencv.app.ui.theme.SourceTruyenCvContainer
import com.truyencv.app.ui.theme.SourceTruyenCvOnContainer
import com.truyencv.app.ui.theme.StatusCompletedContainer
import com.truyencv.app.ui.theme.StatusCompletedOnContainer
import com.truyencv.app.ui.theme.StatusOngoingContainer
import com.truyencv.app.ui.theme.StatusOngoingOnContainer
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Badge nguồn truyện (TruyenCV xanh dương / MotTruyen cam)
 */
@Composable
fun SourceBadge(
    source: Source,
    modifier: Modifier = Modifier,
    small: Boolean = false
) {
    val (bgColor, textColor, label) = when (source) {
        Source.TRUYENCV -> Triple(
            SourceTruyenCvContainer,
            SourceTruyenCvOnContainer,
            "TruyenCV"
        )
        Source.MOTTRUYEN -> Triple(
            SourceMotTruyenContainer,
            SourceMotTruyenOnContainer,
            "MotTruyen"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (small) 4.dp else 6.dp))
            .background(bgColor)
            .padding(
                horizontal = if (small) 5.dp else 8.dp,
                vertical = if (small) 2.dp else 3.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (small) 9.sp else 11.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = if (small) 12.sp else 14.sp
        )
    }
}

/**
 * Badge trạng thái truyện (Đang ra / Hoàn thành)
 */
@Composable
fun StatusBadge(
    status: NovelStatus,
    modifier: Modifier = Modifier,
    small: Boolean = false
) {
    val (bgColor, textColor, label) = when (status) {
        NovelStatus.ONGOING -> Triple(
            StatusOngoingContainer,
            StatusOngoingOnContainer,
            status.label
        )
        NovelStatus.COMPLETED -> Triple(
            StatusCompletedContainer,
            StatusCompletedOnContainer,
            status.label
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (small) 4.dp else 6.dp))
            .background(bgColor)
            .padding(
                horizontal = if (small) 5.dp else 8.dp,
                vertical = if (small) 2.dp else 3.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (small) 9.sp else 11.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = if (small) 12.sp else 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgesPreview() {
    TruyenCVTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            SourceBadge(source = Source.TRUYENCV)
            Box(modifier = Modifier.padding(horizontal = 4.dp))
            SourceBadge(source = Source.MOTTRUYEN)
            Box(modifier = Modifier.padding(horizontal = 4.dp))
            StatusBadge(status = NovelStatus.ONGOING)
            Box(modifier = Modifier.padding(horizontal = 4.dp))
            StatusBadge(status = NovelStatus.COMPLETED)
        }
    }
}
