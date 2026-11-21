package com.truyencv.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.truyencv.app.ui.detail.ChapterListScreen
import com.truyencv.app.ui.detail.NovelDetailScreen
import com.truyencv.app.ui.main.MainScreen
import com.truyencv.app.ui.navigation.LocalNavigationActions
import com.truyencv.app.ui.navigation.rememberNavigationActions
import com.truyencv.app.ui.reader.ReaderScreen
import com.truyencv.app.ui.search.GenreListScreen
import com.truyencv.app.ui.search.SearchScreen

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
                        onNavigateToSearch = { initialGenre -> navActions.navigateToSearch(initialGenre) },
                        onNavigateToReader = { novelId, chapterId -> navActions.navigateToReader(novelId, chapterId) },
                        onNavigateToGenre = { genreSlug -> navActions.navigateToSearch(genreSlug) }
                    )
                }
                entry<NavNovelDetail> { key ->
                    NovelDetailScreen(
                        novelId = key.novelId,
                        onBack = { navActions.navigateBack() },
                        onNavigateToReader = { novelId, chapterId -> navActions.navigateToReader(novelId, chapterId) },
                        onNavigateToFullChapterList = { novelId -> navActions.navigateToChapterList(novelId) }
                    )
                }
                entry<NavReader> { key ->
                    ReaderScreen(
                        novelId = key.novelId,
                        chapterId = key.chapterId,
                        onBack = { navActions.navigateBack() },
                        onNavigateToChapterList = { novelId -> navActions.navigateToChapterList(novelId) }
                    )
                }
                entry<NavSearch> { key ->
                    SearchScreen(
                        initialGenre = key.initialGenre,
                        onBack = { navActions.navigateBack() },
                        onSelectNovel = { novelId -> navActions.navigateToDetail(novelId) },
                        onSelectGenre = { navActions.navigateToGenreList() }
                    )
                }
                entry<NavGenreList> {
                    GenreListScreen(
                        onBack = { navActions.navigateBack() },
                        onGenreSelected = { genreSlug -> navActions.navigateToSearch(genreSlug) }
                    )
                }
                entry<NavChapterList> { key ->
                    ChapterListScreen(
                        novelId = key.novelId,
                        onBack = { navActions.navigateBack() },
                        onNavigateToReader = { novelId, chapterId -> navActions.navigateToReader(novelId, chapterId) }
                    )
                }
            }
        )
    }
}
