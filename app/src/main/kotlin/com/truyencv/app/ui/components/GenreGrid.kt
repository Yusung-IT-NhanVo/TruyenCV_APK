package com.truyencv.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Source
import com.truyencv.app.ui.theme.TruyenCVTheme

/**
 * Grid hiển thị thể loại truyện (Dùng cho Màn hình Danh mục thể loại độc lập)
 */
@Composable
fun GenreGrid(
    genres: List<Genre>,
    selectedGenreSlug: String?,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres, key = { it.slug }) { genre ->
            GenreChip(
                name = genre.name,
                selected = genre.slug == selectedGenreSlug,
                count = genre.count,
                onClick = { onGenreClick(genre) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * FlowRow hiển thị thể loại truyện khi nhúng trong trang cuộn dọc (như Explore tab hoặc Search dialog)
 * Tránh lỗi xung đột cuộn lồng nhau (Nested Scroll)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreFlowRow(
    genres: List<Genre>,
    selectedGenreSlug: String?,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            GenreChip(
                name = genre.name,
                selected = genre.slug == selectedGenreSlug,
                count = genre.count,
                onClick = { onGenreClick(genre) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenreFlowRowPreview() {
    TruyenCVTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GenreFlowRow(
                genres = listOf(
                    Genre(id = "1", name = "Tiên Hiệp", slug = "tien-hiep", count = 350, source = Source.TRUYENCV),
                    Genre(id = "2", name = "Huyền Huyễn", slug = "huyen-huyen", count = 480, source = Source.TRUYENCV),
                    Genre(id = "3", name = "Đô Thị", slug = "do-thi", count = 210, source = Source.TRUYENCV),
                    Genre(id = "4", name = "Khoa Huyễn", slug = "khoa-huyen", count = 98, source = Source.TRUYENCV),
                    Genre(id = "5", name = "Kiếm Hiệp", slug = "kiem-hiep", count = 175, source = Source.TRUYENCV),
                    Genre(id = "6", name = "Ngôn Tình", slug = "ngon-tinh", count = 310, source = Source.TRUYENCV)
                ),
                selectedGenreSlug = "tien-hiep",
                onGenreClick = {}
            )
        }
    }
}
