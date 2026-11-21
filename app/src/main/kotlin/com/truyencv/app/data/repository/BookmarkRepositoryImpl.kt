package com.truyencv.app.data.repository

import com.truyencv.app.data.local.db.BookmarkDao
import com.truyencv.app.data.local.db.BookmarkEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.mapper.TruyenCvMapper
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao,
    private val novelDao: NovelDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BookmarkRepository {

    override fun getBookmarks(): Flow<List<Novel>> {
        return bookmarkDao.getBookmarkedNovels()
            .map { list -> list.map { TruyenCvMapper.entityToNovel(it) } }
            .flowOn(ioDispatcher)
    }

    override suspend fun toggleBookmark(novel: Novel): Boolean = withContext(ioDispatcher) {
        val currentlyBookmarked = bookmarkDao.isBookmarked(novel.id)
        if (currentlyBookmarked) {
            bookmarkDao.delete(novel.id)
            false
        } else {
            novelDao.insert(TruyenCvMapper.novelToEntity(novel))
            bookmarkDao.insert(BookmarkEntity(novelId = novel.id))
            true
        }
    }

    override suspend fun isBookmarked(novelId: String): Boolean = withContext(ioDispatcher) {
        bookmarkDao.isBookmarked(novelId)
    }

    override suspend fun removeBookmark(novelId: String) = withContext(ioDispatcher) {
        bookmarkDao.delete(novelId)
    }
}
