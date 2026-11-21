package com.truyencv.app.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Predefined reading theme presets according to SRS §5.5.4.
 */
data class ReaderThemeOption(
    val name: String,
    val bgHex: String,
    val textHex: String
)

val READER_THEMES = listOf(
    ReaderThemeOption("Trắng", "#FFFFFF", "#212121"),
    ReaderThemeOption("Sepia", "#F8F1E3", "#5C4033"),
    ReaderThemeOption("Tối", "#2B2B2B", "#E0E0E0"),
    ReaderThemeOption("AMOLED", "#000000", "#FFFFFF"),
    ReaderThemeOption("Bảo vệ mắt", "#E8F5E9", "#1B5E20")
)

val AVAILABLE_FONTS = listOf("Roboto", "Serif", "Sans-Serif", "Monospace")
val LINE_HEIGHT_OPTIONS = listOf(1.2f, 1.5f, 1.8f, 2.0f)

/**
 * Bottom Sheet displaying full customization controls for reading preferences.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReaderMenuBottomSheet(
    onDismissRequest: () -> Unit,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    currentFontFamily: String,
    onFontFamilyChange: (String) -> Unit,
    currentLineHeight: Float,
    onLineHeightChange: (Float) -> Unit,
    currentBgHex: String,
    currentTextHex: String,
    onThemeChange: (bgHex: String, textHex: String) -> Unit,
    readerMode: String,
    onReaderModeChange: (String) -> Unit,
    onOpenTts: () -> Unit,
    onOpenChapterList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "Tùy chỉnh giao diện đọc",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. Font Size Control (12sp - 36sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cỡ chữ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(72.dp)
                )

                IconButton(
                    onClick = { onFontSizeChange((fontSize - 1).coerceAtLeast(12f)) }
                ) {
                    Text("A-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Slider(
                    value = fontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 12f..36f,
                    steps = 23,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onFontSizeChange((fontSize + 1).coerceAtMost(36f)) }
                ) {
                    Text("A+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Text(
                    text = "${fontSize.toInt()}sp",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Font Family Selection
            Text(
                text = "Phông chữ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AVAILABLE_FONTS.forEach { fontName ->
                    val isSelected = currentFontFamily.equals(fontName, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFontFamilyChange(fontName) },
                        label = { Text(fontName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 5 Reading Themes (Color Swatches)
            Text(
                text = "Màu nền & Giao diện",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                READER_THEMES.forEach { theme ->
                    val isSelected = currentBgHex.equals(theme.bgHex, ignoreCase = true)
                    val swatchBg = parseColorHex(theme.bgHex, Color.White)
                    val swatchText = parseColorHex(theme.textHex, Color.Black)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onThemeChange(theme.bgHex, theme.textHex) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(swatchBg)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Đã chọn",
                                    tint = swatchText,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "A",
                                    color = swatchText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Reader Mode (Scroll vs Page)
            Text(
                text = "Chế độ đọc",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isScroll = readerMode == "SCROLL"
                FilterChip(
                    selected = isScroll,
                    onClick = { onReaderModeChange("SCROLL") },
                    leadingIcon = { Icon(Icons.Default.SwapVert, contentDescription = null) },
                    label = { Text("Cuộn dọc ↕") },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = !isScroll,
                    onClick = { onReaderModeChange("PAGE") },
                    leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
                    label = { Text("Lật trang ↔") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Line Height (1.2x | 1.5x | 1.8x | 2.0x)
            Text(
                text = "Giãn dòng",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LINE_HEIGHT_OPTIONS.forEach { heightVal ->
                    val isSelected = kotlin.math.abs(currentLineHeight - heightVal) < 0.05f
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLineHeightChange(heightVal) },
                        label = { Text("${heightVal}x") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // 6. Action buttons: Open TTS & Chapter List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismissRequest()
                        onOpenTts()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Đọc giọng nói")
                }

                Button(
                    onClick = {
                        onDismissRequest()
                        onOpenChapterList()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mục lục")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
