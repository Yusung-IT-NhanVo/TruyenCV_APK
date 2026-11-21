package com.truyencv.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.truyencv.app.NavChapterList
import com.truyencv.app.NavGenreList
import com.truyencv.app.NavMain
import com.truyencv.app.NavNovelDetail
import com.truyencv.app.NavReader
import com.truyencv.app.NavSearch

/**
 * Coordinator for app-wide navigation actions using Jetpack Navigation 3.
 *
 * @param backStack The Navigation 3 backstack holding current active [NavKey] entries.
 */
class NavigationActions(
    private val backStack: NavBackStack<NavKey>
) {
    /**
     * Whether backward navigation is possible (more than 1 entry in the backstack).
     */
    val canNavigateBack: Boolean
        get() = backStack.size > 1

    /**
     * The current active screen [NavKey], or null if the stack is empty.
     */
    val currentKey: NavKey?
        get() = backStack.lastOrNull()

    /**
     * Resets backstack to root Main screen (Bottom navigation container).
     */
    fun navigateToMain() {
        backStack.clear()
        backStack.add(NavMain)
    }

    /**
     * Navigates to the Novel Detail screen.
     *
     * @param novelId Unique identifier of the novel (e.g. "tcv-12345" or "mt-67890").
     */
    fun navigateToDetail(novelId: String) {
        backStack.add(NavNovelDetail(novelId))
    }

    /**
     * Navigates to the Reader screen for reading a specific chapter.
     *
     * @param novelId Unique identifier of the novel.
     * @param chapterId Unique identifier or order slug of the chapter.
     */
    fun navigateToReader(novelId: String, chapterId: String) {
        backStack.add(NavReader(novelId, chapterId))
    }

    /**
     * Navigates to the Search & Filter screen, optionally pre-filtering by genre.
     *
     * @param initialGenre Optional genre canonical slug to pre-filter on open.
     */
    fun navigateToSearch(initialGenre: String? = null) {
        backStack.add(NavSearch(initialGenre))
    }

    /**
     * Navigates to the full Category / Genre List screen.
     */
    fun navigateToGenreList() {
        backStack.add(NavGenreList)
    }

    /**
     * Navigates to the full Chapter List screen for a novel.
     *
     * @param novelId Unique identifier of the novel.
     */
    fun navigateToChapterList(novelId: String) {
        backStack.add(NavChapterList(novelId))
    }

    /**
     * Pops the top screen from the backstack.
     *
     * @return true if popped successfully, false if already at root.
     */
    fun navigateBack(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeLastOrNull() != null
        } else {
            false
        }
    }
}

/**
 * CompositionLocal providing [NavigationActions] down the Composable tree.
 */
val LocalNavigationActions = compositionLocalOf<NavigationActions> {
    error("No NavigationActions provided. Ensure MainNavigation supplies LocalNavigationActions.")
}

/**
 * Creates and remembers a [NavigationActions] instance bound to the given [NavBackStack].
 */
@Composable
fun rememberNavigationActions(backStack: NavBackStack<NavKey>): NavigationActions {
    return remember(backStack) {
        NavigationActions(backStack)
    }
}
