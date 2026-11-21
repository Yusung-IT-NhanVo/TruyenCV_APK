package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val novelId: String,
    val chapterId: String,
    val chapterOrder: Int,
    val chapterTitle: String,
    val scrollPosition: Int,
    val lastReadTime: Long = System.currentTimeMillis()
)
