package com.truyencv.app.service.tts

import com.truyencv.app.util.TextSanitizer

/**
 * Trạng thái hoạt động của trình phát Text-to-Speech (TTS).
 */
enum class TtsStatus {
    IDLE,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR
}

/**
 * Dữ liệu trạng thái thời gian thực của TTS service (SRS §5.5 & §5.7).
 */
data class TtsState(
    val status: TtsStatus = TtsStatus.IDLE,
    val novelId: String = "",
    val novelTitle: String = "",
    val chapterId: String = "",
    val chapterTitle: String = "",
    val currentParagraphIndex: Int = 0,
    val totalParagraphs: Int = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val errorMessage: String? = null
) {
    val isPlaying: Boolean get() = status == TtsStatus.PLAYING
    val isPaused: Boolean get() = status == TtsStatus.PAUSED
    val isIdle: Boolean get() = status == TtsStatus.IDLE
    val isStopped: Boolean get() = status == TtsStatus.STOPPED
    val isError: Boolean get() = status == TtsStatus.ERROR

    /**
     * Tỷ lệ phần trăm tiến độ đọc trong chương (0.0f -> 1.0f).
     */
    val progressPercent: Float
        get() = if (totalParagraphs > 0) {
            ((currentParagraphIndex + 1).toFloat() / totalParagraphs.toFloat()).coerceIn(0f, 1f)
        } else 0f
}

/**
 * Định nghĩa các Intent Actions và Extras cho TTS Foreground Service.
 */
object TtsConstants {
    const val ACTION_START = "com.truyencv.app.action.TTS_START"
    const val ACTION_PAUSE = "com.truyencv.app.action.TTS_PAUSE"
    const val ACTION_RESUME = "com.truyencv.app.action.TTS_RESUME"
    const val ACTION_STOP = "com.truyencv.app.action.TTS_STOP"
    const val ACTION_NEXT_PARAGRAPH = "com.truyencv.app.action.TTS_NEXT_PARAGRAPH"
    const val ACTION_PREV_PARAGRAPH = "com.truyencv.app.action.TTS_PREV_PARAGRAPH"
    const val ACTION_SET_SPEED = "com.truyencv.app.action.TTS_SET_SPEED"
    const val ACTION_SET_PITCH = "com.truyencv.app.action.TTS_SET_PITCH"
    const val ACTION_NEXT_CHAPTER = "com.truyencv.app.action.TTS_NEXT_CHAPTER"

    const val EXTRA_NOVEL_ID = "extra_novel_id"
    const val EXTRA_NOVEL_TITLE = "extra_novel_title"
    const val EXTRA_CHAPTER_ID = "extra_chapter_id"
    const val EXTRA_CHAPTER_TITLE = "extra_chapter_title"
    const val EXTRA_CONTENT = "extra_content"
    const val EXTRA_SPEED = "extra_speed"
    const val EXTRA_PITCH = "extra_pitch"
    const val EXTRA_PARAGRAPH_INDEX = "extra_paragraph_index"
}

// Top-level aliases for direct imports
const val ACTION_START = TtsConstants.ACTION_START
const val ACTION_PAUSE = TtsConstants.ACTION_PAUSE
const val ACTION_RESUME = TtsConstants.ACTION_RESUME
const val ACTION_STOP = TtsConstants.ACTION_STOP
const val ACTION_NEXT_PARAGRAPH = TtsConstants.ACTION_NEXT_PARAGRAPH
const val ACTION_PREV_PARAGRAPH = TtsConstants.ACTION_PREV_PARAGRAPH
const val ACTION_SET_SPEED = TtsConstants.ACTION_SET_SPEED
const val ACTION_SET_PITCH = TtsConstants.ACTION_SET_PITCH
const val ACTION_NEXT_CHAPTER = TtsConstants.ACTION_NEXT_CHAPTER

const val EXTRA_NOVEL_ID = TtsConstants.EXTRA_NOVEL_ID
const val EXTRA_NOVEL_TITLE = TtsConstants.EXTRA_NOVEL_TITLE
const val EXTRA_CHAPTER_ID = TtsConstants.EXTRA_CHAPTER_ID
const val EXTRA_CHAPTER_TITLE = TtsConstants.EXTRA_CHAPTER_TITLE
const val EXTRA_CONTENT = TtsConstants.EXTRA_CONTENT
const val EXTRA_SPEED = TtsConstants.EXTRA_SPEED
const val EXTRA_PITCH = TtsConstants.EXTRA_PITCH
const val EXTRA_PARAGRAPH_INDEX = TtsConstants.EXTRA_PARAGRAPH_INDEX

/**
 * Tiện ích tiền xử lý và phân tách văn bản chương truyện trước khi gửi đến TTS Engine.
 */
object TtsTextSplitter {

    /**
     * Làm sạch HTML từ [content] bằng [TextSanitizer.cleanHtml],
     * sau đó phân tách theo ngắt dòng và dấu câu thành danh sách đoạn/câu đọc tự nhiên.
     */
    fun splitIntoParagraphs(content: String?): List<String> {
        if (content.isNullOrBlank()) return emptyList()
        val cleaned = TextSanitizer.cleanHtml(content)
        if (cleaned.isBlank()) return emptyList()

        val rawLines = cleaned.split(Regex("(\\r?\\n)+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val paragraphs = mutableListOf<String>()
        for (line in rawLines) {
            if (line.length <= 150) {
                paragraphs.add(line)
            } else {
                // Tách theo dấu kết thúc câu (. ! ? … hoặc chấm than/hỏi)
                val sentences = line.split(Regex("(?<=[.!?…])\\s+"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                if (sentences.isNotEmpty()) {
                    paragraphs.addAll(sentences)
                } else {
                    paragraphs.add(line)
                }
            }
        }
        return paragraphs
    }
}
