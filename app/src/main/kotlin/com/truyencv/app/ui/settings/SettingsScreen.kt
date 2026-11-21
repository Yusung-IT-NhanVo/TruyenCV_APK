package com.truyencv.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.data.backup.BackupManager
import com.truyencv.app.data.local.datastore.AppPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = run {
        val container = LocalAppContainer.current
        val context = LocalContext.current
        val backupManager = BackupManager(
            novelDao = container.database.novelDao(),
            bookmarkDao = container.database.bookmarkDao(),
            readingHistoryDao = container.database.readingHistoryDao()
        )
        viewModel(
            factory = SettingsViewModel.provideFactory(
                appPreferences = container.appPreferences,
                backupManager = backupManager,
                database = container.database,
                context = context
            )
        )
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreInputText by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. Giao diện & Chủ đề (Theme)
            // ==========================================
            item {
                SettingsSectionCard(title = "Chủ đề & Giao diện", icon = Icons.Default.DarkMode) {
                    Text(
                        text = "Chọn chế độ màu hiển thị toàn ứng dụng:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "Hệ thống",
                            selected = uiState.themeMode == AppPreferences.THEME_SYSTEM,
                            onClick = { viewModel.setThemeMode(AppPreferences.THEME_SYSTEM) }
                        )
                        ThemeOptionChip(
                            label = "Sáng",
                            selected = uiState.themeMode == AppPreferences.THEME_LIGHT,
                            onClick = { viewModel.setThemeMode(AppPreferences.THEME_LIGHT) }
                        )
                        ThemeOptionChip(
                            label = "Tối",
                            selected = uiState.themeMode == AppPreferences.THEME_DARK,
                            onClick = { viewModel.setThemeMode(AppPreferences.THEME_DARK) }
                        )
                    }
                }
            }

            // ==========================================
            // 2. Nguồn truyện mặc định
            // ==========================================
            item {
                SettingsSectionCard(title = "Nguồn truyện ưu tiên", icon = Icons.Default.Language) {
                    Text(
                        text = "Nguồn mặc định khi mở Khám Phá và Tìm Kiếm:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "Tất cả",
                            selected = uiState.defaultSource == AppPreferences.SOURCE_ALL,
                            onClick = { viewModel.setDefaultSource(AppPreferences.SOURCE_ALL) }
                        )
                        ThemeOptionChip(
                            label = "TruyenCV.io",
                            selected = uiState.defaultSource == AppPreferences.SOURCE_TRUYENCV,
                            onClick = { viewModel.setDefaultSource(AppPreferences.SOURCE_TRUYENCV) }
                        )
                        ThemeOptionChip(
                            label = "MotTruyen",
                            selected = uiState.defaultSource == AppPreferences.SOURCE_MOTTRUYEN,
                            onClick = { viewModel.setDefaultSource(AppPreferences.SOURCE_MOTTRUYEN) }
                        )
                    }
                }
            }

            // ==========================================
            // 3. Tải trước & Bộ nhớ đệm (Cache)
            // ==========================================
            item {
                SettingsSectionCard(title = "Bộ nhớ đệm & Đọc trước", icon = Icons.Default.Speed) {
                    Text(
                        text = "Số chương tự động tải trước khi đọc (Buffer):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "±3 chương",
                            selected = uiState.prefetchBufferSize == 3,
                            onClick = { viewModel.setPrefetchBufferSize(3) }
                        )
                        ThemeOptionChip(
                            label = "±6 chương (Chuẩn)",
                            selected = uiState.prefetchBufferSize == 6,
                            onClick = { viewModel.setPrefetchBufferSize(6) }
                        )
                        ThemeOptionChip(
                            label = "±10 chương",
                            selected = uiState.prefetchBufferSize == 10,
                            onClick = { viewModel.setPrefetchBufferSize(10) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dung lượng bộ nhớ tạm:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = formatFileSize(uiState.cacheSizeBytes),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { viewModel.clearCache() },
                            enabled = !uiState.isClearingCache
                        ) {
                            if (uiState.isClearingCache) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(text = "Dọn rác")
                        }
                    }
                }
            }

            // ==========================================
            // 4. Sao lưu & Khôi phục (Backup & Restore)
            // ==========================================
            item {
                SettingsSectionCard(title = "Sao lưu & Khôi phục dữ liệu", icon = Icons.Default.Storage) {
                    val lastBackupStr = if (uiState.lastBackupTime > 0) {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(uiState.lastBackupTime))
                    } else {
                        "Chưa sao lưu lần nào"
                    }

                    Text(
                        text = "Xuất toàn bộ Tủ truyện (Theo dõi, Lịch sử đọc, Cài đặt) ra tệp JSON có mã hóa checksum SHA-256 an toàn.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sao lưu gần nhất: $lastBackupStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.exportBackup { json ->
                                    if (json != null) {
                                        exportedJsonText = json
                                        showExportDialog = true
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Xuất sao lưu")
                        }

                        OutlinedButton(
                            onClick = {
                                restoreInputText = ""
                                showRestoreDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Khôi phục")
                        }
                    }
                }
            }

            // ==========================================
            // 5. Thông tin ứng dụng
            // ==========================================
            item {
                SettingsSectionCard(title = "Thông tin ứng dụng", icon = Icons.Default.Info) {
                    AppInfoRow(label = "Tên ứng dụng", value = "TruyenCV Native Reader")
                    AppInfoRow(label = "Phiên bản", value = "1.0.0 (Build Phase 4)")
                    AppInfoRow(label = "Nền tảng", value = "Android Native · Jetpack Compose M3")
                    AppInfoRow(label = "Nguồn tích hợp", value = "TruyenCV.io + MotTruyen.com")
                    AppInfoRow(label = "Cơ chế lưu trữ", value = "Room SQLite + DataStore Preferences")
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // Dialog hiển thị mã sao lưu để Copy
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(text = "Sao lưu thành công (JSON + SHA-256)") },
            text = {
                Column {
                    Text(
                        text = "Dữ liệu sao lưu đã được tạo. Bạn có thể sao chép vào bộ nhớ tạm hoặc lưu lại:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedJsonText.take(300) + if (exportedJsonText.length > 300) "\n... [còn tiếp ${exportedJsonText.length} ký tự]" else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("TruyenCV_Backup", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Đã sao chép dữ liệu sao lưu vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Text("Sao chép tất cả")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Dialog dán mã JSON để Khôi phục
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text(text = "Khôi phục dữ liệu Tủ truyện") },
            text = {
                Column {
                    Text(
                        text = "Dán chuỗi dữ liệu JSON đã sao lưu vào ô dưới đây (hoặc lấy từ bộ nhớ tạm):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreInputText,
                        onValueChange = { restoreInputText = it },
                        placeholder = { Text("Dán nội dung JSON sao lưu vào đây...") },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipItem = clipboard.primaryClip?.getItemAt(0)
                            val text = clipItem?.text?.toString()
                            if (!text.isNullOrBlank()) {
                                restoreInputText = text
                            } else {
                                Toast.makeText(context, "Bộ nhớ tạm không có dữ liệu văn bản", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Dán từ bộ nhớ tạm")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreInputText.isNotBlank()) {
                            viewModel.restoreBackup(restoreInputText)
                            showRestoreDialog = false
                        }
                    },
                    enabled = restoreInputText.isNotBlank()
                ) {
                    Text("Bắt đầu khôi phục")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, fontSize = 13.sp) }
    )
}

@Composable
private fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    if (digitGroups >= units.size) digitGroups = units.size - 1
    val df = java.text.DecimalFormat("#,##0.#")
    return "${df.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
}
