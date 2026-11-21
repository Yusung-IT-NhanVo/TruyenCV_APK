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

    private val NUMERIC_DEC_ENTITY = Regex("&#(\\d+);")
    private val NUMERIC_HEX_ENTITY = Regex("&#x([0-9a-fA-F]+);", RegexOption.IGNORE_CASE)
    private val NAMED_ENTITY = Regex("&([a-zA-Z]+);")

    private val HTML_ENTITIES: Map<String, String> = mapOf(
        "nbsp" to " ", "amp" to "&", "quot" to "\"", "apos" to "'", "lt" to "<", "gt" to ">",
        "hellip" to "…", "mdash" to "—", "ndash" to "–", "laquo" to "«", "raquo" to "»",
        "lsquo" to "‘", "rsquo" to "’", "ldquo" to "“", "rdquo" to "”", "bull" to "•",
        // Nguyên âm tiếng Việt thường
        "aacute" to "á", "agrave" to "à", "acirc" to "â", "atilde" to "ã", "auml" to "ä", "aring" to "å",
        "eacute" to "é", "egrave" to "è", "ecirc" to "ê", "euml" to "ë",
        "iacute" to "í", "igrave" to "ì", "icirc" to "î", "iuml" to "ï",
        "oacute" to "ó", "ograve" to "ò", "ocirc" to "ô", "otilde" to "õ", "ouml" to "ö", "oslash" to "ø",
        "uacute" to "ú", "ugrave" to "ù", "ucirc" to "û", "uuml" to "ü",
        "yacute" to "ý", "yuml" to "ÿ",
        // Nguyên âm tiếng Việt hoa
        "Aacute" to "Á", "Agrave" to "À", "Acirc" to "Â", "Atilde" to "Ã", "Auml" to "Ä", "Aring" to "Å",
        "Eacute" to "É", "Egrave" to "È", "Ecirc" to "Ê", "Euml" to "Ë",
        "Iacute" to "Í", "Igrave" to "Ì", "Icirc" to "Î", "Iuml" to "Ï",
        "Oacute" to "Ó", "Ograve" to "Ò", "Ocirc" to "Ô", "Otilde" to "Õ", "Ouml" to "Ö", "Oslash" to "Ø",
        "Uacute" to "Ú", "Ugrave" to "Ù", "Ucirc" to "Û", "Uuml" to "Ü",
        "Yacute" to "Ý",
        // Ký tự khác
        "ccedil" to "ç", "Ccedil" to "Ç", "ntilde" to "ñ", "Ntilde" to "Ñ",
        "eth" to "ð", "ETH" to "Ð", "thorn" to "þ", "THORN" to "Þ", "szlig" to "ß",
        "copy" to "©", "reg" to "®", "trade" to "™", "deg" to "°", "plusmn" to "±"
    )

    /**
     * Giải mã toàn bộ HTML Entities (numeric decimal, numeric hex và named entities tiếng Việt/HTML4).
     */
    fun unescapeHtmlEntities(text: String?): String {
        if (text.isNullOrBlank()) return ""

        // 1. Thập phân: &#234; -> ê
        val decReplaced = NUMERIC_DEC_ENTITY.replace(text) { match ->
            try {
                match.groupValues[1].toInt().toChar().toString()
            } catch (_: Exception) {
                match.value
            }
        }

        // 2. Thập lục phân: &#x00ea; -> ê
        val hexReplaced = NUMERIC_HEX_ENTITY.replace(decReplaced) { match ->
            try {
                match.groupValues[1].toInt(16).toChar().toString()
            } catch (_: Exception) {
                match.value
            }
        }

        // 3. Ký tự có tên: &ecirc; -> ê
        return NAMED_ENTITY.replace(hexReplaced) { match ->
            val entityName = match.groupValues[1]
            HTML_ENTITIES[entityName] ?: match.value
        }
    }

    /**
     * Loại bỏ các thẻ HTML và giải mã toàn bộ các ký tự entity.
     */
    fun cleanHtml(text: String?): String {
        if (text.isNullOrBlank()) return ""

        var cleaned = unescapeHtmlEntities(text)

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
