package com.truyencv.app.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.repository.NovelRepository
import com.truyencv.app.ui.components.GenreGrid
import com.truyencv.app.ui.theme.TruyenCVTheme
import com.truyencv.app.util.GenreMappingTable
import kotlinx.coroutines.flow.firstOrNull

/**
 * Màn hình Danh Mục Thể Loại hiển thị lưới 3 cột đầy đủ các thể loại truyện (SRS §4.3, §5.1, §5.3).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(
    onBack: () -> Unit,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedGenreSlug: String? = null,
    novelRepository: NovelRepository? = null
) {
    var genres by remember {
        mutableStateOf(
            GenreMappingTable.getAllCanonicalGenres().map {
                Genre(
                    id = it.slug,
                    name = it.name,
                    slug = it.slug,
                    count = 0,
                    source = null
                )
            }
        )
    }

    // Tải số lượng truyện theo thể loại nếu có repository
    LaunchedEffect(novelRepository) {
        if (novelRepository != null) {
            try {
                val fetched = novelRepository.getGenres(null).firstOrNull()
                if (!fetched.isNullOrEmpty()) {
                    genres = fetched
                }
            } catch (_: Exception) {
                // Giữ danh sách canonical mặc định
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Thể Loại Truyện",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Chọn thể loại để khám phá các tác phẩm đặc sắc",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            GenreGrid(
                genres = genres,
                selectedGenreSlug = selectedGenreSlug,
                onGenreClick = { genre -> onGenreSelected(genre.slug) },
                columns = 3,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenreListScreenPreview() {
    TruyenCVTheme {
        GenreListScreen(
            onBack = {},
            onGenreSelected = {},
            selectedGenreSlug = "tien-hiep"
        )
    }
}
