package com.truyencv.app

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.truyencv.app.ui.main.MainScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(NavMain)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<NavMain> {
                MainScreen(
                    onNavigateToDetail = { novelId -> backStack.add(NavNovelDetail(novelId)) },
                    onNavigateToSearch = { backStack.add(NavSearch) }
                )
            }
            entry<NavNovelDetail> { key ->
                // Placeholder for Phase 2 Detail Screen
            }
            entry<NavReader> { key ->
                // Placeholder for Phase 3 Reader Screen
            }
            entry<NavSearch> {
                // Placeholder for Phase 2 Search Screen
            }
        }
    )
}
