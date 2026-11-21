package com.truyencv.app.data.repository

import com.truyencv.app.data.local.db.ChapterDao
import com.truyencv.app.data.mapper.MotTruyenMapper
import com.truyencv.app.data.mapper.TruyenCvMapper
import com.truyencv.app.data.remote.mottruyen.MotTruyenApiService
import com.truyencv.app.data.remote.truyencv.TruyenCvApiService
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Source
import com.truyencv.app.domain.repository.ChapterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ChapterRepositoryImpl(
    private val truyenCvApi: TruyenCvApiService,
    private val motTruyenApi: MotTruyenApiService,
    private val chapterDao: ChapterDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChapterRepository {

    override fun getChapters(novelId: String, page: Int, perPage: Int): Flow<List<Chapter>> = flow {
        val offset = (page - 1) * perPage
        val cached = chapterDao.getChaptersPaged(novelId, limit = perPage, offset = offset)

        if (cached.isNotEmpty()) {
            emit(cached.map { TruyenCvMapper.entityToChapter(it) })
            return@flow
        }

        val chapters = when (Source.fromId(novelId)) {
            Source.TRUYENCV -> {
                val rawId = novelId.removePrefix(Source.TRUYENCV.prefix).toLong()
                val response = truyenCvApi.getChapters(mangaId = rawId, perPage = perPage, page = page)
                response.items.mapIndexed { index, item ->
                    val order = offset + index + 1
                    TruyenCvMapper.mapChapterItemToChapter(item, novelId, order)
                }
            }

            Source.MOTTRUYEN -> {
                val rawId = novelId.removePrefix(Source.MOTTRUYEN.prefix).toLong()
                val response = motTruyenApi.getChapterList(rawId)
                val allChapters = response.data.map { item ->
                    MotTruyenMapper.mapChapterItemToChapter(item, novelId)
                }
                chapterDao.insertAll(allChapters.map { TruyenCvMapper.chapterToEntity(it) })
                allChapters.drop(offset).take(perPage)
            }
        }

        if (chapters.isNotEmpty()) {
            chapterDao.insertAll(chapters.map { TruyenCvMapper.chapterToEntity(it) })
        }
        emit(chapters)
    }.flowOn(ioDispatcher)

    override suspend fun getAllChapters(novelId: String): List<Chapter> = withContext(ioDispatcher) {
        val cached = chapterDao.getChaptersListForNovel(novelId)
        if (cached.isNotEmpty()) {
            return@withContext cached.map { TruyenCvMapper.entityToChapter(it) }
        }

        val chapters = when (Source.fromId(novelId)) {
            Source.TRUYENCV -> {
                val rawId = novelId.removePrefix(Source.TRUYENCV.prefix).toLong()
                val firstPage = truyenCvApi.getChapters(mangaId = rawId, perPage = 50, page = 1)
                val totalPages = firstPage.totalPages
                val allItems = mutableListOf<Chapter>()

                val firstBatch = firstPage.items.mapIndexed { index, item ->
                    TruyenCvMapper.mapChapterItemToChapter(item, novelId, index + 1)
                }
                allItems.addAll(firstBatch)

                for (p in 2..totalPages) {
                    val pageResp = truyenCvApi.getChapters(mangaId = rawId, perPage = 50, page = p)
                    val batch = pageResp.items.mapIndexed { index, item ->
                        val order = (p - 1) * 50 + index + 1
                        TruyenCvMapper.mapChapterItemToChapter(item, novelId, order)
                    }
                    allItems.addAll(batch)
                }
                linkAdjacentChapters(allItems)
            }

            Source.MOTTRUYEN -> {
                val rawId = novelId.removePrefix(Source.MOTTRUYEN.prefix).toLong()
                val response = motTruyenApi.getChapterList(rawId)
                val all = response.data.map { MotTruyenMapper.mapChapterItemToChapter(it, novelId) }
                linkAdjacentChapters(all)
            }
        }

        if (chapters.isNotEmpty()) {
            chapterDao.insertAll(chapters.map { TruyenCvMapper.chapterToEntity(it) })
        }
        chapters
    }

    override suspend fun getChapterContent(chapterId: String): Chapter = withContext(ioDispatcher) {
        val cached = chapterDao.getChapterById(chapterId)
        if (cached != null && !cached.content.isNullOrBlank()) {
            return@withContext TruyenCvMapper.entityToChapter(cached)
        }

        val chapter = when (Source.fromId(chapterId)) {
            Source.TRUYENCV -> {
                val rawId = chapterId.removePrefix(Source.TRUYENCV.prefix).toLong()
                val detailDto = truyenCvApi.getChapterContent(rawId)
                val order = cached?.chapterOrder ?: 1
                val novelId = cached?.novelId ?: ""
                TruyenCvMapper.mapChapterDetailToChapter(
                    dto = detailDto,
                    novelId = novelId,
                    order = order,
                    prevId = cached?.prevId,
                    nextId = cached?.nextId
                )
            }

            Source.MOTTRUYEN -> {
                val rawId = chapterId.removePrefix(Source.MOTTRUYEN.prefix).toLong()
                val response = motTruyenApi.getChapterContent(rawId)
                val contentData = response.data
                    ?: throw IllegalStateException("Không tìm thấy nội dung chương: $chapterId")
                val novelId = cached?.novelId ?: ""
                MotTruyenMapper.mapChapterContentToChapter(
                    dto = contentData,
                    novelId = novelId,
                    prevId = cached?.prevId,
                    nextId = cached?.nextId
                )
            }
        }

        if (cached != null) {
            chapterDao.updateContent(
                id = chapterId,
                content = chapter.content ?: "",
                isDownloaded = chapter.isDownloaded,
                downloadedAt = System.currentTimeMillis()
            )
        } else {
            chapterDao.insert(TruyenCvMapper.chapterToEntity(chapter))
        }

        chapter
    }

    override suspend fun getChapterBuffer(
        novelId: String,
        currentChapterOrder: Int,
        bufferSize: Int
    ): List<Chapter> = withContext(ioDispatcher) {
        val minOrder = maxOf(1, currentChapterOrder - bufferSize)
        val maxOrder = currentChapterOrder + bufferSize

        var list = chapterDao.getChaptersByOrderRange(novelId, minOrder, maxOrder)
        if (list.isEmpty()) {
            getAllChapters(novelId)
            list = chapterDao.getChaptersByOrderRange(novelId, minOrder, maxOrder)
        }

        list.map { TruyenCvMapper.entityToChapter(it) }
    }

    private fun linkAdjacentChapters(chapters: List<Chapter>): List<Chapter> {
        return chapters.mapIndexed { idx, chap ->
            chap.copy(
                prevId = if (idx > 0) chapters[idx - 1].id else null,
                nextId = if (idx < chapters.size - 1) chapters[idx + 1].id else null
            )
        }
    }
}
