package com.truyencv.app.data.remote.truyencv

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TruyenCvApiService {

    @GET("wp/v2/manga")
    suspend fun getMangaList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("_embed") embed: String = "1",
        @Query("genre") genre: Long? = null,
        @Query("search") search: String? = null,
        @Query("orderby") orderby: String? = null,
        @Query("order") order: String = "desc",
        @Query("include") include: String? = null,
        @Query("manga_status") status: String? = null
    ): List<WpMangaDto>

    @GET("wp/v2/manga/{id}")
    suspend fun getMangaDetail(
        @Path("id") id: Long,
        @Query("_embed") embed: String = "1"
    ): WpMangaDto

    @GET("wp/v2/genre")
    suspend fun getGenres(
        @Query("per_page") perPage: Int = 100
    ): List<WpGenreDto>

    @GET("initmanga/v1/chapters")
    suspend fun getChapters(
        @Query("manga_id") mangaId: Long,
        @Query("per_page") perPage: Int = 50,
        @Query("paged") page: Int = 1
    ): TruyenCvChaptersResponse

    @GET("initmanga/v1/chapter/{chapterId}")
    suspend fun getChapterContent(
        @Path("chapterId") chapterId: Long
    ): TruyenCvChapterDetailDto

    @GET("initmanga/v1/ranking")
    suspend fun getRanking(
        @Query("type") type: String,
        @Query("per_page") perPage: Int = 20,
        @Query("paged") page: Int = 1
    ): List<TruyenCvRankingItem>

    @GET("initmanga/v1/manga-by-chapters")
    suspend fun getMangaByChapters(
        @Query("min") min: Int,
        @Query("max") max: Int
    ): TruyenCvMangaByChaptersResponse
}
