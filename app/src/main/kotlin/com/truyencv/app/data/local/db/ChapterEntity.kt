package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    indices = [
        Index(value = ["novelId"]),
        Index(value = ["novelId", "chapterOrder"])
    ]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val rawId: Long,
    val novelId: String,
    val chapterOrder: Int,
    val title: String,
    val rawTitle: String,
    val content: String? = null,
    val prevId: String? = null,
    val nextId: String? = null,
    val source: String = "TRUYENCV",
    val isDownloaded: Boolean = false,
    val downloadedAt: Long? = null
)
