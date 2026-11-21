package com.truyencv.app.service.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.truyencv.app.MainActivity

/**
 * Trợ thủ quản lý thông báo cho tiến trình tải truyện offline hàng loạt (SRS §5.7 & §5.9).
 */
object DownloadNotificationHelper {

    const val CHANNEL_ID = "truyencv_download_channel"
    const val CHANNEL_NAME = "TruyenCV Tải Truyện Offline"
    const val NOTIFICATION_ID = 3001

    // Action Intent Constants
    const val ACTION_START_DOWNLOAD = "com.truyencv.app.action.START_DOWNLOAD"
    const val ACTION_PAUSE_DOWNLOAD = "com.truyencv.app.action.PAUSE_DOWNLOAD"
    const val ACTION_RESUME_DOWNLOAD = "com.truyencv.app.action.RESUME_DOWNLOAD"
    const val ACTION_CANCEL_DOWNLOAD = "com.truyencv.app.action.CANCEL_DOWNLOAD"

    // Extras Constants
    const val EXTRA_NOVEL_ID = "extra_novel_id"
    const val EXTRA_NOVEL_TITLE = "extra_novel_title"
    const val EXTRA_THUMB_URL = "extra_thumb_url"
    const val EXTRA_START_ORDER = "extra_start_order"
    const val EXTRA_END_ORDER = "extra_end_order"

    /**
     * Tính toán tỷ lệ % tiến độ tải dạng số nguyên (0 đến 100).
     */
    fun calculateProgressPercentage(downloaded: Int, total: Int): Int {
        if (total <= 0) return 0
        return ((downloaded.toLong() * 100) / total).toInt().coerceIn(0, 100)
    }

    /**
     * Tạo Notification Channel trên Android 8.0+ (API 26).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Thông báo tiến độ tải truyện offline hàng loạt"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Xây dựng Notification hiển thị thanh tiến độ tải truyện (hoặc trạng thái tạm dừng).
     */
    fun buildProgressNotification(
        context: Context,
        novelId: String,
        novelTitle: String,
        downloadedCount: Int,
        totalChapters: Int,
        isPaused: Boolean = false
    ): Notification {
        createNotificationChannel(context)

        // PendingIntent click mở ứng dụng
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOVEL_ID, novelId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent Pause hoặc Resume
        val toggleAction = if (isPaused) ACTION_RESUME_DOWNLOAD else ACTION_PAUSE_DOWNLOAD
        val toggleIntent = Intent(context, NovelDownloadService::class.java).apply {
            action = toggleAction
            putExtra(EXTRA_NOVEL_ID, novelId)
        }
        val togglePendingIntent = PendingIntent.getService(
            context,
            1,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent Cancel
        val cancelIntent = Intent(context, NovelDownloadService::class.java).apply {
            action = ACTION_CANCEL_DOWNLOAD
            putExtra(EXTRA_NOVEL_ID, novelId)
        }
        val cancelPendingIntent = PendingIntent.getService(
            context,
            2,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val percentage = calculateProgressPercentage(downloadedCount, totalChapters)
        val title = if (isPaused) "Tạm dừng tải: $novelTitle" else "Đang tải: $novelTitle"
        val contentText = if (totalChapters > 0) {
            "Chương $downloadedCount/$totalChapters ($percentage%)"
        } else {
            "Đang chuẩn bị tải danh sách chương..."
        }

        val toggleIcon = if (isPaused) {
            android.R.drawable.ic_media_play
        } else {
            android.R.drawable.ic_media_pause
        }
        val toggleText = if (isPaused) "Tiếp tục" else "Tạm dừng"

        val maxProgress = totalChapters.coerceAtLeast(1)
        val isIndeterminate = totalChapters <= 0

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(contentPendingIntent)
            .setProgress(maxProgress, downloadedCount, isIndeterminate)
            .addAction(toggleIcon, toggleText, togglePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Hủy tải", cancelPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * Xây dựng Notification khi đã hoàn tất tải toàn bộ chương truyện.
     */
    fun buildCompletedNotification(
        context: Context,
        novelId: String,
        novelTitle: String,
        totalChapters: Int
    ): Notification {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOVEL_ID, novelId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Đã tải xong: $novelTitle")
            .setContentText("Đã tải xong toàn bộ $totalChapters chương truyện.")
            .setContentIntent(contentPendingIntent)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * Xây dựng Notification khi tiến trình tải gặp sự cố hoặc bị gián đoạn.
     */
    fun buildErrorNotification(
        context: Context,
        novelId: String,
        novelTitle: String,
        downloadedCount: Int,
        totalChapters: Int,
        errorMessage: String
    ): Notification {
        createNotificationChannel(context)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Lỗi tải truyện: $novelTitle")
            .setContentText("Dừng tại $downloadedCount/$totalChapters chương: $errorMessage")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * Cập nhật thông báo tiến độ trực tiếp.
     */
    fun updateProgress(
        context: Context,
        novelId: String,
        novelTitle: String,
        downloadedCount: Int,
        totalChapters: Int,
        isPaused: Boolean = false
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val notif = buildProgressNotification(
            context, novelId, novelTitle, downloadedCount, totalChapters, isPaused
        )
        manager.notify(NOTIFICATION_ID, notif)
    }
}
