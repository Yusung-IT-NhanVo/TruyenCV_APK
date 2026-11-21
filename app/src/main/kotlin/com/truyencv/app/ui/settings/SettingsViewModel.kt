package com.truyencv.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.truyencv.app.data.backup.BackupManager
import com.truyencv.app.data.local.datastore.AppPreferences
import com.truyencv.app.data.local.db.TruyenCVDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class SettingsUiState(
    val themeMode: String = AppPreferences.THEME_SYSTEM,
    val defaultSource: String = AppPreferences.SOURCE_ALL,
    val prefetchBufferSize: Int = AppPreferences.DEFAULT_PREFETCH_BUFFER_SIZE,
    val cacheSizeBytes: Long = 0L,
    val lastBackupTime: Long = 0L,
    val isClearingCache: Boolean = false,
    val message: String? = null,
    val isSuccessMessage: Boolean = true
)

class SettingsViewModel(
    private val appPreferences: AppPreferences,
    private val backupManager: BackupManager,
    private val database: TruyenCVDatabase,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
        calculateCacheSize()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            appPreferences.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            appPreferences.defaultSource.collect { source ->
                _uiState.update { it.copy(defaultSource = source) }
            }
        }
        viewModelScope.launch {
            appPreferences.prefetchBufferSize.collect { size ->
                _uiState.update { it.copy(prefetchBufferSize = size) }
            }
        }
        viewModelScope.launch {
            appPreferences.lastBackupTime.collect { time ->
                _uiState.update { it.copy(lastBackupTime = time) }
            }
        }
    }

    fun calculateCacheSize() {
        viewModelScope.launch {
            val totalBytes = withContext(Dispatchers.IO) {
                var size = 0L
                try {
                    // Cache thư mục ứng dụng
                    context.cacheDir?.let { size += getFolderSize(it) }
                    // External cache
                    context.externalCacheDir?.let { size += getFolderSize(it) }
                } catch (_: Exception) {}
                size
            }
            _uiState.update { it.copy(cacheSizeBytes = totalBytes) }
        }
    }

    private fun getFolderSize(file: File): Long {
        if (!file.exists()) return 0L
        if (file.isFile) return file.length()
        var size = 0L
        file.listFiles()?.forEach {
            size += getFolderSize(it)
        }
        return size
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            appPreferences.setThemeMode(mode)
        }
    }

    fun setDefaultSource(source: String) {
        viewModelScope.launch {
            appPreferences.setDefaultSource(source)
        }
    }

    fun setPrefetchBufferSize(size: Int) {
        viewModelScope.launch {
            appPreferences.setPrefetchBufferSize(size)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearingCache = true) }
            withContext(Dispatchers.IO) {
                try {
                    context.cacheDir?.deleteRecursively()
                    context.externalCacheDir?.deleteRecursively()
                } catch (_: Exception) {}
            }
            calculateCacheSize()
            _uiState.update {
                it.copy(
                    isClearingCache = false,
                    message = "Đã dọn dẹp toàn bộ bộ nhớ đệm thành công!",
                    isSuccessMessage = true
                )
            }
        }
    }

    fun exportBackup(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val json = backupManager.createBackupJson()
                val now = System.currentTimeMillis()
                appPreferences.setLastBackupTime(now)
                _uiState.update {
                    it.copy(
                        message = "Xuất tệp sao lưu thành công!",
                        isSuccessMessage = true
                    )
                }
                onResult(json)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        message = "Lỗi xuất sao lưu: ${e.localizedMessage}",
                        isSuccessMessage = false
                    )
                }
                onResult(null)
            }
        }
    }

    fun restoreBackup(jsonString: String) {
        viewModelScope.launch {
            val result = backupManager.restoreFromJson(jsonString)
            result.fold(
                onSuccess = { count ->
                    _uiState.update {
                        it.copy(
                            message = "Khôi phục thành công $count mục vào Tủ truyện!",
                            isSuccessMessage = true
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            message = "Khôi phục thất bại: ${e.localizedMessage}",
                            isSuccessMessage = false
                        )
                    }
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        fun provideFactory(
            appPreferences: AppPreferences,
            backupManager: BackupManager,
            database: TruyenCVDatabase,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    appPreferences = appPreferences,
                    backupManager = backupManager,
                    database = database,
                    context = context
                ) as T
            }
        }
    }
}
