package com.truyencv.app.data.repository

import com.truyencv.app.data.local.db.ReadingHistoryDao
import com.truyencv.app.data.local.db.ReadingHistoryEntity
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val readingHistoryDao: ReadingHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HistoryRepository {

    override fun getHistory(): Flow<List<ReadingProgress>> {
        return readingHistoryDao.getAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveProgress(progress: ReadingProgress) = withContext(ioDispatcher) {
        readingHistoryDao.insertOrUpdate(progress.toEntity())
    }

    override suspend fun getProgress(novelId: String): ReadingProgress? = withContext(ioDispatcher) {
        readingHistoryDao.getByNovelId(novelId)?.toDomain()
    }

    override suspend fun deleteProgress(novelId: String) = withContext(ioDispatcher) {
        readingHistoryDao.deleteByNovelId(novelId)
    }

    override suspend fun clearHistory() = withContext(ioDispatcher) {
        readingHistoryDao.clearAll()
    }

    private fun ReadingHistoryEntity.toDomain(): ReadingProgress {
        return ReadingProgress(
            novelId = novelId,
            chapterId = chapterId,
            chapterOrder = chapterOrder,
            chapterTitle = chapterTitle,
            scrollPosition = scrollPosition,
            lastReadTime = lastReadTime
        )
    }

    private fun ReadingProgress.toEntity(): ReadingHistoryEntity {
        return ReadingHistoryEntity(
            novelId = novelId,
            chapterId = chapterId,
            chapterOrder = chapterOrder,
            chapterTitle = chapterTitle,
            scrollPosition = scrollPosition,
            lastReadTime = lastReadTime
        )
    }
}
