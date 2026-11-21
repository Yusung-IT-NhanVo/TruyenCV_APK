package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

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
