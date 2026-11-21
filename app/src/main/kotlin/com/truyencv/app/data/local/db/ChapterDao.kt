package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterOrder ASC")
    fun getChaptersForNovel(novelId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterOrder ASC")
    suspend fun getChaptersListForNovel(novelId: String): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterOrder ASC LIMIT :limit OFFSET :offset")
    suspend fun getChaptersPaged(novelId: String, limit: Int, offset: Int): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE novelId = :novelId AND chapterOrder BETWEEN :minOrder AND :maxOrder ORDER BY chapterOrder ASC")
    suspend fun getChaptersByOrderRange(novelId: String, minOrder: Int, maxOrder: Int): List<ChapterEntity>

    @Query("SELECT id FROM chapters WHERE novelId = :novelId AND isDownloaded = 1")
    suspend fun getDownloadedChapterIds(novelId: String): List<String>

    @Query("SELECT COUNT(*) FROM chapters WHERE novelId = :novelId")
    suspend fun getChapterCount(novelId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: ChapterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapters: List<ChapterEntity>)

    @Query("UPDATE chapters SET content = :content, isDownloaded = :isDownloaded, downloadedAt = :downloadedAt WHERE id = :id")
    suspend fun updateContent(id: String, content: String, isDownloaded: Boolean, downloadedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM chapters WHERE novelId = :novelId")
    suspend fun deleteByNovelId(novelId: String)

    @Query("UPDATE chapters SET content = NULL, isDownloaded = 0, downloadedAt = NULL WHERE novelId = :novelId")
    suspend fun clearDownloadedContent(novelId: String)
}
