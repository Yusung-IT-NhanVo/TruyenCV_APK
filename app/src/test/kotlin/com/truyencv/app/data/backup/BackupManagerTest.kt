package com.truyencv.app.data.backup

import com.truyencv.app.data.local.db.BookmarkDao
import com.truyencv.app.data.local.db.BookmarkEntity
import com.truyencv.app.data.local.db.NovelDao
import com.truyencv.app.data.local.db.NovelEntity
import com.truyencv.app.data.local.db.ReadingHistoryDao
import com.truyencv.app.data.local.db.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BackupManagerTest {

    private lateinit var fakeNovelDao: FakeNovelDao
    private lateinit var fakeBookmarkDao: FakeBookmarkDao
    private lateinit var fakeHistoryDao: FakeReadingHistoryDao
    private lateinit var backupManager: BackupManager

    @Before
    fun setUp() {
        fakeNovelDao = FakeNovelDao()
        fakeBookmarkDao = FakeBookmarkDao()
        fakeHistoryDao = FakeReadingHistoryDao()
        backupManager = BackupManager(fakeNovelDao, fakeBookmarkDao, fakeHistoryDao)
    }

    @Test
    fun testCalculateSha256_isDeterministic() {
        val hash1 = BackupManager.calculateSha256("Hello TruyenCV")
        val hash2 = BackupManager.calculateSha256("Hello TruyenCV")
        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length)
    }

    @Test
    fun testCreateBackupJson_containsChecksumAndData() = runTest {
        fakeNovelDao.insert(
            NovelEntity(
                id = "tcv-1",
                rawId = 1L,
                title = "Truyện Test",
                author = "Tác giả",
                thumbUrl = "https://thumb.com/1.jpg",
                description = "Mô tả",
                genresJson = "[]",
                totalChapters = 10,
                status = "COMPLETED",
                source = "TRUYENCV"
            )
        )
        fakeBookmarkDao.insert(BookmarkEntity(novelId = "tcv-1", createdAt = 1000L))
        fakeHistoryDao.insertOrUpdate(
            ReadingHistoryEntity(
                novelId = "tcv-1",
                chapterId = "tcv-c1",
                chapterOrder = 1,
                chapterTitle = "C1",
                scrollPosition = 0,
                lastReadTime = 1000L
            )
        )

        val json = backupManager.createBackupJson()
        assertNotNull(json)
        assertTrue(json.contains("\"checksum\""))
        assertTrue(json.contains("\"Truyện Test\""))
        assertTrue(json.contains("\"tcv-1\""))
    }

    @Test
    fun testRestoreFromJson_verifiesChecksumSuccess() = runTest {
        val originalJson = backupManager.createBackupJson()

        // Clear DAOs
        fakeNovelDao.clear()
        fakeBookmarkDao.clear()
        fakeHistoryDao.clear()

        val result = backupManager.restoreFromJson(originalJson)
        assertTrue(result.isSuccess)
    }

    @Test
    fun testRestoreFromJson_invalidChecksumFails() = runTest {
        val originalJson = backupManager.createBackupJson()
        // Tamper with checksum directly
        val tamperedJson = originalJson.replace("\"checksum\": \"", "\"checksum\": \"invalid_sha256_hash_")

        val result = backupManager.restoreFromJson(tamperedJson)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SecurityException)
    }

    // --- Fakes ---

    private class FakeNovelDao : NovelDao {
        val memory = mutableMapOf<String, NovelEntity>()
        private val flow = MutableStateFlow<List<NovelEntity>>(emptyList())

        fun clear() {
            memory.clear()
            flow.value = emptyList()
        }

        override fun getAll(): Flow<List<NovelEntity>> = flow
        override suspend fun getById(id: String): NovelEntity? = memory[id]
        override suspend fun insert(novel: NovelEntity) {
            memory[novel.id] = novel
            flow.value = memory.values.toList()
        }
        override suspend fun insertAll(novels: List<NovelEntity>) {
            novels.forEach { memory[it.id] = it }
            flow.value = memory.values.toList()
        }
        override suspend fun deleteById(id: String) {
            memory.remove(id)
            flow.value = memory.values.toList()
        }
        override suspend fun updateCoverBase64(id: String, base64: String) {}
    }

    private class FakeBookmarkDao : BookmarkDao {
        val memory = mutableMapOf<String, BookmarkEntity>()
        private val flow = MutableStateFlow<List<BookmarkEntity>>(emptyList())

        fun clear() {
            memory.clear()
            flow.value = emptyList()
        }

        override fun getAllBookmarks(): Flow<List<BookmarkEntity>> = flow
        override fun getBookmarkedNovels(): Flow<List<NovelEntity>> = MutableStateFlow(emptyList())
        override suspend fun getBookmark(novelId: String): BookmarkEntity? = memory[novelId]
        override suspend fun isBookmarked(novelId: String): Boolean = memory.containsKey(novelId)
        override suspend fun insert(bookmark: BookmarkEntity) {
            memory[bookmark.novelId] = bookmark
            flow.value = memory.values.toList()
        }
        override suspend fun delete(novelId: String) {
            memory.remove(novelId)
            flow.value = memory.values.toList()
        }
    }

    private class FakeReadingHistoryDao : ReadingHistoryDao {
        val memory = mutableMapOf<String, ReadingHistoryEntity>()
        private val flow = MutableStateFlow<List<ReadingHistoryEntity>>(emptyList())

        fun clear() {
            memory.clear()
            flow.value = emptyList()
        }

        override fun getAll(): Flow<List<ReadingHistoryEntity>> = flow
        override suspend fun getByNovelId(novelId: String): ReadingHistoryEntity? = memory[novelId]
        override suspend fun insertOrUpdate(history: ReadingHistoryEntity) {
            memory[history.novelId] = history
            flow.value = memory.values.toList()
        }
        override suspend fun deleteByNovelId(novelId: String) {
            memory.remove(novelId)
            flow.value = memory.values.toList()
        }
        override suspend fun clearAll() {
            memory.clear()
            flow.value = emptyList()
        }
    }
}
