package com.truyencv.app.data.mapper

import com.truyencv.app.data.local.db.ChapterEntity
import com.truyencv.app.data.local.db.NovelEntity
import com.truyencv.app.data.remote.truyencv.TruyenCvChapterDetailDto
import com.truyencv.app.data.remote.truyencv.TruyenCvChapterItem
import com.truyencv.app.data.remote.truyencv.WpMangaDto
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.domain.model.Novel
import com.truyencv.app.domain.model.NovelStatus
import com.truyencv.app.domain.model.Source
import com.truyencv.app.util.TextSanitizer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale

object TruyenCvMapper {

    private val json = Json { ignoreUnknownKeys = true }

    fun mapWpMangaToNovel(dto: WpMangaDto): Novel {
        val rawId = dto.id
        val novelId = "${Source.TRUYENCV.prefix}$rawId"
        val title = TextSanitizer.cleanHtml(dto.title?.rendered ?: "")
        val author = resolveAuthor(dto)
        val thumbUrl = resolveThumbnail(dto)
        val description = TextSanitizer.cleanHtml(dto.content?.rendered ?: dto.excerpt?.rendered ?: "")
        val genres = resolveGenres(dto)
        val status = resolveStatus(dto)

        return Novel(
            id = novelId,
            rawId = rawId,
            title = title,
            author = author,
            thumbUrl = thumbUrl,
            coverBase64 = null,
            description = description,
            genres = genres,
            totalChapters = 0,
            views = 0L,
            status = status,
            source = Source.TRUYENCV
        )
    }

