package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingHistoryDao {
    @Query("SELECT * FROM reading_history ORDER BY lastReadTime DESC")
    fun getAll(): Flow<List<ReadingHistoryEntity>>

    @Query("SELECT * FROM reading_history WHERE novelId = :novelId")
    suspend fun getByNovelId(novelId: String): ReadingHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history WHERE novelId = :novelId")
    suspend fun deleteByNovelId(novelId: String)

    @Query("DELETE FROM reading_history")
    suspend fun clearAll()
}
