package com.truyencv.app.service.download

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.truyencv.app.TruyenCVApp
import com.truyencv.app.data.local.db.DownloadTaskEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.service.download.DownloadNotificationHelper.ACTION_CANCEL_DOWNLOAD
import com.truyencv.app.service.download.DownloadNotificationHelper.ACTION_PAUSE_DOWNLOAD
import com.truyencv.app.service.download.DownloadNotificationHelper.ACTION_RESUME_DOWNLOAD
import com.truyencv.app.service.download.DownloadNotificationHelper.ACTION_START_DOWNLOAD
import com.truyencv.app.service.download.DownloadNotificationHelper.EXTRA_END_ORDER
import com.truyencv.app.service.download.DownloadNotificationHelper.EXTRA_NOVEL_ID
import com.truyencv.app.service.download.DownloadNotificationHelper.EXTRA_NOVEL_TITLE
import com.truyencv.app.service.download.DownloadNotificationHelper.EXTRA_START_ORDER
import com.truyencv.app.service.download.DownloadNotificationHelper.EXTRA_THUMB_URL
import com.truyencv.app.service.download.DownloadNotificationHelper.NOTIFICATION_ID
import com.truyencv.app.util.TextSanitizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Trạng thái tiến trình tải truyện để UI quan sát theo thời gian thực.
 */
data class DownloadProgressState(
    val novelId: String = "",
    val novelTitle: String = "",
    val downloadedChapters: Int = 0,
    val totalChapters: Int = 0,
    val status: String = "IDLE", // IDLE, DOWNLOADING, PAUSED, COMPLETED, CANCELLED, ERROR
    val errorMessage: String? = null
) {
    val progressPercentage: Int
        get() = DownloadNotificationHelper.calculateProgressPercentage(downloadedChapters, totalChapters)
}

/**
 * Android Foreground Service thực hiện tải truyện offline hàng loạt (SRS §5.7 & §5.9).
 * Sử dụng FOREGROUND_SERVICE_TYPE_DATA_SYNC.
 */
