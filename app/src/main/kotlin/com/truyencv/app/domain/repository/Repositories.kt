package com.truyencv.app.domain.repository

import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Genre
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.RankingPeriod
import com.truyencv.app.domain.model.ReadingProgress
import com.truyencv.app.domain.model.SearchQuery
import com.truyencv.app.domain.model.Source
import kotlinx.coroutines.flow.Flow

interface NovelRepository {
    fun getHotNovels(source: Source?, page: Int): Flow<List<Novel>>
    fun getNewUpdates(source: Source?, page: Int): Flow<List<Novel>>
    fun getRecommended(source: Source?, page: Int): Flow<List<Novel>>
    fun getRanking(period: RankingPeriod, source: Source?, page: Int): Flow<List<Novel>>
    fun searchNovels(query: SearchQuery): Flow<List<Novel>>
    suspend fun getNovelDetail(id: String): Novel
    fun getGenres(source: Source?): Flow<List<Genre>>
}

interface ChapterRepository {
    fun getChapters(novelId: String, page: Int, perPage: Int = 50): Flow<List<Chapter>>
    suspend fun getAllChapters(novelId: String): List<Chapter>
    suspend fun getChapterContent(chapterId: String): Chapter
    suspend fun getChapterBuffer(novelId: String, currentChapterOrder: Int, bufferSize: Int = 6): List<Chapter>
}

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<Novel>>
    suspend fun toggleBookmark(novel: Novel): Boolean
    suspend fun isBookmarked(novelId: String): Boolean
    suspend fun removeBookmark(novelId: String)
}

interface HistoryRepository {
    fun getHistory(): Flow<List<ReadingProgress>>
    suspend fun saveProgress(progress: ReadingProgress)
    suspend fun getProgress(novelId: String): ReadingProgress?
    suspend fun deleteProgress(novelId: String)
    suspend fun clearHistory()
}
