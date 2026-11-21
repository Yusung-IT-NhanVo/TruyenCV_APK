package com.truyencv.app.service.tts

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Android Foreground Service thực hiện đọc truyện bằng giọng nói chạy nền (SRS §5.5 & §5.7).
 * Sử dụng android.speech.tts.TextToSpeech với kiểu FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK.
 */
class TtsForegroundService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var isPendingStart = false

    private var paragraphs: List<String> = emptyList()
    private var currentParagraphIndex: Int = 0
    private var currentUtteranceId: String? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        TtsNotificationHelper.createNotificationChannel(this)
        tts = TextToSpeech(applicationContext, this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_START -> handleStartAction(intent)
            ACTION_PAUSE -> handlePauseAction()
            ACTION_RESUME -> handleResumeAction()
            ACTION_STOP -> handleStopAction()
            ACTION_NEXT_PARAGRAPH -> handleNextParagraphAction()
            ACTION_PREV_PARAGRAPH -> handlePrevParagraphAction()
            ACTION_SET_SPEED -> handleSetSpeedAction(intent)
            ACTION_SET_PITCH -> handleSetPitchAction(intent)
            ACTION_NEXT_CHAPTER -> handleNextChapterAction(intent)
        }

        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            setupTtsEngine()

            if (isPendingStart) {
                isPendingStart = false
                speakCurrentParagraph()
            }
        } else {
            isTtsInitialized = false
            _serviceState.value = _serviceState.value.copy(
                status = TtsStatus.ERROR,
                errorMessage = "Khởi tạo công cụ TTS thất bại ($status)"
            )
        }
    }

    private fun setupTtsEngine() {
        val engine = tts ?: return

        // Cấu hình ngôn ngữ tiếng Việt (hoặc ngôn ngữ mặc định nếu thiếu)
        var langResult = engine.setLanguage(Locale("vi", "VN"))
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            langResult = engine.setLanguage(Locale("vi"))
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                engine.setLanguage(Locale.getDefault())
            }
        }

        engine.setSpeechRate(_serviceState.value.speed)
        engine.setPitch(_serviceState.value.pitch)

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                mainHandler.post {
                    _serviceState.value = _serviceState.value.copy(status = TtsStatus.PLAYING)
                    val text = paragraphs.getOrNull(currentParagraphIndex)
                    TtsNotificationHelper.updateNotification(
                        this@TtsForegroundService,
                        _serviceState.value,
                        text
                    )
                }
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId != currentUtteranceId) return
                mainHandler.post {
                    playNextParagraph()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                handleTtsError("Lỗi đọc văn bản TTS")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                handleTtsError("Lỗi đọc TTS (mã lỗi: $errorCode)")
            }
        })
    }

    private fun handleStartAction(intent: Intent) {
        val novelId = intent.getStringExtra(EXTRA_NOVEL_ID) ?: ""
        val novelTitle = intent.getStringExtra(EXTRA_NOVEL_TITLE) ?: ""
        val chapterId = intent.getStringExtra(EXTRA_CHAPTER_ID) ?: ""
        val chapterTitle = intent.getStringExtra(EXTRA_CHAPTER_TITLE) ?: ""
        val content = intent.getStringExtra(EXTRA_CONTENT)
        val speed = intent.getFloatExtra(EXTRA_SPEED, _serviceState.value.speed)
        val pitch = intent.getFloatExtra(EXTRA_PITCH, _serviceState.value.pitch)
        val startIndex = intent.getIntExtra(EXTRA_PARAGRAPH_INDEX, 0)

        if (!content.isNullOrBlank()) {
            paragraphs = TtsTextSplitter.splitIntoParagraphs(content)
        }
        currentParagraphIndex = startIndex.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))

        _serviceState.value = _serviceState.value.copy(
            status = TtsStatus.PLAYING,
            novelId = novelId,
            novelTitle = novelTitle,
            chapterId = chapterId,
            chapterTitle = chapterTitle,
            currentParagraphIndex = currentParagraphIndex,
            totalParagraphs = paragraphs.size,
            speed = speed,
            pitch = pitch,
            errorMessage = null
        )

        // Bắt đầu Foreground Service
        startForegroundWithNotification()

        if (isTtsInitialized) {
            tts?.setSpeechRate(speed)
            tts?.setPitch(pitch)
            speakCurrentParagraph()
        } else {
            isPendingStart = true
        }
    }

    private fun handlePauseAction() {
        tts?.stop()
        _serviceState.value = _serviceState.value.copy(status = TtsStatus.PAUSED)
        val currentText = paragraphs.getOrNull(currentParagraphIndex)
        TtsNotificationHelper.updateNotification(this, _serviceState.value, currentText)
    }

    private fun handleResumeAction() {
        if (_serviceState.value.status == TtsStatus.PAUSED || _serviceState.value.status == TtsStatus.IDLE) {
            speakCurrentParagraph()
        }
    }

    private fun handleStopAction() {
        stopPlaybackAndExit()
    }

    private fun handleNextParagraphAction() {
        if (currentParagraphIndex + 1 < paragraphs.size) {
            currentParagraphIndex++
            speakCurrentParagraph()
        }
    }

    private fun handlePrevParagraphAction() {
        if (currentParagraphIndex > 0) {
            currentParagraphIndex--
            speakCurrentParagraph()
        }
    }

    private fun handleSetSpeedAction(intent: Intent) {
        val speed = intent.getFloatExtra(EXTRA_SPEED, 1.0f).coerceIn(0.5f, 2.0f)
        _serviceState.value = _serviceState.value.copy(speed = speed)
        tts?.setSpeechRate(speed)
    }

    private fun handleSetPitchAction(intent: Intent) {
        val pitch = intent.getFloatExtra(EXTRA_PITCH, 1.0f).coerceIn(0.5f, 2.0f)
        _serviceState.value = _serviceState.value.copy(pitch = pitch)
        tts?.setPitch(pitch)
    }

    private fun handleNextChapterAction(intent: Intent) {
        handleStartAction(intent)
    }

    private fun speakCurrentParagraph() {
        if (!isTtsInitialized || paragraphs.isEmpty()) return

        if (currentParagraphIndex !in paragraphs.indices) {
            // Đã đọc hết danh sách đoạn
            _serviceState.value = _serviceState.value.copy(status = TtsStatus.IDLE)
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            return
        }

        val text = paragraphs[currentParagraphIndex]
        val utteranceId = "tts_${_serviceState.value.chapterId}_$currentParagraphIndex"
        currentUtteranceId = utteranceId

        val params = Bundle()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

        _serviceState.value = _serviceState.value.copy(
            status = TtsStatus.PLAYING,
            currentParagraphIndex = currentParagraphIndex,
            totalParagraphs = paragraphs.size,
            errorMessage = null
        )

        TtsNotificationHelper.updateNotification(this, _serviceState.value, text)
    }

    private fun playNextParagraph() {
        if (currentParagraphIndex + 1 < paragraphs.size) {
            currentParagraphIndex++
            speakCurrentParagraph()
        } else {
            // Đã hoàn thành chương hiện tại
            _serviceState.value = _serviceState.value.copy(
                status = TtsStatus.IDLE,
                currentParagraphIndex = paragraphs.size - 1
            )
            val currentText = paragraphs.getOrNull(currentParagraphIndex)
            TtsNotificationHelper.updateNotification(this, _serviceState.value, currentText)
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        }
    }

    private fun handleTtsError(message: String) {
        mainHandler.post {
            _serviceState.value = _serviceState.value.copy(
                status = TtsStatus.ERROR,
                errorMessage = message
            )
            TtsNotificationHelper.updateNotification(this, _serviceState.value, message)
        }
    }

    private fun startForegroundWithNotification() {
        val currentText = paragraphs.getOrNull(currentParagraphIndex)
        val notification = TtsNotificationHelper.buildNotification(
            this,
            _serviceState.value,
            currentText
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                TtsNotificationHelper.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(TtsNotificationHelper.NOTIFICATION_ID, notification)
        }
    }

    private fun stopPlaybackAndExit() {
        tts?.stop()
        _serviceState.value = _serviceState.value.copy(status = TtsStatus.STOPPED)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsInitialized = false
        serviceScope.cancel()
        _serviceState.value = TtsState(status = TtsStatus.IDLE)
    }

    companion object {
        private val _serviceState = MutableStateFlow(TtsState())
        val serviceState: StateFlow<TtsState> = _serviceState.asStateFlow()

        /**
         * Tiện ích gọi Service bắt đầu phát TTS cho một chương truyện.
         */
        fun start(
            context: Context,
            novelId: String,
            novelTitle: String,
            chapterId: String,
            chapterTitle: String,
            content: String,
            speed: Float = 1.0f,
            pitch: Float = 1.0f,
            startParagraphIndex: Int = 0
        ) {
            val intent = Intent(context, TtsForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_NOVEL_ID, novelId)
                putExtra(EXTRA_NOVEL_TITLE, novelTitle)
                putExtra(EXTRA_CHAPTER_ID, chapterId)
                putExtra(EXTRA_CHAPTER_TITLE, chapterTitle)
                putExtra(EXTRA_CONTENT, content)
                putExtra(EXTRA_SPEED, speed)
                putExtra(EXTRA_PITCH, pitch)
                putExtra(EXTRA_PARAGRAPH_INDEX, startParagraphIndex)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pause(context: Context) {
            sendCommand(context, ACTION_PAUSE)
        }

        fun resume(context: Context) {
            sendCommand(context, ACTION_RESUME)
        }

        fun stop(context: Context) {
            sendCommand(context, ACTION_STOP)
        }

        fun nextParagraph(context: Context) {
            sendCommand(context, ACTION_NEXT_PARAGRAPH)
        }

        fun prevParagraph(context: Context) {
            sendCommand(context, ACTION_PREV_PARAGRAPH)
        }

        fun setSpeed(context: Context, speed: Float) {
            val intent = Intent(context, TtsForegroundService::class.java).apply {
                action = ACTION_SET_SPEED
                putExtra(EXTRA_SPEED, speed)
            }
            context.startService(intent)
        }

        fun setPitch(context: Context, pitch: Float) {
            val intent = Intent(context, TtsForegroundService::class.java).apply {
                action = ACTION_SET_PITCH
                putExtra(EXTRA_PITCH, pitch)
            }
            context.startService(intent)
        }

        private fun sendCommand(context: Context, action: String) {
            val intent = Intent(context, TtsForegroundService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }
    }
}
