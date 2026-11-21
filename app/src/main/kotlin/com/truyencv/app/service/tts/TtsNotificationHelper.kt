package com.truyencv.app.service.tts

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
 * Trợ thủ quản lý Notification cho TTS Foreground Service (SRS §5.5 & §5.7).
 */
object TtsNotificationHelper {

    const val CHANNEL_ID = "truyencv_tts_channel"
    const val CHANNEL_NAME = "TruyenCV TTS Trình đọc giọng nói"
    const val NOTIFICATION_ID = 2001

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
                description = "Điều khiển đọc truyện giọng nói chạy nền"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Xây dựng Notification hiển thị trạng thái phát TTS với các nút điều khiển:
     * - Lùi đoạn / chương (Prev)
     * - Tạm dừng / Tiếp tục (Play/Pause)
     * - Tiến đoạn / chương (Next)
     * - Dừng hoàn toàn (Stop)
     */
    fun buildNotification(
        context: Context,
        state: TtsState,
        currentParagraphText: String? = null
    ): Notification {
        createNotificationChannel(context)

        // PendingIntent click mở lại MainActivity tại màn hình đọc
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOVEL_ID, state.novelId)
            putExtra(EXTRA_CHAPTER_ID, state.chapterId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent nút Lùi (Prev)
        val prevIntent = Intent(context, TtsForegroundService::class.java).apply {
            action = ACTION_PREV_PARAGRAPH
        }
        val prevPendingIntent = PendingIntent.getService(
            context,
            1,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent nút Play / Pause
        val playPauseAction = if (state.isPlaying) ACTION_PAUSE else ACTION_RESUME
        val playPauseIntent = Intent(context, TtsForegroundService::class.java).apply {
            action = playPauseAction
        }
        val playPausePendingIntent = PendingIntent.getService(
            context,
            2,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent nút Tiến (Next)
        val nextIntent = Intent(context, TtsForegroundService::class.java).apply {
            action = ACTION_NEXT_PARAGRAPH
        }
        val nextPendingIntent = PendingIntent.getService(
            context,
            3,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent nút Dừng (Stop)
        val stopIntent = Intent(context, TtsForegroundService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            4,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = state.chapterTitle.ifBlank {
            state.novelTitle.ifBlank { "TruyenCV Đang đọc giọng nói" }
        }

        val paragraphInfo = if (state.totalParagraphs > 0) {
            "Đoạn ${state.currentParagraphIndex + 1}/${state.totalParagraphs}"
        } else {
            ""
        }

        val subText = if (state.novelTitle.isNotBlank() && paragraphInfo.isNotBlank()) {
            "${state.novelTitle} • $paragraphInfo"
        } else if (state.novelTitle.isNotBlank()) {
            state.novelTitle
        } else {
            paragraphInfo
        }

        val bodyText = currentParagraphText ?: state.novelTitle.ifBlank { "Đang phát..." }

        val playPauseIcon = if (state.isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        val playPauseText = if (state.isPlaying) "Tạm dừng" else "Tiếp tục"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle(title)
            .setContentText(bodyText)
            .setSubText(subText)
            .setContentIntent(contentPendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(subText)
                    .bigText(bodyText)
            )
            .addAction(android.R.drawable.ic_media_previous, "Trước", prevPendingIntent)
            .addAction(playPauseIcon, playPauseText, playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Sau", nextPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dừng", stopPendingIntent)
            .setOngoing(state.isPlaying)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return builder.build()
    }

    /**
     * Cập nhật Notification trực tiếp tới hệ thống.
     */
    fun updateNotification(
        context: Context,
        state: TtsState,
        currentParagraphText: String? = null
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        notificationManager.notify(
            NOTIFICATION_ID,
            buildNotification(context, state, currentParagraphText)
        )
    }
}
