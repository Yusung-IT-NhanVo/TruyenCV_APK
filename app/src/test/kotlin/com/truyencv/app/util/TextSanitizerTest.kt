package com.truyencv.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextSanitizerTest {

    @Test
    fun testCleanHtmlStripsTagsAndEntities() {
        val input = "<p>Đại Quản Gia <strong>Là</strong> Ma Hoàng &amp; Tiên Vương</p><br/>&nbsp;&quot;Hay&quot;"
        val result = TextSanitizer.cleanHtml(input)
        assertEquals("Đại Quản Gia Là Ma Hoàng & Tiên Vương \"Hay\"", result)
    }

    @Test
    fun testUnescapeVietnameseHtmlEntities() {
        val input = "Li&ecirc;n tiếp bị đ&aacute;nh bại 147 tr&agrave;ng nam nh&acirc;n, cập nhật l&uacute;c 2012"
        val result = TextSanitizer.cleanHtml(input)
        assertEquals("Liên tiếp bị đánh bại 147 tràng nam nhân, cập nhật lúc 2012", result)

        val numericInput = "&#76;i&#234;n ti&#7871;p"
        val numericResult = TextSanitizer.cleanHtml(numericInput)
        assertEquals("Liên tiếp", numericResult)
    }

    @Test
    fun testCleanHtmlNullOrEmpty() {
        assertEquals("", TextSanitizer.cleanHtml(null))
        assertEquals("", TextSanitizer.cleanHtml(""))
        assertEquals("", TextSanitizer.cleanHtml("   "))
    }

    @Test
    fun testSanitizeCoverUrlWithDoubleSlash() {
        val malformedUrl = "https://static.cdnno.com/poster//150.jpg"
        val fixed = TextSanitizer.sanitizeCoverUrl(malformedUrl)
        assertEquals(TextSanitizer.DEFAULT_COVER_URL, fixed)

        val validUrl = "https://static.cdnno.com/poster/150.jpg"
        assertEquals(validUrl, TextSanitizer.sanitizeCoverUrl(validUrl))

        assertEquals(TextSanitizer.DEFAULT_COVER_URL, TextSanitizer.sanitizeCoverUrl(null))
        assertEquals(TextSanitizer.DEFAULT_COVER_URL, TextSanitizer.sanitizeCoverUrl(""))
    }

    @Test
    fun testUnescapeSlashes() {
        val escaped = "Tác giả: \\\"Dạ Kiêu\\\", thể loại: \\'Tiên Hiệp\\', đường dẫn: c:\\\\temp"
        val result = TextSanitizer.unescapeSlashes(escaped)
        assertEquals("Tác giả: \"Dạ Kiêu\", thể loại: 'Tiên Hiệp', đường dẫn: c:\\temp", result)
    }

    @Test
    fun testRemoveDuplicateChapterHeader() {
        val contentWithHeader = "<p>Chương 100: Đại Chiến Đỉnh Phong</p>\nNội dung chính của chương truyện tại đây..."
        val result = TextSanitizer.removeDuplicateChapterHeader(contentWithHeader)
        assertEquals("Nội dung chính của chương truyện tại đây...", result)

        val contentWithBr = "Chương 5: Bắt đầu hành trình<br><br/>Nội dung chương 5"
        val resultBr = TextSanitizer.removeDuplicateChapterHeader(contentWithBr)
        assertEquals("Nội dung chương 5", resultBr)

        val normalContent = "Nội dung chương không có tiêu đề lặp ở đầu dòng"
        assertEquals(normalContent, TextSanitizer.removeDuplicateChapterHeader(normalContent))
    }

    @Test
    fun testSanitizeChapterContent() {
        val input = "<p>Chương 1: Khởi đầu</p><br/>Hắn nói: \\\"Ta đã trở lại!\\\""
        val result = TextSanitizer.sanitizeChapterContent(input)
        assertEquals("Hắn nói: \"Ta đã trở lại!\"", result)
    }
}
