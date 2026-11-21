package com.truyencv.app.service

import com.truyencv.app.service.download.DownloadNotificationHelper
import com.truyencv.app.service.download.DownloadProgressState
import com.truyencv.app.service.download.NovelDownloadService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelDownloadServiceTest {

    @Test
    fun testCalculateProgressPercentage() {
        // Trường hợp thông thường
        assertEquals(0, DownloadNotificationHelper.calculateProgressPercentage(0, 100))
        assertEquals(25, DownloadNotificationHelper.calculateProgressPercentage(25, 100))
        assertEquals(50, DownloadNotificationHelper.calculateProgressPercentage(50, 100))
        assertEquals(100, DownloadNotificationHelper.calculateProgressPercentage(100, 100))

        // Làm tròn số nguyên
        assertEquals(33, DownloadNotificationHelper.calculateProgressPercentage(1, 3))
        assertEquals(66, DownloadNotificationHelper.calculateProgressPercentage(2, 3))

        // Biên 0 mẫu số
        assertEquals(0, DownloadNotificationHelper.calculateProgressPercentage(0, 0))
        assertEquals(0, DownloadNotificationHelper.calculateProgressPercentage(10, 0))
        assertEquals(0, DownloadNotificationHelper.calculateProgressPercentage(10, -5))

        // Vượt quá tổng (coerce 100)
        assertEquals(100, DownloadNotificationHelper.calculateProgressPercentage(150, 100))
    }

    @Test
    fun testDownloadActionConstants() {
        val actions = listOf(
            DownloadNotificationHelper.ACTION_START_DOWNLOAD,
            DownloadNotificationHelper.ACTION_PAUSE_DOWNLOAD,
            DownloadNotificationHelper.ACTION_RESUME_DOWNLOAD,
            DownloadNotificationHelper.ACTION_CANCEL_DOWNLOAD
        )

        // Không rỗng và không trùng lặp
        assertEquals(4, actions.distinct().size)
        assertTrue(actions.all { it.isNotBlank() })
    }

    @Test
    fun testDownloadExtrasConstants() {
        assertNotNull(DownloadNotificationHelper.EXTRA_NOVEL_ID)
        assertNotNull(DownloadNotificationHelper.EXTRA_NOVEL_TITLE)
        assertNotNull(DownloadNotificationHelper.EXTRA_THUMB_URL)
        assertNotNull(DownloadNotificationHelper.EXTRA_START_ORDER)
        assertNotNull(DownloadNotificationHelper.EXTRA_END_ORDER)
    }

    @Test
    fun testBase64EncodingAndDataUriFormatting() {
        val testData = "TruyenCV Offline Download Engine Cover Test".toByteArray(Charsets.UTF_8)

        val encoded = NovelDownloadService.encodeBase64(testData)
        assertNotNull(encoded)
        assertTrue(encoded.isNotBlank())
        assertFalse(encoded.contains("\n"))

        val dataUri = NovelDownloadService.formatBase64DataUri(testData, "image/jpeg")
        assertTrue(dataUri.startsWith("data:image/jpeg;base64,"))
        assertTrue(dataUri.endsWith(encoded))
    }

    @Test
    fun testDownloadRetryBackoffDelay() {
        assertEquals(300L, NovelDownloadService.calculateRetryDelay(1))
        assertEquals(600L, NovelDownloadService.calculateRetryDelay(2))
        assertEquals(900L, NovelDownloadService.calculateRetryDelay(3))

        // Biên <= 0
        assertEquals(300L, NovelDownloadService.calculateRetryDelay(0))
        assertEquals(300L, NovelDownloadService.calculateRetryDelay(-1))
    }

    @Test
    fun testDownloadProgressStateModel() {
        val initial = DownloadProgressState()
        assertEquals("", initial.novelId)
        assertEquals(0, initial.downloadedChapters)
        assertEquals(0, initial.totalChapters)
        assertEquals("IDLE", initial.status)
        assertEquals(0, initial.progressPercentage)

        val progress = initial.copy(
            novelId = "tcv-555",
            novelTitle = "Mục Thần Ký",
            downloadedChapters = 45,
            totalChapters = 90,
            status = "DOWNLOADING"
        )
        assertEquals(50, progress.progressPercentage)
        assertEquals("DOWNLOADING", progress.status)

        val completed = progress.copy(
            downloadedChapters = 90,
            status = "COMPLETED"
        )
        assertEquals(100, completed.progressPercentage)
        assertEquals("COMPLETED", completed.status)
    }
}
