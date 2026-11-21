package com.truyencv.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Top-level main container hosting the 4 bottom navigation tabs:
 * Explore, Ranking, Library, Settings.
 */
@Serializable
data object NavMain : NavKey

/**
 * Novel Detail screen route.
 * @param novelId Unique identifier of the novel (e.g. "tcv-12345" or "mt-67890").
 */
@Serializable
data class NavNovelDetail(val novelId: String) : NavKey

/**
 * Reader screen route for reading a specific chapter.
 * @param novelId Unique identifier of the novel.
 * @param chapterId Unique identifier or slug of the chapter.
 */
@Serializable
data class NavReader(val novelId: String, val chapterId: String) : NavKey

/**
 * Search & Filter screen route.
 * @param initialGenre Optional genre canonical slug to pre-filter on open.
 */
@Serializable
data class NavSearch(val initialGenre: String? = null) : NavKey

/**
 * Category / Genre List screen route.
 */
@Serializable
data object NavGenreList : NavKey

/**
 * Full Chapter List screen route.
 * @param novelId Unique identifier of the novel.
 */
@Serializable
data class NavChapterList(val novelId: String) : NavKey
