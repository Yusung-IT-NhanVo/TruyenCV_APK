package com.truyencv.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object NavMain : NavKey

@Serializable
data class NavNovelDetail(val novelId: String) : NavKey

@Serializable
data class NavReader(val novelId: String, val chapterId: String) : NavKey

@Serializable
data object NavSearch : NavKey

@Serializable
data class NavChapterList(val novelId: String) : NavKey
