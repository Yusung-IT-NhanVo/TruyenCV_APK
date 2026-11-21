package com.truyencv.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.theme.SourceMotTruyenContainer
import com.truyencv.app.ui.theme.SourceMotTruyenOnContainer
import com.truyencv.app.ui.theme.SourceTruyenCvContainer
import com.truyencv.app.ui.theme.SourceTruyenCvOnContainer
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Nhóm Filter Chips cho phép chọn nguồn truyện: Tất cả | TruyenCV.io | MotTruyen
 */
@Composable
fun SourceChipGroup(
    selectedSource: Source?,
    onSourceSelected: (Source?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chip "Tất cả"
        val isAllSelected = selectedSource == null
        FilterChip(
            selected = isAllSelected,
            onClick = { onSourceSelected(null) },
            label = {
                Text(
                    text = "Tất cả nguồn",
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                )
            },
            leadingIcon = if (isAllSelected) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else null
        )

        // Chip "TruyenCV.io"
        val isTcvSelected = selectedSource == Source.TRUYENCV
        FilterChip(
            selected = isTcvSelected,
            onClick = { onSourceSelected(Source.TRUYENCV) },
            label = {
                Text(
                    text = Source.TRUYENCV.label,
                    fontWeight = if (isTcvSelected) FontWeight.Bold else FontWeight.Normal
                )
            },
            leadingIcon = if (isTcvSelected) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SourceTruyenCvContainer,
                selectedLabelColor = SourceTruyenCvOnContainer,
                selectedLeadingIconColor = SourceTruyenCvOnContainer
            )
        )

        // Chip "MotTruyen"
        val isMtSelected = selectedSource == Source.MOTTRUYEN
        FilterChip(
            selected = isMtSelected,
            onClick = { onSourceSelected(Source.MOTTRUYEN) },
            label = {
                Text(
                    text = Source.MOTTRUYEN.label,
                    fontWeight = if (isMtSelected) FontWeight.Bold else FontWeight.Normal
                )
            },
            leadingIcon = if (isMtSelected) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SourceMotTruyenContainer,
                selectedLabelColor = SourceMotTruyenOnContainer,
                selectedLeadingIconColor = SourceMotTruyenOnContainer
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SourceChipGroupPreview() {
    TruyenCVTheme {
        SourceChipGroup(
            selectedSource = Source.TRUYENCV,
            onSourceSelected = {}
        )
    }
}
