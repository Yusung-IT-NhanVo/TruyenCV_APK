package com.truyencv.app.service

import com.truyencv.app.service.tts.ACTION_NEXT_CHAPTER
import com.truyencv.app.service.tts.ACTION_NEXT_PARAGRAPH
import com.truyencv.app.service.tts.ACTION_PAUSE
import com.truyencv.app.service.tts.ACTION_PREV_PARAGRAPH
import com.truyencv.app.service.tts.ACTION_RESUME
import com.truyencv.app.service.tts.ACTION_SET_PITCH
import com.truyencv.app.service.tts.ACTION_SET_SPEED
import com.truyencv.app.service.tts.ACTION_START
import com.truyencv.app.service.tts.ACTION_STOP
import com.truyencv.app.service.tts.EXTRA_CHAPTER_ID
import com.truyencv.app.service.tts.EXTRA_CHAPTER_TITLE
import com.truyencv.app.service.tts.EXTRA_CONTENT
import com.truyencv.app.service.tts.EXTRA_NOVEL_ID
import com.truyencv.app.service.tts.EXTRA_NOVEL_TITLE
import com.truyencv.app.service.tts.EXTRA_SPEED
import com.truyencv.app.service.tts.TtsState
import com.truyencv.app.service.tts.TtsStatus
import com.truyencv.app.service.tts.TtsTextSplitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TtsPlayerStateTest {

    @Test
    fun testInitialStateDefaults() {
        val state = TtsState()
        assertEquals(TtsStatus.IDLE, state.status)
        assertEquals("", state.novelId)
        assertEquals("", state.novelTitle)
        assertEquals("", state.chapterId)
        assertEquals("", state.chapterTitle)
        assertEquals(0, state.currentParagraphIndex)
        assertEquals(0, state.totalParagraphs)
        assertEquals(1.0f, state.speed, 0.001f)
        assertEquals(1.0f, state.pitch, 0.001f)
        assertNull(state.errorMessage)

        assertTrue(state.isIdle)
        assertFalse(state.isPlaying)
        assertFalse(state.isPaused)
        assertFalse(state.isStopped)
        assertFalse(state.isError)
        assertEquals(0f, state.progressPercent, 0.001f)
    }

    @Test
    fun testStateTransitionsLifecycle() {
        var state = TtsState(
            novelId = "tcv-100",
            novelTitle = "Toàn Chức Cao Thủ",
            chapterId = "tcv-ch-1",
            chapterTitle = "Chương 1: Diệp Tu",
            totalParagraphs = 20
        )
        assertTrue(state.isIdle)

        // IDLE -> PLAYING
        state = state.copy(status = TtsStatus.PLAYING, currentParagraphIndex = 0)
        assertTrue(state.isPlaying)
        assertFalse(state.isPaused)
        assertEquals(0.05f, state.progressPercent, 0.001f)

        // PLAYING -> PAUSED
        state = state.copy(status = TtsStatus.PAUSED, currentParagraphIndex = 9)
        assertTrue(state.isPaused)
        assertFalse(state.isPlaying)
        assertEquals(0.5f, state.progressPercent, 0.001f)

        // PAUSED -> PLAYING (Resume)
        state = state.copy(status = TtsStatus.PLAYING)
        assertTrue(state.isPlaying)

        // PLAYING -> STOPPED
        state = state.copy(status = TtsStatus.STOPPED)
        assertTrue(state.isStopped)
        assertFalse(state.isPlaying)

        // Any -> ERROR
        state = state.copy(status = TtsStatus.ERROR, errorMessage = "Thiếu gói ngôn ngữ")
        assertTrue(state.isError)
        assertEquals("Thiếu gói ngôn ngữ", state.errorMessage)
    }

    @Test
    fun testProgressPercentCalculation() {
        val emptyState = TtsState(totalParagraphs = 0)
        assertEquals(0f, emptyState.progressPercent, 0.001f)

        val halfState = TtsState(currentParagraphIndex = 4, totalParagraphs = 10)
        assertEquals(0.5f, halfState.progressPercent, 0.001f)

        val completedState = TtsState(currentParagraphIndex = 9, totalParagraphs = 10)
        assertEquals(1.0f, completedState.progressPercent, 0.001f)

        // Kiểm tra chặn giới hạn (coerceIn 0..1)
        val overflowState = TtsState(currentParagraphIndex = 15, totalParagraphs = 10)
        assertEquals(1.0f, overflowState.progressPercent, 0.001f)
    }

    @Test
    fun testSplitTextIntoParagraphsWithHtmlAndEntities() {
        val rawHtml = """
            <p>Trần Dạ mở mắt ra, nhìn thấy trần nhà bằng gỗ mộc mạc &amp; cổ kính.</p>
            <p>&quot;Đây là đâu?&quot; Hắn tự lẩm bẩm trong đầu.&nbsp;Ký ức ùa về như thủy triều.</p>
            <br/>
            <p>Hắn nhớ rõ mình vừa bị một đạo lôi kiếp đánh trúng khi độ kiếp phi thăng.</p>
        """.trimIndent()

        val paragraphs = TtsTextSplitter.splitIntoParagraphs(rawHtml)
        assertEquals(3, paragraphs.size)
        assertEquals(
            "Trần Dạ mở mắt ra, nhìn thấy trần nhà bằng gỗ mộc mạc & cổ kính.",
            paragraphs[0]
        )
        assertEquals(
            "\"Đây là đâu?\" Hắn tự lẩm bẩm trong đầu. Ký ức ùa về như thủy triều.",
            paragraphs[1]
        )
        assertEquals(
            "Hắn nhớ rõ mình vừa bị một đạo lôi kiếp đánh trúng khi độ kiếp phi thăng.",
            paragraphs[2]
        )
    }

    @Test
    fun testSplitTextEmptyOrNull() {
        assertTrue(TtsTextSplitter.splitIntoParagraphs(null).isEmpty())
        assertTrue(TtsTextSplitter.splitIntoParagraphs("").isEmpty())
        assertTrue(TtsTextSplitter.splitIntoParagraphs("   \n\n   ").isEmpty())
    }

    @Test
    fun testSpeedAndPitchUpdates() {
        val state = TtsState(speed = 1.0f, pitch = 1.0f)
        val fasterState = state.copy(speed = 1.5f, pitch = 1.2f)

        assertEquals(1.5f, fasterState.speed, 0.001f)
        assertEquals(1.2f, fasterState.pitch, 0.001f)

        val clampedState = state.copy(
            speed = 2.5f.coerceIn(0.5f, 2.0f),
            pitch = 0.2f.coerceIn(0.5f, 2.0f)
        )
        assertEquals(2.0f, clampedState.speed, 0.001f)
        assertEquals(0.5f, clampedState.pitch, 0.001f)
    }

    @Test
    fun testIntentActionConstants() {
        assertEquals("com.truyencv.app.action.TTS_START", ACTION_START)
        assertEquals("com.truyencv.app.action.TTS_PAUSE", ACTION_PAUSE)
        assertEquals("com.truyencv.app.action.TTS_RESUME", ACTION_RESUME)
        assertEquals("com.truyencv.app.action.TTS_STOP", ACTION_STOP)
        assertEquals("com.truyencv.app.action.TTS_NEXT_PARAGRAPH", ACTION_NEXT_PARAGRAPH)
        assertEquals("com.truyencv.app.action.TTS_PREV_PARAGRAPH", ACTION_PREV_PARAGRAPH)
        assertEquals("com.truyencv.app.action.TTS_SET_SPEED", ACTION_SET_SPEED)
        assertEquals("com.truyencv.app.action.TTS_SET_PITCH", ACTION_SET_PITCH)
        assertEquals("com.truyencv.app.action.TTS_NEXT_CHAPTER", ACTION_NEXT_CHAPTER)

        assertNotNull(EXTRA_NOVEL_ID)
        assertNotNull(EXTRA_NOVEL_TITLE)
        assertNotNull(EXTRA_CHAPTER_ID)
        assertNotNull(EXTRA_CHAPTER_TITLE)
        assertNotNull(EXTRA_CONTENT)
        assertNotNull(EXTRA_SPEED)
    }
}
