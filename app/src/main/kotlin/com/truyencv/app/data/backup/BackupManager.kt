package com.truyencv.app.data.backup

import com.truyencv.app.data.local.db.BookmarkDao
import com.truyencv.app.data.local.db.BookmarkEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.local.db.NovelEntity
import com.truyencv.app.data.local.db.ReadingHistoryDao
import com.truyencv.app.data.local.db.ReadingHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

/**
 * Data payload định dạng sao lưu chuẩn của TruyenCV_APK theo SRS §5.8.
 */
@Serializable
data class BackupPayload(
    val version: Int = 1,
    val app: String = "TruyenCV_APK",
    val timestamp: Long = System.currentTimeMillis(),
    val bookmarksCount: Int = 0,
    val historyCount: Int = 0,
    val bookmarks: List<BackupBookmarkItem> = emptyList(),
    val history: List<BackupHistoryItem> = emptyList(),
    val novels: List<BackupNovelItem> = emptyList(),
    val checksum: String = ""
)

@Serializable
data class BackupBookmarkItem(
    val novelId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class BackupHistoryItem(
    val novelId: String,
    val chapterId: String,
    val chapterOrder: Int,
    val chapterTitle: String,
    val scrollPosition: Int = 0,
    val lastReadTime: Long = System.currentTimeMillis()
)

@Serializable
data class BackupNovelItem(
    val id: String,
    val rawId: Long,
    val title: String,
    val author: String,
    val thumbUrl: String,
    val description: String,
    val genresJson: String,
    val totalChapters: Int,
    val views: Long = 0L,
    val status: String,
    val source: String
)

class BackupManager(
    private val novelDao: NovelDao,
    private val bookmarkDao: BookmarkDao,
    private val readingHistoryDao: ReadingHistoryDao
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Xuất dữ liệu Tủ truyện ra chuỗi JSON kèm SHA-256 checksum chống lỗi dữ liệu.
     */
    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val bookmarksList = bookmarkDao.getAllBookmarks().firstOrNull() ?: emptyList()
        val historyList = readingHistoryDao.getAll().firstOrNull() ?: emptyList()

        // Lấy danh sách các truyện tương ứng
        val allNovelEntities = novelDao.getAll().firstOrNull() ?: emptyList()
        val relevantNovelIds = (bookmarksList.map { it.novelId } + historyList.map { it.novelId }).toSet()
        val relevantNovels = allNovelEntities.filter { it.id in relevantNovelIds }

        val bookmarkItems = bookmarksList.map {
            BackupBookmarkItem(novelId = it.novelId, createdAt = it.createdAt)
        }
        val historyItems = historyList.map {
            BackupHistoryItem(
                novelId = it.novelId,
                chapterId = it.chapterId,
                chapterOrder = it.chapterOrder,
                chapterTitle = it.chapterTitle,
                scrollPosition = it.scrollPosition,
                lastReadTime = it.lastReadTime
            )
        }
        val novelItems = relevantNovels.map {
            BackupNovelItem(
                id = it.id,
                rawId = it.rawId,
                title = it.title,
                author = it.author,
                thumbUrl = it.thumbUrl,
                description = it.description,
                genresJson = it.genresJson,
                totalChapters = it.totalChapters,
                views = it.views,
                status = it.status,
                source = it.source
            )
        }

        val rawPayload = BackupPayload(
            version = 1,
            timestamp = System.currentTimeMillis(),
            bookmarksCount = bookmarkItems.size,
            historyCount = historyItems.size,
            bookmarks = bookmarkItems,
            history = historyItems,
            novels = novelItems,
            checksum = ""
        )

        val calculatedChecksum = computePayloadChecksum(rawPayload)
        val finalPayload = rawPayload.copy(checksum = calculatedChecksum)
        json.encodeToString(finalPayload)
    }

    /**
     * Nhập và khôi phục dữ liệu từ chuỗi JSON sao lưu.
     * Kiểm tra tính toàn vẹn bằng SHA-256 trước khi lưu vào SQLite Room.
     */
    suspend fun restoreFromJson(backupJson: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val payload = json.decodeFromString<BackupPayload>(backupJson)

            // Kiểm tra tính toàn vẹn dữ liệu
            if (payload.checksum.isNotBlank()) {
                val expectedChecksum = computePayloadChecksum(payload)
                if (payload.checksum != expectedChecksum) {
                    return@withContext Result.failure(
                        SecurityException("Tệp sao lưu bị sửa đổi hoặc lỗi checksum SHA-256 không hợp lệ")
                    )
                }
            }

            // 1. Phục hồi thông tin truyện
            if (payload.novels.isNotEmpty()) {
                val novelEntities = payload.novels.map {
                    NovelEntity(
                        id = it.id,
                        rawId = it.rawId,
                        title = it.title,
                        author = it.author,
                        thumbUrl = it.thumbUrl,
                        description = it.description,
                        genresJson = it.genresJson,
                        totalChapters = it.totalChapters,
                        views = it.views,
                        status = it.status,
                        source = it.source
                    )
                }
                novelDao.insertAll(novelEntities)
            }

            // 2. Phục hồi Bookmarks
            payload.bookmarks.forEach {
                bookmarkDao.insert(BookmarkEntity(novelId = it.novelId, createdAt = it.createdAt))
            }

            // 3. Phục hồi Reading History
            payload.history.forEach {
                readingHistoryDao.insertOrUpdate(
                    ReadingHistoryEntity(
                        novelId = it.novelId,
                        chapterId = it.chapterId,
                        chapterOrder = it.chapterOrder,
                        chapterTitle = it.chapterTitle,
                        scrollPosition = it.scrollPosition,
                        lastReadTime = it.lastReadTime
                    )
                )
            }

            val totalRestored = payload.bookmarks.size + payload.history.size
            Result.success(totalRestored)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        fun calculateSha256(text: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(text.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }

        fun computePayloadChecksum(payload: BackupPayload): String {
            val summary = "${payload.app}#${payload.version}#${payload.timestamp}#${payload.bookmarksCount}#${payload.historyCount}#" +
                payload.bookmarks.joinToString(",") { "${it.novelId}:${it.createdAt}" } + "#" +
                payload.history.joinToString(",") { "${it.novelId}:${it.chapterId}:${it.chapterOrder}" }
            return calculateSha256(summary)
        }
    }
}
