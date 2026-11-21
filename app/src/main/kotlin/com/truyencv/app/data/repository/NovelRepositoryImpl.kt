package com.truyencv.app.data.repository

import com.truyencv.app.data.local.db.GenreDao
import com.truyencv.app.data.local.db.GenreEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.mapper.MotTruyenMapper
import com.truyencv.app.data.mapper.TruyenCvMapper
import com.truyencv.app.data.remote.mottruyen.MotTruyenApiService
import com.truyencv.app.data.remote.truyencv.TruyenCvApiService
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.NovelRepository
import com.truyencv.app.util.GenreMappingTable
import com.truyencv.app.util.interleave
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NovelRepositoryImpl(
    private val truyenCvApi: TruyenCvApiService,
    private val motTruyenApi: MotTruyenApiService,
    private val novelDao: NovelDao,
    private val genreDao: GenreDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NovelRepository {

    override fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>> = flow {
        val novels = fetchBySource(
            source = source,
            fetchTcv = {
                truyenCvApi.getMangaList(page = page, perPage = 20, orderby = "views", order = "desc")
                    .map { TruyenCvMapper.mapWpMangaToNovel(it) }
            },
            fetchMt = {
                motTruyenApi.filterNovels(sort = 2, page = page)
                    .data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
            }
        )
        cacheNovels(novels)
        emit(novels)
    }.flowOn(ioDispatcher)

    override fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>> = flow {
        val novels = fetchBySource(
            source = source,
            fetchTcv = {
                truyenCvApi.getMangaList(page = page, perPage = 20, orderby = "modified", order = "desc")
                    .map { TruyenCvMapper.mapWpMangaToNovel(it) }
            },
            fetchMt = {
                motTruyenApi.filterNovels(sort = 0, page = page)
                    .data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
            }
        )
        cacheNovels(novels)
        emit(novels)
    }.flowOn(ioDispatcher)

    override fun getRecommended(source: Source?, page: Int): Flow<List<Novel>> = flow {
        val novels = fetchBySource(
            source = source,
            fetchTcv = {
                truyenCvApi.getMangaList(page = page, perPage = 20, genre = 639L)
                    .map { TruyenCvMapper.mapWpMangaToNovel(it) }
            },
            fetchMt = {
                motTruyenApi.filterNovels(sort = 6, page = page)
                    .data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
            }
        )
        cacheNovels(novels)
        emit(novels)
    }.flowOn(ioDispatcher)

    override fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>> = flow {
        val novels = fetchBySource(
            source = source,
            fetchTcv = {
                try {
                    val orderby = when (period) {
                        RankingPeriod.DAY -> "views"
                        RankingPeriod.WEEK -> "views_week"
                        RankingPeriod.MONTH -> "views_month"
                        RankingPeriod.ALL -> "views"
                    }
                    truyenCvApi.getMangaList(page = page, perPage = 20, orderby = orderby, order = "desc")
                        .map { TruyenCvMapper.mapWpMangaToNovel(it) }
                } catch (_: Exception) {
                    emptyList()
                }
            },
            fetchMt = {
                val mtSort = when (period) {
                    RankingPeriod.DAY -> 5
                    RankingPeriod.WEEK -> 4
                    RankingPeriod.MONTH -> 3
                    RankingPeriod.ALL -> 2
                }
                motTruyenApi.filterNovels(sort = mtSort, page = page)
                    .data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
            }
        )
        cacheNovels(novels)
        emit(novels)
    }.flowOn(ioDispatcher)

    override fun searchNovels(query: SearchQuery): Flow<List<Novel>> = flow {
        val novels = fetchBySource(
            source = query.source,
            fetchTcv = {
                val tcvGenreId = query.genre?.let {
                    GenreMappingTable.getTcvGenreId(it) ?: it.toLongOrNull()
                }
                val statusStr = when (query.status) {
                    NovelStatus.COMPLETED -> "completed"
                    NovelStatus.ONGOING -> "publish"
                    null -> null
                }
                val orderby = when (query.sort) {
                    "views" -> "views"
                    "date" -> "date"
                    "rating" -> "rating"
                    else -> "modified"
                }
                truyenCvApi.getMangaList(
                    page = query.page,
                    perPage = 20,
                    search = query.keyword.ifBlank { null },
                    genre = tcvGenreId,
                    status = statusStr,
                    orderby = orderby
                ).map { TruyenCvMapper.mapWpMangaToNovel(it) }
            },
            fetchMt = {
                if (query.keyword.isNotBlank() && query.genre == null && query.status == null) {
                    motTruyenApi.searchNovels(keyword = query.keyword, page = query.page)
                        .data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
                } else {
                    val mtCatId = query.genre?.let {
                        GenreMappingTable.getMotTruyenCatId(it) ?: it.toIntOrNull()
                    } ?: 0
                    val mtStatus = when (query.status) {
                        NovelStatus.ONGOING -> 1
                        NovelStatus.COMPLETED -> 2
                        null -> 0
                    }
                    val mtSort = when (query.sort) {
                        "views" -> 2
                        "date" -> 1
                        "rating" -> 8
                        else -> 0
                    }
                    val totalChapter = when {
                        query.maxChapters != null && query.maxChapters <= 100 -> 1
                        query.minChapters != null && query.minChapters >= 1000 -> 5
                        else -> 0
                    }
                    motTruyenApi.filterNovels(
                        categoryId = mtCatId,
                        status = mtStatus,
                        totalChapter = totalChapter,
                        sort = mtSort,
                        page = query.page
                    ).data.map { MotTruyenMapper.mapStoryDtoToNovel(it) }
                }
            }
        )
        cacheNovels(novels)
        emit(novels)
    }.flowOn(ioDispatcher)

    override suspend fun getNovelDetail(id: String): Novel = withContext(ioDispatcher) {
        val cached = novelDao.getById(id)
        val source = Source.fromId(id)

        try {
            val freshNovel = when (source) {
                Source.TRUYENCV -> {
                    val rawId = id.removePrefix(Source.TRUYENCV.prefix).toLong()
                    val dto = truyenCvApi.getMangaDetail(rawId)
                    val base = TruyenCvMapper.mapWpMangaToNovel(dto)

                    // Enrichment: Lấy số chương chính xác từ /initmanga/v1/chapters
                    val totalChapters = try {
                        val chaptersResponse = truyenCvApi.getChapters(mangaId = rawId, perPage = 1, page = 1)
                        chaptersResponse.totalPages
                    } catch (_: Exception) {
                        base.totalChapters
                    }
                    base.copy(
                        totalChapters = totalChapters,
                        coverBase64 = cached?.coverBase64
                    )
                }

                Source.MOTTRUYEN -> {
                    val rawId = id.removePrefix(Source.MOTTRUYEN.prefix).toLong()
                    val response = motTruyenApi.getStoryDetail(rawId)
                    val dto = response.data ?: throw IllegalStateException("Không tìm thấy truyện MotTruyen: $id")
                    val base = MotTruyenMapper.mapStoryDtoToNovel(dto)
                    base.copy(coverBase64 = cached?.coverBase64)
                }
            }

            novelDao.insert(TruyenCvMapper.novelToEntity(freshNovel))
            freshNovel
        } catch (e: Exception) {
            if (cached != null) {
                TruyenCvMapper.entityToNovel(cached)
            } else {
                throw e
            }
        }
    }

    override fun getGenres(source: Source?): Flow<List<Genre>> = flow {
        val genres = when (source) {
            Source.TRUYENCV -> {
                try {
                    val list = truyenCvApi.getGenres(100).map {
                        Genre(
                            id = it.id.toString(),
                            name = it.name,
                            slug = it.slug,
                            count = it.count,
                            source = Source.TRUYENCV
                        )
                    }
                    genreDao.insertAll(list.map { GenreEntity(it.id, it.name, it.slug, it.count, Source.TRUYENCV.name) })
                    list
                } catch (_: Exception) {
                    GenreMappingTable.getAllCanonicalGenres().map {
                        Genre(it.tcvGenreId.toString(), it.name, it.slug, 0, Source.TRUYENCV)
                    }
                }
            }

            Source.MOTTRUYEN -> {
                try {
                    val list = motTruyenApi.getCategories().data.map {
                        Genre(
                            id = it.resolvedId.toString(),
                            name = it.resolvedName,
                            slug = it.resolvedSlug,
                            count = 0,
                            source = Source.MOTTRUYEN
                        )
                    }
                    genreDao.insertAll(list.map { GenreEntity(it.id, it.name, it.slug, it.count, Source.MOTTRUYEN.name) })
                    list
                } catch (_: Exception) {
                    GenreMappingTable.getAllCanonicalGenres().map {
                        Genre(it.motTruyenCatId.toString(), it.name, it.slug, 0, Source.MOTTRUYEN)
                    }
                }
            }

            null -> {
                GenreMappingTable.getAllCanonicalGenres().map {
                    Genre(id = it.slug, name = it.name, slug = it.slug, count = 0, source = null)
                }
            }
        }
        emit(genres)
    }.flowOn(ioDispatcher)

    private suspend fun fetchBySource(
        source: Source?,
        fetchTcv: suspend () -> List<Novel>,
        fetchMt: suspend () -> List<Novel>
    ): List<Novel> = coroutineScope {
        when (source) {
            Source.TRUYENCV -> {
                try { fetchTcv() } catch (_: Exception) { emptyList() }
            }
            Source.MOTTRUYEN -> {
                try { fetchMt() } catch (_: Exception) { emptyList() }
            }
            null -> {
                val tcvDeferred = async { try { fetchTcv() } catch (_: Exception) { emptyList() } }
                val mtDeferred = async { try { fetchMt() } catch (_: Exception) { emptyList() } }
                val listTcv = tcvDeferred.await()
                val listMt = mtDeferred.await()
                interleave(listTcv, listMt)
            }
        }
    }

    private suspend fun cacheNovels(novels: List<Novel>) {
        if (novels.isNotEmpty()) {
            try {
                novelDao.insertAll(novels.map { TruyenCvMapper.novelToEntity(it) })
            } catch (_: Exception) {
                // Ignore cache error
            }
        }
    }
}
