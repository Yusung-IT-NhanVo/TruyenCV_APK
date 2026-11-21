package com.truyencv.app.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.truyencv.app.R
import com.truyencv.app.ui.explore.ExploreScreen
import com.truyencv.app.ui.library.LibraryScreen
import com.truyencv.app.ui.ranking.RankingScreen
import com.truyencv.app.ui.theme.TruyenCVTheme

enum class BottomNavTab(val titleRes: Int, val iconEmoji: String) {
    EXPLORE(R.string.tab_explore, "🔍"),
    RANKING(R.string.tab_ranking, "🏆"),
    LIBRARY(R.string.tab_library, "📚"),
    SETTINGS(R.string.tab_settings, "⚙️")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSearch: (String?) -> Unit = {},
    onNavigateToReader: (String, String) -> Unit = { _, _ -> },
    onNavigateToGenre: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val currentTab = BottomNavTab.entries[selectedTab]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(currentTab.titleRes),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                BottomNavTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Text(text = tab.iconEmoji, fontSize = 20.sp)
                        },
                        label = {
                            Text(text = stringResource(tab.titleRes))
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                BottomNavTab.EXPLORE -> {
                    ExploreScreen(
                        onNavigateToSearch = { onNavigateToSearch(null) },
                        onNavigateToDetail = onNavigateToDetail,
                        onNavigateToGenre = onNavigateToGenre,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BottomNavTab.RANKING -> {
                    RankingScreen(
                        onNavigateToDetail = onNavigateToDetail,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BottomNavTab.LIBRARY -> {
                    LibraryScreen(
                        onNavigateToDetail = onNavigateToDetail,
                        onNavigateToReader = onNavigateToReader,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BottomNavTab.SETTINGS -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Màn hình Cài Đặt (Settings)\nSẽ triển khai ở Phase 4",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TruyenCVTheme {
        MainScreen()
    }
}
