package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadTaskDao {
    @Query("SELECT * FROM download_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<DownloadTaskEntity>>

    @Query("SELECT * FROM download_tasks WHERE novelId = :novelId")
    suspend fun getTask(novelId: String): DownloadTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(task: DownloadTaskEntity)

    @Query("UPDATE download_tasks SET downloadedChapters = :downloadedChapters, status = :status, updatedAt = :updatedAt WHERE novelId = :novelId")
    suspend fun updateProgress(
        novelId: String,
        downloadedChapters: Int,
        status: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM download_tasks WHERE novelId = :novelId")
    suspend fun delete(novelId: String)
}