class NovelDownloadService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var downloadJob: Job? = null

    @Volatile
    private var isPaused: Boolean = false

    @Volatile
    private var isCancelled: Boolean = false

    private var activeNovelId: String = ""
    private var activeNovelTitle: String = ""

    override fun onCreate() {
        super.onCreate()
        DownloadNotificationHelper.createNotificationChannel(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_START_DOWNLOAD -> handleStartDownload(intent)
            ACTION_PAUSE_DOWNLOAD -> handlePauseDownload()
            ACTION_RESUME_DOWNLOAD -> handleResumeDownload()
            ACTION_CANCEL_DOWNLOAD -> handleCancelDownload()
        }

        return START_NOT_STICKY
    }

    private fun handleStartDownload(intent: Intent) {
        val novelId = intent.getStringExtra(EXTRA_NOVEL_ID) ?: return
        val novelTitle = intent.getStringExtra(EXTRA_NOVEL_TITLE) ?: "Truyện"
        val thumbUrl = intent.getStringExtra(EXTRA_THUMB_URL) ?: ""
        val startOrder = intent.getIntExtra(EXTRA_START_ORDER, 1)
        val endOrder = intent.getIntExtra(EXTRA_END_ORDER, Int.MAX_VALUE)

        activeNovelId = novelId
        activeNovelTitle = novelTitle
        isPaused = false
        isCancelled = false

        // Khởi động Foreground Service ngay lập tức
        val initialNotif = DownloadNotificationHelper.buildProgressNotification(
            this, novelId, novelTitle, 0, 0, false
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                initialNotif,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, initialNotif)
        }

        // Hủy job cũ nếu đang chạy
        downloadJob?.cancel()
        downloadJob = serviceScope.launch {
            executeDownloadPipeline(novelId, novelTitle, thumbUrl, startOrder, endOrder)
        }
    }

    private fun handlePauseDownload() {
        isPaused = true
        _downloadState.value = _downloadState.value.copy(status = "PAUSED")
        DownloadNotificationHelper.updateProgress(
            this,
            activeNovelId,
            activeNovelTitle,
            _downloadState.value.downloadedChapters,
            _downloadState.value.totalChapters,
            isPaused = true
        )

        serviceScope.launch {
            try {
                val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
                app.container.database.downloadTaskDao().updateProgress(
                    activeNovelId,
                    _downloadState.value.downloadedChapters,
                    "PAUSED"
                )
            } catch (_: Exception) {
            }
        }
    }

    private fun handleResumeDownload() {
        if (!isPaused) return
        isPaused = false
        _downloadState.value = _downloadState.value.copy(status = "DOWNLOADING")
        DownloadNotificationHelper.updateProgress(
            this,
            activeNovelId,
            activeNovelTitle,
            _downloadState.value.downloadedChapters,
            _downloadState.value.totalChapters,
            isPaused = false
        )

        serviceScope.launch {
            try {
                val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
                app.container.database.downloadTaskDao().updateProgress(
                    activeNovelId,
                    _downloadState.value.downloadedChapters,
                    "DOWNLOADING"
                )
            } catch (_: Exception) {
            }
        }
    }

    private fun handleCancelDownload() {
        isCancelled = true
        isPaused = false
        downloadJob?.cancel()

        _downloadState.value = _downloadState.value.copy(status = "CANCELLED")

        serviceScope.launch {
            try {
                val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
                app.container.database.downloadTaskDao().updateProgress(
                    activeNovelId,
                    _downloadState.value.downloadedChapters,
                    "CANCELLED"
                )
            } catch (_: Exception) {
            } finally {
                withContext(Dispatchers.Main) {
                    ServiceCompat.stopForeground(
                        this@NovelDownloadService,
                        ServiceCompat.STOP_FOREGROUND_REMOVE
                    )
                    stopSelf()
                }
            }
        }
    }

    /**
     * Quy trình tải offline tuần tự:
     * 1. Cache ảnh bìa sang Base64 trong SQLite.
     * 2. Lấy danh sách chương & áp dụng Idempotency (bỏ qua chương đã tải).
     * 3. Tải nội dung từng chương với cơ chế thử lại (exponential backoff retry).
     * 4. Cooldown 250ms giữa các chương để tránh Cloudflare / chặn IP.
     * 5. Cập nhật SQLite & Notification hoàn thành.
     */
    private suspend fun executeDownloadPipeline(
        novelId: String,
        novelTitle: String,
        thumbUrl: String,
        startOrder: Int,
        endOrder: Int
    ) {
        val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
        val container = app.container
        val novelDao = container.database.novelDao()
        val chapterDao = container.database.chapterDao()
        val downloadTaskDao = container.database.downloadTaskDao()
        val chapterRepository = container.chapterRepository

        try {
            // Bước 1: Cache ảnh bìa offline dạng Base64
            fetchAndCacheCoverImage(novelId, thumbUrl, novelDao)

            // Bước 2: Lấy danh sách chương mục tiêu
            var allChapters = chapterDao.getChaptersByOrderRange(novelId, startOrder, endOrder)
            if (allChapters.isEmpty()) {
                val remoteChapters = chapterRepository.getAllChapters(novelId)
                allChapters = remoteChapters
                    .filter { it.chapterOrder in startOrder..endOrder }
                    .map { com.truyencv.app.data.mapper.TruyenCvMapper.chapterToEntity(it) }
            }

            val totalCount = allChapters.size
            if (totalCount == 0) {
                notifyCompletion(novelId, novelTitle, 0)
                return
            }

            // Ghi nhận tác vụ tải vào Database
            downloadTaskDao.insertOrReplace(
                DownloadTaskEntity(
                    novelId = novelId,
                    novelTitle = novelTitle,
                    thumbUrl = thumbUrl,
                    totalChapters = totalCount,
                    downloadedChapters = 0,
                    status = "DOWNLOADING"
                )
            )

            _downloadState.value = DownloadProgressState(
                novelId = novelId,
                novelTitle = novelTitle,
                downloadedChapters = 0,
                totalChapters = totalCount,
                status = "DOWNLOADING"
            )

            // Bước 3: Lọc Idempotency - lấy danh sách ID đã tải
            val alreadyDownloadedIds = chapterDao.getDownloadedChapterIds(novelId).toSet()
            var downloadedCount = allChapters.count { it.id in alreadyDownloadedIds && !it.content.isNullOrBlank() }

            // Bước 4: Tải từng chương còn thiếu
            for (chapter in allChapters) {
                if (isCancelled) break

                // Chờ khi đang tạm dừng
                while (isPaused && !isCancelled) {
                    delay(500)
                }
                if (isCancelled) break

                // Bỏ qua nếu chương đã tải đủ nội dung
                if (chapter.id in alreadyDownloadedIds && !chapter.content.isNullOrBlank()) {
                    continue
                }

                // Tải nội dung với cơ chế retry
                var success = false
                var attempts = 0
                val maxAttempts = 3

                while (attempts < maxAttempts && !success && !isCancelled) {
                    attempts++
                    try {
                        val chapterContent = chapterRepository.getChapterContent(chapter.id)
                        val content = chapterContent.content
                        if (!content.isNullOrBlank()) {
                            chapterDao.updateContent(
                                id = chapter.id,
                                content = content,
                                isDownloaded = true,
                                downloadedAt = System.currentTimeMillis()
                            )
                            success = true
                        } else {
                            delay(calculateRetryDelay(attempts))
                        }
                    } catch (e: Exception) {
                        if (attempts >= maxAttempts) {
                            // Bỏ qua sang chương kế nếu quá số lần thử
                            break
                        } else {
                            delay(calculateRetryDelay(attempts))
                        }
                    }
                }

                if (success) {
                    downloadedCount++
                }

                // Cập nhật tiến độ vào SQLite và Notification
                downloadTaskDao.updateProgress(novelId, downloadedCount, "DOWNLOADING")
                _downloadState.value = _downloadState.value.copy(
                    downloadedChapters = downloadedCount,
                    totalChapters = totalCount,
                    status = "DOWNLOADING"
                )
                DownloadNotificationHelper.updateProgress(
                    this@NovelDownloadService,
                    novelId,
                    novelTitle,
                    downloadedCount,
                    totalCount,
                    isPaused = false
                )

                // Cooldown 250ms giữa các chương để bảo vệ IP
                delay(250)
            }

            if (!isCancelled) {
                notifyCompletion(novelId, novelTitle, downloadedCount)
            }

        } catch (e: Exception) {
            handleDownloadError(novelId, novelTitle, e.message ?: "Lỗi kết nối trong quá trình tải")
        }
    }

    private suspend fun fetchAndCacheCoverImage(
        novelId: String,
        thumbUrl: String,
        novelDao: NovelDao
    ) {
        if (thumbUrl.isBlank()) return
        try {
            val existingNovel = novelDao.getById(novelId)
            if (!existingNovel?.coverBase64.isNullOrBlank()) return

            val sanitizedUrl = TextSanitizer.sanitizeCoverUrl(thumbUrl)
            val url = URL(sanitizedUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.doInput = true
            connection.connect()

            if (connection.responseCode in 200..299) {
                val bytes = connection.inputStream.use { it.readBytes() }
                if (bytes.isNotEmpty()) {
                    val base64DataUri = formatBase64DataUri(bytes)
                    novelDao.updateCoverBase64(novelId, base64DataUri)
                }
            }
        } catch (_: Exception) {
            // Không làm gián đoạn tải truyện nếu chỉ lỗi ảnh bìa
        }
    }

    private suspend fun notifyCompletion(novelId: String, novelTitle: String, totalChapters: Int) {
        val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
        app.container.database.downloadTaskDao().updateProgress(novelId, totalChapters, "COMPLETED")

        _downloadState.value = _downloadState.value.copy(
            status = "COMPLETED",
            downloadedChapters = totalChapters,
            totalChapters = totalChapters
        )

        val completedNotif = DownloadNotificationHelper.buildCompletedNotification(
            this, novelId, novelTitle, totalChapters
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, completedNotif)

        withContext(Dispatchers.Main) {
            ServiceCompat.stopForeground(
                this@NovelDownloadService,
                ServiceCompat.STOP_FOREGROUND_DETACH
            )
            stopSelf()
        }
    }

    private suspend fun handleDownloadError(novelId: String, novelTitle: String, errorMsg: String) {
        val app = applicationContext as? TruyenCVApp ?: TruyenCVApp.instance
        val currentProgress = _downloadState.value.downloadedChapters
        val total = _downloadState.value.totalChapters

        app.container.database.downloadTaskDao().updateProgress(novelId, currentProgress, "ERROR")

        _downloadState.value = _downloadState.value.copy(
            status = "ERROR",
            errorMessage = errorMsg
        )

        val errorNotif = DownloadNotificationHelper.buildErrorNotification(
            this, novelId, novelTitle, currentProgress, total, errorMsg
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, errorNotif)

        withContext(Dispatchers.Main) {
            ServiceCompat.stopForeground(
                this@NovelDownloadService,
                ServiceCompat.STOP_FOREGROUND_DETACH
            )
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        serviceScope.cancel()
    }

    companion object {
        private val _downloadState = MutableStateFlow(DownloadProgressState())
        val downloadState: StateFlow<DownloadProgressState> = _downloadState.asStateFlow()

        /**
         * Mã hóa mảng bytes thành chuỗi Base64 chuẩn tương thích mọi nền tảng (Android & JVM Test).
         */
        fun encodeBase64(bytes: ByteArray): String {
            return try {
                val clazz = Class.forName("android.util.Base64")
                val method = clazz.getMethod("encodeToString", ByteArray::class.java, Int::class.javaPrimitiveType)
                method.invoke(null, bytes, 2) as String // 2 == NO_WRAP
            } catch (_: Throwable) {
                java.util.Base64.getEncoder().encodeToString(bytes)
            }
        }

        /**
         * Định dạng dữ liệu Data URI Image Base64: data:image/jpeg;base64,...
         */
        fun formatBase64DataUri(bytes: ByteArray, mimeType: String = "image/jpeg"): String {
            return "data:$mimeType;base64,${encodeBase64(bytes)}"
        }

        /**
         * Tính toán thời gian trễ khi thử lại tải (Exponential Backoff): attempt * 300ms.
         */
        fun calculateRetryDelay(attempt: Int): Long {
            return attempt.coerceAtLeast(1) * 300L
        }

        /**
         * Tiện ích gọi Service bắt đầu tải hàng loạt chương cho một truyện.
         */
        fun startDownload(
            context: Context,
            novelId: String,
            novelTitle: String,
            thumbUrl: String = "",
            startOrder: Int = 1,
            endOrder: Int = Int.MAX_VALUE
        ) {
            val intent = Intent(context, NovelDownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_NOVEL_ID, novelId)
                putExtra(EXTRA_NOVEL_TITLE, novelTitle)
                putExtra(EXTRA_THUMB_URL, thumbUrl)
                putExtra(EXTRA_START_ORDER, startOrder)
                putExtra(EXTRA_END_ORDER, endOrder)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pauseDownload(context: Context, novelId: String) {
            val intent = Intent(context, NovelDownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOAD
                putExtra(EXTRA_NOVEL_ID, novelId)
            }
            context.startService(intent)
        }

        fun resumeDownload(context: Context, novelId: String) {
            val intent = Intent(context, NovelDownloadService::class.java).apply {
                action = ACTION_RESUME_DOWNLOAD
                putExtra(EXTRA_NOVEL_ID, novelId)
            }
            context.startService(intent)
        }

        fun cancelDownload(context: Context, novelId: String) {
            val intent = Intent(context, NovelDownloadService::class.java).apply {
                action = ACTION_CANCEL_DOWNLOAD
                putExtra(EXTRA_NOVEL_ID, novelId)
            }
            context.startService(intent)
        }
    }
}
