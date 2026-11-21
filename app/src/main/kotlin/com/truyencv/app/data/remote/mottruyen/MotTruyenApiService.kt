package com.truyencv.app.data.remote.mottruyen

import retrofit2.http.GET
import retrofit2.http.Query

interface MotTruyenApiService {
    @GET("filter")
    suspend fun filterNovels(
        @Query("kind") kind: Int = 0,
        @Query("cat") categoryId: Int = 0,
        @Query("status") status: Int = 0,
        @Query("totalchapter") totalChapter: Int = 0,
        @Query("sort") sort: Int = 0,
        @Query("page") page: Int = 1
    ): String

    @GET("listchap")
    suspend fun getChapterList(
        @Query("story_id") storyId: Long
    ): String

    @GET("chapter/")
    suspend fun getChapterContent(
        @Query("chapter_id") chapterId: Long
    ): String
}
