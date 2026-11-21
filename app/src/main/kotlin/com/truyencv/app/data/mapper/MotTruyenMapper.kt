package com.truyencv.app.data.mapper

import com.truyencv.app.data.remote.mottruyen.MotTruyenChapterContentData
import com.truyencv.app.data.remote.mottruyen.MotTruyenChapterItem
import com.truyencv.app.data.remote.mottruyen.MotTruyenStoryDto
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import com.truyencv.app.util.TextSanitizer

object MotTruyenMapper {

    fun mapStoryDtoToNovel(dto: MotTruyenStoryDto): Novel {
        val rawId = dto.resolvedId
        val novelId = "${Source.MOTTRUYEN.prefix}$rawId"
        val title = TextSanitizer.cleanHtml(dto.resolvedName)
        val author = dto.resolvedAuthor.trim().ifBlank { "Chưa rõ" }
        val thumbUrl = TextSanitizer.sanitizeCoverUrl(dto.resolvedThumb)
        val description = TextSanitizer.cleanHtml(dto.resolvedDesc)

        val genres = if (dto.resolvedCat.isNotBlank()) {
            dto.resolvedCat.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }

        val status = if (dto.resolvedStatus == 2) {
            NovelStatus.COMPLETED
        } else {
            NovelStatus.ONGOING
        }

        return Novel(
            id = novelId,
            rawId = rawId,
            title = title,
            author = author,
            thumbUrl = thumbUrl,
            coverBase64 = null,
            description = description,
            genres = genres,
            totalChapters = dto.resolvedTotalChapter,
            views = dto.resolvedView,
            status = status,
            source = Source.MOTTRUYEN
        )
    }

    fun mapChapterItemToChapter(
        dto: MotTruyenChapterItem,
        novelId: String,
        prevId: String? = null,
        nextId: String? = null
    ): Chapter {
        val rawId = dto.resolvedId
        val id = "${Source.MOTTRUYEN.prefix}$rawId"
        val rawTitle = dto.resolvedName
        val cleanTitle = TextSanitizer.cleanHtml(rawTitle)
        val order = dto.resolvedOrder
        val formattedTitle = if (cleanTitle.startsWith("Chương", ignoreCase = true)) {
            cleanTitle
        } else if (cleanTitle.isNotBlank()) {
            "Chương $order: $cleanTitle"
        } else {
            "Chương $order"
        }

        return Chapter(
            id = id,
            rawId = rawId,
            novelId = novelId,
            chapterOrder = order,
            title = formattedTitle,
            rawTitle = rawTitle,
            content = null,
            prevId = prevId,
            nextId = nextId,
            source = Source.MOTTRUYEN,
            isDownloaded = false
        )
    }

    fun mapChapterContentToChapter(
        dto: MotTruyenChapterContentData,
        novelId: String,
        prevId: String? = null,
        nextId: String? = null
    ): Chapter {
        val rawId = dto.resolvedId
        val id = "${Source.MOTTRUYEN.prefix}$rawId"
        val rawTitle = dto.resolvedName
        val cleanTitle = TextSanitizer.cleanHtml(rawTitle)
        val order = dto.resolvedOrder
        val formattedTitle = if (cleanTitle.startsWith("Chương", ignoreCase = true)) {
            cleanTitle
        } else if (cleanTitle.isNotBlank()) {
            "Chương $order: $cleanTitle"
        } else {
            "Chương $order"
        }

        val sanitizedContent = TextSanitizer.sanitizeChapterContent(dto.resolvedContent)

        return Chapter(
            id = id,
            rawId = rawId,
            novelId = novelId,
            chapterOrder = order,
            title = formattedTitle,
            rawTitle = rawTitle,
            content = sanitizedContent,
            prevId = prevId,
            nextId = nextId,
            source = Source.MOTTRUYEN,
            isDownloaded = !sanitizedContent.isNullOrBlank()
        )
    }
}
