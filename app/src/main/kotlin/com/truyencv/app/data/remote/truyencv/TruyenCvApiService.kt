package com.truyencv.app.data.remote.truyencv

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TruyenCvApiService {
    @GET("wp/v2/manga")
    suspend fun getMangaList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("_embed") embed: String = "1"
    ): String

    @GET("initmanga/v1/chapters")
    suspend fun getChapters(
        @Query("manga_id") mangaId: Long,
        @Query("per_page") perPage: Int,
        @Query("paged") page: Int
    ): String

    @GET("initmanga/v1/chapter/{chapterId}")
    suspend fun getChapterContent(
        @Path("chapterId") chapterId: Long
    ): String
}
