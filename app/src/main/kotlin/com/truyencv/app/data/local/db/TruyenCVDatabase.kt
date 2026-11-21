package com.truyencv.app.data.local.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "novels")
data class NovelEntity(
    @PrimaryKey val id: String,
    val rawId: Long,
    val title: String,
    val author: String,
    val thumbUrl: String,
    val coverBase64: String? = null,
    val description: String,
    val genresJson: String = "[]",
    val totalChapters: Int = 0,
    val views: Long = 0,
    val status: String = "ONGOING",
    val source: String = "TRUYENCV",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface NovelDao {
    @Query("SELECT * FROM novels WHERE id = :id")
    suspend fun getById(id: String): NovelEntity?

    @Query("SELECT * FROM novels")
    fun getAll(): Flow<List<NovelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(novel: NovelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(novels: List<NovelEntity>)

    @Query("DELETE FROM novels WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Database(
    entities = [NovelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TruyenCVDatabase : RoomDatabase() {
    abstract fun novelDao(): NovelDao
}
