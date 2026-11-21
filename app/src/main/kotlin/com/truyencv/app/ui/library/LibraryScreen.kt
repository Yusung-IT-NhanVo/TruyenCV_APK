package com.truyencv.app.ui.library

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truyencv.app.LocalAppContainer

/**
 * Màn hình Tủ truyện (Library) tích hợp 4 Tab nội bộ theo SRS_REQUIREMENTS.md §5.8:
 * 1. 📚 Theo dõi (Bookmarks)
 * 2. 🕒 Lịch sử (Reading History)
 * 3. 💾 Offline (Offline Novels)
 * 4. ⏳ Đang tải (Download Tasks)
 */
@Composable
fun LibraryScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReader: (novelId: String, chapterId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModel.provideFactory(LocalAppContainer.current)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryScreenContent(
        uiState = uiState,
        onSelectTab = { viewModel.selectTab(it) },
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToReader = onNavigateToReader,
        onRemoveBookmark = { viewModel.removeBookmark(it) },
        onRemoveHistory = { viewModel.removeHistory(it) },
        onClearAllHistory = { viewModel.clearAllHistory() },
        onRemoveOffline = { viewModel.removeOfflineNovel(it) },
        onPauseDownload = { viewModel.pauseDownload(it) },
        onResumeDownload = { viewModel.resumeDownload(it) },
        onCancelDownload = { viewModel.cancelDownload(it) },
        modifier = modifier
    )
}

/**
 * Stateless Content Composable cho màn hình Tủ truyện, hỗ trợ test & Compose Preview.
 */
@Composable
fun LibraryScreenContent(
    uiState: LibraryUiState,
    onSelectTab: (LibraryTab) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReader: (novelId: String, chapterId: String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    onRemoveOffline: (String) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String) -> Unit,
    onCancelDownload: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // TabRow Material 3 với 4 tab và Badge thông báo số lượng
        TabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (uiState.selectedTab.ordinal < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            LibraryTab.entries.forEach { tab ->
                val badgeCount = when (tab) {
                    LibraryTab.FOLLOW -> uiState.bookmarks.size
                    LibraryTab.HISTORY -> uiState.history.size
                    LibraryTab.OFFLINE -> uiState.offlineNovels.size
                    LibraryTab.DOWNLOADING -> uiState.downloadTasks.size
                }

                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { onSelectTab(tab) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (badgeCount > 0) {
                                    Badge(
                                        containerColor = if (tab == LibraryTab.DOWNLOADING) {
                                            MaterialTheme.colorScheme.errorContainer
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        },
                                        contentColor = if (tab == LibraryTab.DOWNLOADING) {
                                            MaterialTheme.colorScheme.onErrorContainer
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    ) {
                                        Text(
                                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "${tab.emoji} ${tab.title}",
                                fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                )
            }
        }

        // Chuyển đổi nội dung theo tab được chọn với Crossfade mượt mà
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Crossfade(
                targetState = uiState.selectedTab,
                label = "LibraryTabTransition",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    LibraryTab.FOLLOW -> FollowTabContent(
                        novels = uiState.bookmarks,
                        onNovelClick = onNavigateToDetail,
                        onRemoveBookmark = onRemoveBookmark
                    )

                    LibraryTab.HISTORY -> HistoryTabContent(
                        historyList = uiState.history,
                        onNovelClick = onNavigateToDetail,
                        onContinueReading = onNavigateToReader,
                        onRemoveHistory = onRemoveHistory,
                        onClearAllHistory = onClearAllHistory
                    )

                    LibraryTab.OFFLINE -> OfflineTabContent(
                        offlineNovels = uiState.offlineNovels,
                        onNovelClick = onNavigateToDetail,
                        onReadOffline = onNavigateToDetail,
                        onRemoveOffline = onRemoveOffline
                    )

                    LibraryTab.DOWNLOADING -> DownloadingTabContent(
                        downloadTasks = uiState.downloadTasks,
                        onPause = onPauseDownload,
                        onResume = onResumeDownload,
                        onCancel = onCancelDownload
                    )
                }
            }
        }
    }
}
