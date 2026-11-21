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
    onNavigateToSearch: () -> Unit = {},
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (currentTab) {
                BottomNavTab.EXPLORE -> {
                    Text(
                        text = "Màn hình Khám Phá (Explore)\nSẽ triển khai ở Phase 2",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BottomNavTab.RANKING -> {
                    Text(
                        text = "Màn hình Bảng Xếp Hạng (Ranking)\nSẽ triển khai ở Phase 2",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BottomNavTab.LIBRARY -> {
                    Text(
                        text = "Màn hình Tủ Truyện (Library)\nSẽ triển khai ở Phase 2",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BottomNavTab.SETTINGS -> {
                    Text(
                        text = "Màn hình Cài Đặt (Settings)\nSẽ triển khai ở Phase 4",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
