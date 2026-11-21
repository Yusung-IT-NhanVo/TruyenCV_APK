package com.truyencv.app.ui.reader

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer
import com.truyencv.app.ui.components.ErrorStateView

/**
 * Main Reader screen integrating content rendering, gestures, floating overlay toolbars,
 * and preference settings bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    novelId: String,
    chapterId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToChapterList: (novelId: String) -> Unit = {},
    onOpenTts: (novelId: String, chapterId: String) -> Unit = { _, _ -> },
    viewModel: ReaderViewModel = run {
        val container = LocalAppContainer.current
        viewModel(
            key = "Reader_${novelId}_$chapterId",
            factory = ReaderViewModel.provideFactory(
                novelId = novelId,
                initialChapterId = chapterId,
                chapterRepository = container.chapterRepository,
                historyRepository = container.historyRepository,
                readerPreferences = container.readerPreferences
            )
        )
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Screen-on effect when keepScreenOn preference is enabled
    val context = LocalContext.current
    DisposableEffect(uiState.keepScreenOn) {
        val window = (context as? Activity)?.window
        if (uiState.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val backgroundColor = remember(uiState.backgroundColorHex) {
        parseColorHex(uiState.backgroundColorHex, Color.White)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Main reading content layer
        when {
            uiState.isLoading && uiState.currentChapter == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.error != null && uiState.currentChapter == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorStateView(
                        title = "Không thể tải chương",
                        message = uiState.error ?: "Đã xảy ra lỗi không xác định",
                        onRetry = { viewModel.loadInitialData() }
                    )
                }
            }

            uiState.currentChapter != null -> {
                val currentChapter = uiState.currentChapter!!
                ReaderTextContent(
                    chapter = currentChapter,
                    fontSize = uiState.fontSize,
                    fontFamilyName = uiState.fontFamily,
                    lineHeightMultiplier = uiState.lineHeight,
                    backgroundColorHex = uiState.backgroundColorHex,
                    textColorHex = uiState.textColorHex,
                    readerMode = uiState.readerMode,
                    onToggleMenu = { viewModel.toggleMenu() },
                    onNextChapter = { viewModel.navigateToNextChapter() },
                    onPrevChapter = { viewModel.navigateToPrevChapter() },
                    hasNextChapter = uiState.hasNextChapter,
                    hasPrevChapter = uiState.hasPrevChapter,
                    initialScrollPosition = uiState.scrollPosition,
                    onScrollPositionChanged = { pos -> viewModel.updateScrollPosition(pos) }
                )
            }
        }

        // Top Navigation Bar (Animated Overlay)
        AnimatedVisibility(
            visible = uiState.isMenuVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.currentChapter?.title ?: "Đang đọc truyện",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToChapterList(novelId) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Mục lục chương"
                        )
                    }
                    IconButton(
                        onClick = {
                            uiState.currentChapter?.let { chapter ->
                                onOpenTts(novelId, chapter.id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Đọc bằng giọng nói"
                        )
                    }
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Cài đặt giao diện"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }

        // Bottom Navigation Bar (Animated Overlay)
        AnimatedVisibility(
            visible = uiState.isMenuVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val totalCount = uiState.totalChaptersCount
                    val currentIndex = uiState.currentChapterIndex
                    var sliderPos by remember(currentIndex) { mutableFloatStateOf(currentIndex.toFloat()) }

                    // Chapter navigation slider row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateToPrevChapter() },
                            enabled = uiState.hasPrevChapter
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Chương trước"
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (totalCount > 0) {
                                    "Chương ${currentIndex + 1} / $totalCount"
                                } else {
                                    "Chương --"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (totalCount > 1) {
                                Slider(
                                    value = sliderPos,
                                    onValueChange = { sliderPos = it },
                                    onValueChangeFinished = {
                                        viewModel.navigateToChapterByIndex(sliderPos.toInt())
                                    },
                                    valueRange = 0f..(totalCount - 1).toFloat(),
                                    steps = (totalCount - 2).coerceAtLeast(0)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.navigateToNextChapter() },
                            enabled = uiState.hasNextChapter
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Chương sau"
                            )
                        }
                    }

                    // Quick settings action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.setFontSize(uiState.fontSize - 2f) }) {
                            Text("A-")
                        }
                        TextButton(onClick = { viewModel.setFontSize(uiState.fontSize + 2f) }) {
                            Text("A+")
                        }
                        TextButton(
                            onClick = {
                                val nextMode = if (uiState.readerMode == "SCROLL") "PAGE" else "SCROLL"
                                viewModel.setReaderMode(nextMode)
                            }
                        ) {
                            Text(if (uiState.readerMode == "SCROLL") "Lật trang ↔" else "Cuộn dọc ↕")
                        }
                        TextButton(onClick = { showBottomSheet = true }) {
                            Text("Cài đặt")
                        }
                    }
                }
            }
        }

        // Settings BottomSheet
        if (showBottomSheet) {
            ReaderMenuBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                fontSize = uiState.fontSize,
                onFontSizeChange = { viewModel.setFontSize(it) },
                currentFontFamily = uiState.fontFamily,
                onFontFamilyChange = { viewModel.setFontFamily(it) },
                currentLineHeight = uiState.lineHeight,
                onLineHeightChange = { viewModel.setLineHeight(it) },
                currentBgHex = uiState.backgroundColorHex,
                currentTextHex = uiState.textColorHex,
                onThemeChange = { bg, text -> viewModel.setTheme(bg, text) },
                readerMode = uiState.readerMode,
                onReaderModeChange = { viewModel.setReaderMode(it) },
                onOpenTts = {
                    showBottomSheet = false
                    uiState.currentChapter?.let { chapter ->
                        onOpenTts(novelId, chapter.id)
                    }
                },
                onOpenChapterList = {
                    showBottomSheet = false
                    onNavigateToChapterList(novelId)
                }
            )
        }
    }
}
