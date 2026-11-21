package com.truyencv.app.util

import java.util.regex.Pattern

object TextSanitizer {

    const val DEFAULT_COVER_URL = "https://truyencv.io/default-cover.png"

    private val HTML_TAG_REGEX = Pattern.compile("<[^>]*>")
    private val MULTI_SPACE_REGEX = Pattern.compile("[ \\t\\x0B\\f]+")
    private val MULTI_NEWLINE_REGEX = Pattern.compile("(\\r?\\n){3,}")
    private val DUPLICATE_CHAPTER_HEADER_REGEX = Pattern.compile(
        "^(?:<p>)?\\s*Chương\\s+\\d+[^<\\r\\n]*(?:<\\/p>|<br\\s*\\/?>)+",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Loại bỏ các thẻ HTML và giải mã các ký tự entity phổ biến.
     */
    fun cleanHtml(text: String?): String {
        if (text.isNullOrBlank()) return ""

        var cleaned = text
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&hellip;", "…")
            .replace("&mdash;", "—")
            .replace("&ndash;", "–")
            .replace("&#8211;", "–")
            .replace("&#8212;", "—")
            .replace("&#8216;", "‘")
            .replace("&#8217;", "’")
            .replace("&#8220;", "“")
            .replace("&#8221;", "”")

        cleaned = HTML_TAG_REGEX.matcher(cleaned).replaceAll("")
        cleaned = MULTI_SPACE_REGEX.matcher(cleaned).replaceAll(" ")
        cleaned = MULTI_NEWLINE_REGEX.matcher(cleaned).replaceAll("\n\n")

        return cleaned.trim()
    }

    /**
     * Sửa URL ảnh bìa bị lỗi double slash (API_INTEGRATION_SPEC.md §7.1).
     * Nếu phần path sau `://` chứa `//` (ví dụ `https://static.cdnno.com/poster//150.jpg`),
     * coi như URL hỏng và trả về [DEFAULT_COVER_URL].
     */
    fun sanitizeCoverUrl(url: String?): String {
        if (url.isNullOrBlank()) return DEFAULT_COVER_URL

        val trimmed = url.trim()
        val schemeIdx = trimmed.indexOf("://")
        if (schemeIdx != -1) {
            val afterScheme = trimmed.substring(schemeIdx + 3)
            if (afterScheme.contains("//")) {
                return DEFAULT_COVER_URL
            }
        }
        return trimmed
    }

    /**
     * Sửa các ký tự escape JSON/MySQL: \" -> ", \' -> ', \\ -> \
     */
    fun unescapeSlashes(text: String?): String {
        if (text.isNullOrBlank()) return ""
        return text
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("\\\\", "\\")
    }

    /**
     * Xóa dòng lặp tiêu đề chương ở đầu bài viết (API_INTEGRATION_SPEC.md §2.5).
     */
    fun removeDuplicateChapterHeader(content: String?): String {
        if (content.isNullOrBlank()) return ""
        val trimmed = content.trimStart()
        val matcher = DUPLICATE_CHAPTER_HEADER_REGEX.matcher(trimmed)
        return if (matcher.find()) {
            matcher.replaceFirst("").trimStart()
        } else {
            trimmed
        }
    }

    /**
     * Chuẩn hóa toàn diện nội dung chương: unescape slashes và xóa dòng tiêu đề lặp.
     */
    fun sanitizeChapterContent(content: String?): String {
        if (content.isNullOrBlank()) return ""
        val unescaped = unescapeSlashes(content)
        return removeDuplicateChapterHeader(unescaped)
    }
}
