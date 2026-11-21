package com.truyencv.app.data.remote.mottruyen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MotTruyenStoryDto(
    @SerialName("ID") val ID: Long? = null,
    val id: Long? = null,
    @SerialName("NAME") val NAME: String? = null,
    val name: String? = null,
    @SerialName("THUMB") val THUMB: String? = null,
    val thumb: String? = null,
    @SerialName("AUTHOR") val AUTHOR: String? = null,
    val author: String? = null,
    @SerialName("DESC") val DESC: String? = null,
    val desc: String? = null,
    @SerialName("CAT") val CAT: String? = null,
    val cat: String? = null,
    @SerialName("STATUS") val STATUS: Int? = null,
    val status: Int? = null,
    @SerialName("TOTALCHAPTER") val TOTALCHAPTER: Int? = null,
    val totalchapter: Int? = null,
    val totalChapter: Int? = null,
    @SerialName("VIEW") val VIEW: Long? = null,
    val view: Long? = null,
    @SerialName("RATE") val RATE: Double? = null,
    val rate: Double? = null,
    @SerialName("SLUG") val SLUG: String? = null,
    val slug: String? = null
) {
    val resolvedId: Long get() = ID ?: id ?: 0L
    val resolvedName: String get() = NAME ?: name ?: ""
    val resolvedThumb: String get() = THUMB ?: thumb ?: ""
    val resolvedAuthor: String get() = AUTHOR ?: author ?: ""
    val resolvedDesc: String get() = DESC ?: desc ?: ""
    val resolvedCat: String get() = CAT ?: cat ?: ""
    val resolvedStatus: Int get() = STATUS ?: status ?: 1
    val resolvedTotalChapter: Int get() = TOTALCHAPTER ?: totalchapter ?: totalChapter ?: 0
    val resolvedView: Long get() = VIEW ?: view ?: 0L
    val resolvedSlug: String get() = SLUG ?: slug ?: ""
}

@Serializable
data class MotTruyenFilterResponse(
    val status: Boolean? = null,
    val data: List<MotTruyenStoryDto> = emptyList(),
    @SerialName("total_page") val totalPage: Int? = null,
    @SerialName("total_item") val totalItem: Int? = null
)

@Serializable
data class MotTruyenCategoryItem(
    @SerialName("ID") val ID: Int? = null,
    val id: Int? = null,
    @SerialName("NAME") val NAME: String? = null,
    val name: String? = null,
    @SerialName("SLUG") val SLUG: String? = null,
    val slug: String? = null
) {
    val resolvedId: Int get() = ID ?: id ?: 0
    val resolvedName: String get() = NAME ?: name ?: ""
    val resolvedSlug: String get() = SLUG ?: slug ?: ""
}

@Serializable
data class MotTruyenCategoryResponse(
    val data: List<MotTruyenCategoryItem> = emptyList()
)

@Serializable
data class MotTruyenStoryDetailResponse(
    val status: Boolean? = null,
    val data: MotTruyenStoryDto? = null
)

@Serializable
data class MotTruyenChapterItem(
    @SerialName("ID") val ID: Long? = null,
    val id: Long? = null,
    @SerialName("NAME") val NAME: String? = null,
    val name: String? = null,
    @SerialName("ORDER") val ORDER: Int? = null,
    val order: Int? = null
) {
    val resolvedId: Long get() = ID ?: id ?: 0L
    val resolvedName: String get() = NAME ?: name ?: ""
    val resolvedOrder: Int get() = ORDER ?: order ?: 0
}

@Serializable
data class MotTruyenChapterListResponse(
    val data: List<MotTruyenChapterItem> = emptyList()
)

@Serializable
data class MotTruyenChapterContentData(
    @SerialName("ID") val ID: Long? = null,
    val id: Long? = null,
    @SerialName("NAME") val NAME: String? = null,
    val name: String? = null,
    @SerialName("ORDER") val ORDER: Int? = null,
    val order: Int? = null,
    @SerialName("CONTENT") val CONTENT: String? = null,
    val content: String? = null
) {
    val resolvedId: Long get() = ID ?: id ?: 0L
    val resolvedName: String get() = NAME ?: name ?: ""
    val resolvedOrder: Int get() = ORDER ?: order ?: 0
    val resolvedContent: String get() = CONTENT ?: content ?: ""
}

@Serializable
data class MotTruyenChapterContentResponse(
    val data: MotTruyenChapterContentData? = null
)