    fun mapChapterItemToChapter(
        dto: TruyenCvChapterItem,
        novelId: String,
        order: Int,
        prevId: String? = null,
        nextId: String? = null
    ): Chapter {
        val rawId = dto.id
        val id = "${Source.TRUYENCV.prefix}$rawId"
        val rawTitle = dto.title ?: dto.name ?: ""
        val cleanTitle = TextSanitizer.cleanHtml(rawTitle)
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
            source = Source.TRUYENCV,
            isDownloaded = false
        )
    }

    fun mapChapterDetailToChapter(
        dto: TruyenCvChapterDetailDto,
        novelId: String,
        order: Int,
        prevId: String? = null,
        nextId: String? = null
    ): Chapter {
        val rawId = dto.id
        val id = "${Source.TRUYENCV.prefix}$rawId"
        val rawTitle = dto.title ?: ""
        val cleanTitle = TextSanitizer.cleanHtml(rawTitle)
        val formattedTitle = if (cleanTitle.startsWith("Chương", ignoreCase = true)) {
            cleanTitle
        } else if (cleanTitle.isNotBlank()) {
            "Chương $order: $cleanTitle"
        } else {
            "Chương $order"
        }

        val sanitizedContent = TextSanitizer.sanitizeChapterContent(dto.content)

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
            source = Source.TRUYENCV,
            isDownloaded = !sanitizedContent.isNullOrBlank()
        )
    }

    fun resolveAuthor(dto: WpMangaDto): String {
        // 1. Phân giải từ _embedded['wp:term'] với taxonomy author_tax
        dto.embedded?.terms?.forEach { termList ->
            val authorTerm = termList.firstOrNull { it.taxonomy == "author_tax" && it.name.isNotBlank() }
            if (authorTerm != null) {
                return authorTerm.name.trim()
            }
        }

        // 2. Fallback 1: Trích xuất từ class_list dạng 'author_tax-{slug}'
        dto.classList?.forEach { cls ->
            if (cls.startsWith("author_tax-")) {
                val slug = cls.removePrefix("author_tax-")
                if (slug.isNotBlank()) {
                    return slug.split("-")
                        .joinToString(" ") { word ->
                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        }
                }
            }
        }

        // 3. Fallback 2: author_name hoặc 'Chưa rõ'
        return dto.authorName?.takeIf { it.isNotBlank() } ?: "Chưa rõ"
    }

    fun resolveThumbnail(dto: WpMangaDto): String {
        val rawUrl = dto.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
        return TextSanitizer.sanitizeCoverUrl(rawUrl)
    }

    fun resolveGenres(dto: WpMangaDto): List<String> {
        val result = mutableListOf<String>()

        // 1. Từ _embedded['wp:term'] với taxonomy genre
        dto.embedded?.terms?.forEach { termList ->
            termList.filter { it.taxonomy == "genre" && it.name.isNotBlank() }
                .forEach { result.add(it.name.trim()) }
        }

        if (result.isNotEmpty()) return result.distinct()

        // 2. Fallback: class_list dạng 'genre-{slug}'
        dto.classList?.forEach { cls ->
            if (cls.startsWith("genre-")) {
                val slug = cls.removePrefix("genre-")
                if (slug.isNotBlank()) {
                    val humanized = slug.split("-")
                        .joinToString(" ") { word ->
                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        }
                    result.add(humanized)
                }
            }
        }

        return result.distinct()
    }

    fun resolveStatus(dto: WpMangaDto): NovelStatus {
        if (dto.status.equals("completed", ignoreCase = true)) {
            return NovelStatus.COMPLETED
        }
        if (dto.classList?.any { it.contains("status-completed", ignoreCase = true) } == true) {
            return NovelStatus.COMPLETED
        }
        return NovelStatus.ONGOING
    }

    fun novelToEntity(novel: Novel): NovelEntity {
        return NovelEntity(
            id = novel.id,
            rawId = novel.rawId,
            title = novel.title,
            author = novel.author,
            thumbUrl = novel.thumbUrl,
            coverBase64 = novel.coverBase64,
            description = novel.description,
            genresJson = json.encodeToString(novel.genres),
            totalChapters = novel.totalChapters,
            views = novel.views,
            status = novel.status.name,
            source = novel.source.name,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun entityToNovel(entity: NovelEntity): Novel {
        val genres = try {
            json.decodeFromString<List<String>>(entity.genresJson)
        } catch (_: Exception) {
            emptyList()
        }
        val status = try {
            NovelStatus.valueOf(entity.status)
        } catch (_: Exception) {
            NovelStatus.ONGOING
        }
        val source = try {
            Source.valueOf(entity.source)
        } catch (_: Exception) {
            Source.fromId(entity.id)
        }

        return Novel(
            id = entity.id,
            rawId = entity.rawId,
            title = entity.title,
            author = entity.author,
            thumbUrl = entity.thumbUrl,
            coverBase64 = entity.coverBase64,
            description = entity.description,
            genres = genres,
            totalChapters = entity.totalChapters,
            views = entity.views,
            status = status,
            source = source
        )
    }

    fun chapterToEntity(chapter: Chapter): ChapterEntity {
        return ChapterEntity(
            id = chapter.id,
            rawId = chapter.rawId,
            novelId = chapter.novelId,
            chapterOrder = chapter.chapterOrder,
            title = chapter.title,
            rawTitle = chapter.rawTitle,
            content = chapter.content,
            prevId = chapter.prevId,
            nextId = chapter.nextId,
            source = chapter.source.name,
            isDownloaded = chapter.isDownloaded,
            downloadedAt = if (chapter.isDownloaded) System.currentTimeMillis() else null
        )
    }

    fun entityToChapter(entity: ChapterEntity): Chapter {
        val source = try {
            Source.valueOf(entity.source)
        } catch (_: Exception) {
            Source.fromId(entity.id)
        }

        return Chapter(
            id = entity.id,
            rawId = entity.rawId,
            novelId = entity.novelId,
            chapterOrder = entity.chapterOrder,
            title = entity.title,
            rawTitle = entity.rawTitle,
            content = entity.content,
            prevId = entity.prevId,
            nextId = entity.nextId,
            source = source,
            isDownloaded = entity.isDownloaded
        )
    }
}
