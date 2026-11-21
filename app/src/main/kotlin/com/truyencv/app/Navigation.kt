package com.truyencv.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.truyencv.app.ui.main.MainScreen
import com.truyencv.app.ui.navigation.ChapterListPlaceholderScreen
import com.truyencv.app.ui.navigation.GenreListPlaceholderScreen
import com.truyencv.app.ui.navigation.LocalNavigationActions
import com.truyencv.app.ui.navigation.NovelDetailPlaceholderScreen
import com.truyencv.app.ui.navigation.ReaderPlaceholderScreen
import com.truyencv.app.ui.navigation.SearchPlaceholderScreen
import com.truyencv.app.ui.navigation.rememberNavigationActions

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(NavMain)
    val navActions = rememberNavigationActions(backStack)

    CompositionLocalProvider(LocalNavigationActions provides navActions) {
        NavDisplay(
            backStack = backStack,
            onBack = { navActions.navigateBack() },
            entryProvider = entryProvider {
                entry<NavMain> {
                    MainScreen(
                        onNavigateToDetail = { novelId -> navActions.navigateToDetail(novelId) },
                        onNavigateToSearch = { navActions.navigateToSearch() }
                    )
                }
                entry<NavNovelDetail> { key ->
                    NovelDetailPlaceholderScreen(
                        novelId = key.novelId,
                        onBack = { navActions.navigateBack() },
                        onReadChapter = { chapterId -> navActions.navigateToReader(key.novelId, chapterId) },
                        onViewChapterList = { navActions.navigateToChapterList(key.novelId) }
                    )
                }
                entry<NavReader> { key ->
                    ReaderPlaceholderScreen(
                        novelId = key.novelId,
                        chapterId = key.chapterId,
                        onBack = { navActions.navigateBack() },
                        onViewChapterList = { navActions.navigateToChapterList(key.novelId) }
                    )
                }
                entry<NavSearch> { key ->
                    SearchPlaceholderScreen(
                        initialGenre = key.initialGenre,
                        onBack = { navActions.navigateBack() },
                        onSelectGenre = { navActions.navigateToGenreList() },
                        onSelectNovel = { novelId -> navActions.navigateToDetail(novelId) }
                    )
                }
                entry<NavGenreList> {
                    GenreListPlaceholderScreen(
                        onBack = { navActions.navigateBack() },
                        onGenreSelected = { genreSlug -> navActions.navigateToSearch(genreSlug) }
                    )
                }
                entry<NavChapterList> { key ->
                    ChapterListPlaceholderScreen(
                        novelId = key.novelId,
                        onBack = { navActions.navigateBack() },
                        onChapterSelected = { chapterId -> navActions.navigateToReader(key.novelId, chapterId) }
                    )
                }
            }
        )
    }
}
