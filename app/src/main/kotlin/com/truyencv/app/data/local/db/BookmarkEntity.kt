package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val novelId: String,
    val createdAt: Long = System.currentTimeMillis()
)
