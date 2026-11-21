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
        @Query("date") date: String? = null,
        @Query("sort") sort: Int = 0,
        @Query("page") page: Int = 1
    ): MotTruyenFilterResponse

    @GET("tim-kiem/")
    suspend fun searchNovels(
        @Query("k") keyword: String,
        @Query("page_number") page: Int = 1
    ): MotTruyenFilterResponse

    @GET("getListCategory")
    suspend fun getCategories(): MotTruyenCategoryResponse

    @GET("story/")
    suspend fun getStoryDetail(
        @Query("story_id") storyId: Long,
        @Query("page_numer") pageNumber: Int = 1,
        @Query("page_size") pageSize: Int = 1
    ): MotTruyenStoryDetailResponse

    @GET("listchap")
    suspend fun getChapterList(
        @Query("story_id") storyId: Long
    ): MotTruyenChapterListResponse

    @GET("chapter/")
    suspend fun getChapterContent(
        @Query("chapter_id") chapterId: Long
    ): MotTruyenChapterContentResponse
}
