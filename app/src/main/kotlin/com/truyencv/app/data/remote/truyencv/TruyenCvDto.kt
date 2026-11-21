package com.truyencv.app.data.remote.truyencv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WpRendered(
    val rendered: String = ""
)

@Serializable
data class WpAuthorItem(
    val id: Long? = null,
    val name: String? = null,
    val slug: String? = null
)

@Serializable
data class WpFeaturedMedia(
    val id: Long? = null,
    @SerialName("source_url") val sourceUrl: String? = null
)

@Serializable
data class WpTermItem(
    val id: Long = 0,
    val name: String = "",
    val slug: String = "",
    val taxonomy: String = ""
)

@Serializable
data class WpEmbedded(
    val author: List<WpAuthorItem>? = null,
    @SerialName("wp:featuredmedia") val featuredMedia: List<WpFeaturedMedia>? = null,
    @SerialName("wp:term") val terms: List<List<WpTermItem>>? = null
)

@Serializable
data class WpMangaDto(
    val id: Long,
    val title: WpRendered? = null,
    val content: WpRendered? = null,
    val excerpt: WpRendered? = null,
    val author: Long? = null,
    @SerialName("author_name") val authorName: String? = null,
    @SerialName("class_list") val classList: List<String>? = null,
    @SerialName("_embedded") val embedded: WpEmbedded? = null,
    val status: String? = null,
    val modified: String? = null,
    val date: String? = null
)

@Serializable
data class WpGenreDto(
    val id: Long,
    val name: String,
    val slug: String,
    val count: Int = 0
)

@Serializable
data class TruyenCvChapterItem(
    val id: Long,
    val number: String? = null,
    val slug: String? = null,
    val title: String? = null,
    val name: String? = null
)

@Serializable
data class TruyenCvChaptersResponse(
    val items: List<TruyenCvChapterItem> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1
)

@Serializable
data class TruyenCvChapterDetailDto(
    val id: Long,
    val number: String? = null,
    val slug: String? = null,
    val title: String? = null,
    val content: String? = null
)

@Serializable
data class TruyenCvRankingItem(
    val id: Long? = null,
    val title: String? = null,
    val views: Long? = null,
    @SerialName("total_views") val totalViews: Long? = null,
    val thumb: String? = null,
    val author: String? = null
)

@Serializable
data class TruyenCvMangaByChaptersResponse(
    val success: Boolean = true,
    val ids: List<Long> = emptyList()
)
