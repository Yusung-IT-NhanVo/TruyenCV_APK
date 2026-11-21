package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT novels.* FROM novels INNER JOIN bookmarks ON novels.id = bookmarks.novelId ORDER BY bookmarks.createdAt DESC")
    fun getBookmarkedNovels(): Flow<List<NovelEntity>>

    @Query("SELECT * FROM bookmarks WHERE novelId = :novelId")
    suspend fun getBookmark(novelId: String): BookmarkEntity?

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE novelId = :novelId")
    suspend fun isBookmarked(novelId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE novelId = :novelId")
    suspend fun delete(novelId: String)
}
