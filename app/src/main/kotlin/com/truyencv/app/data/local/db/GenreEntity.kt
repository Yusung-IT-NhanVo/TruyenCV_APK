package com.truyencv.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val count: Int = 0,
    val source: String? = null
)
