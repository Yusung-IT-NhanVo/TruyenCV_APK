package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_tasks")
data class DownloadTaskEntity(
    @PrimaryKey val novelId: String,
    val novelTitle: String,
    val thumbUrl: String,
    val totalChapters: Int,
    val downloadedChapters: Int = 0,
    val status: String = "QUEUED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
