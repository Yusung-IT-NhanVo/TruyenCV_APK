package com.truyencv.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Source(val prefix: String, val label: String) {
    TRUYENCV("tcv-", "TruyenCV.io"),
    MOTTRUYEN("mt-", "MotTruyen");

    companion object {
        fun fromId(id: String): Source {
            return when {
                id.startsWith(TRUYENCV.prefix) -> TRUYENCV
                id.startsWith(MOTTRUYEN.prefix) -> MOTTRUYEN
                else -> TRUYENCV
            }
        }
    }
}

@Serializable
enum class NovelStatus(val label: String) {
    ONGOING("Đang ra"),
    COMPLETED("Hoàn thành")
}

@Serializable
data class Novel(
    val id: String,              // "tcv-1234" hoặc "mt-5678"
    val rawId: Long,             // ID gốc từ nguồn
    val title: String,
    val author: String,
    val thumbUrl: String,        // URL ảnh bìa
    val coverBase64: String? = null,
    val description: String,     // Đã strip HTML
    val genres: List<String> = emptyList(),
    val totalChapters: Int = 0,
    val views: Long = 0,
    val status: NovelStatus = NovelStatus.ONGOING,
    val source: Source = Source.TRUYENCV
)

@Serializable
data class Chapter(
    val id: String,              // "tcv-9999" hoặc "mt-8888"
    val rawId: Long,
    val novelId: String,
    val chapterOrder: Int,
    val title: String,           // "Chương 1: Tiêu đề"
    val rawTitle: String,
    val content: String? = null,
    val prevId: String? = null,
    val nextId: String? = null,
    val source: Source = Source.TRUYENCV,
    val isDownloaded: Boolean = false
)

@Serializable
data class Genre(
    val id: String,
    val name: String,
    val slug: String,
    val count: Int = 0,
    val source: Source? = null
)

@Serializable
data class ReadingProgress(
    val novelId: String,
    val chapterId: String,
    val chapterOrder: Int = 1,
    val chapterTitle: String = "",
    val scrollPosition: Int = 0,
    val lastReadTime: Long = System.currentTimeMillis()
)

enum class RankingPeriod(val value: String, val label: String) {
    DAY("day", "Ngày"),
    WEEK("week", "Tuần"),
    MONTH("month", "Tháng"),
    ALL("all", "Tổng")
}

data class SearchQuery(
    val keyword: String = "",
    val genre: String? = null,
    val status: NovelStatus? = null,
    val minChapters: Int? = null,
    val maxChapters: Int? = null,
    val sort: String = "modified",
    val source: Source? = null,
    val page: Int = 1
)
