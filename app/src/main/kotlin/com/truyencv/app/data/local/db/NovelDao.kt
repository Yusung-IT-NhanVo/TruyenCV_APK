package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {
    @Query("SELECT * FROM novels WHERE id = :id")
    suspend fun getById(id: String): NovelEntity?

    @Query("SELECT * FROM novels ORDER BY lastUpdated DESC")
    fun getAll(): Flow<List<NovelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(novel: NovelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(novels: List<NovelEntity>)

    @Query("DELETE FROM novels WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE novels SET coverBase64 = :base64 WHERE id = :id")
    suspend fun updateCoverBase64(id: String, base64: String)
}
