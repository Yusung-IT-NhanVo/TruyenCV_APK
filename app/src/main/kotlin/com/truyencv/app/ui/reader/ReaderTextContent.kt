package com.truyencv.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truyencv.app.domain.model.Chapter
import com.truyencv.app.util.TextSanitizer
import kotlinx.coroutines.launch

/**
 * Resolves font family name string into Compose [FontFamily].
 */
fun resolveFontFamily(fontName: String): FontFamily {
    return when (fontName.trim().lowercase()) {
        "serif", "noto serif", "georgia" -> FontFamily.Serif
        "sans-serif", "sans", "roboto" -> FontFamily.SansSerif
        "monospace", "mono" -> FontFamily.Monospace
        else -> FontFamily.Default
    }
}

/**
 * Safely parses color hex string (e.g. #FFFFFF) with fallback color.
 */
fun parseColorHex(hex: String, fallback: Color = Color.Black): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        fallback
    }
}

/**
 * Data representation for paginated chapter text in PAGE mode.
 */
data class ReaderPageData(
    val pageIndex: Int,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val title: String,
    val paragraphs: List<String>
)

/**
 * Renders novel chapter text with full styling, supporting both SCROLL and PAGE reader modes.
 */
@Composable
fun ReaderTextContent(
    chapter: Chapter,
    fontSize: Float,
    fontFamilyName: String,
    lineHeightMultiplier: Float,
    backgroundColorHex: String,
    textColorHex: String,
    readerMode: String,
    onToggleMenu: () -> Unit,
    onNextChapter: () -> Unit,
    onPrevChapter: () -> Unit,
    hasNextChapter: Boolean,
    hasPrevChapter: Boolean,
    modifier: Modifier = Modifier,
    initialScrollPosition: Int = 0,
    onScrollPositionChanged: (Int) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val resolvedFontFamily = remember(fontFamilyName) { resolveFontFamily(fontFamilyName) }
    val backgroundColor = remember(backgroundColorHex) { parseColorHex(backgroundColorHex, Color.White) }
    val textColor = remember(textColorHex) { parseColorHex(textColorHex, Color.Black) }
    val lineHeightSp = (fontSize * lineHeightMultiplier).sp

    // Clean HTML & duplicate chapter headers
    val cleanedText = remember(chapter.content) {
        val sanitized = TextSanitizer.sanitizeChapterContent(chapter.content)
        TextSanitizer.cleanHtml(sanitized)
    }

    val paragraphs = remember(cleanedText) {
        if (cleanedText.isBlank()) {
            emptyList()
        } else {
            cleanedText.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (readerMode == "PAGE") {
            // ================= PAGE MODE (HorizontalPager) =================
            val pages = remember(paragraphs, chapter.title, fontSize) {
                paginateParagraphs(
                    title = chapter.title,
                    paragraphs = paragraphs,
                    fontSize = fontSize
                )
            }

            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { pages.size.coerceAtLeast(1) }
            )

            // Tap gestures for PAGE mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .reader3ZoneTapGesture(
                        onLeftTap = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            } else if (hasPrevChapter) {
                                onPrevChapter()
                            }
                        },
                        onCenterTap = onToggleMenu,
                        onRightTap = {
                            if (pagerState.currentPage < pages.size - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else if (hasNextChapter) {
                                onNextChapter()
                            }
                        }
                    )
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val pageData = pages.getOrNull(pageIndex)
                    if (pageData != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            // First page displays chapter header
                            if (pageData.isFirstPage) {
                                Text(
                                    text = pageData.title,
                                    fontSize = (fontSize + 4).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = resolvedFontFamily,
                                    color = textColor,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                HorizontalDivider(
                                    color = textColor.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            // Content paragraphs for this page
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Top
                            ) {
                                pageData.paragraphs.forEach { paragraph ->
                                    Text(
                                        text = paragraph,
                                        fontSize = fontSize.sp,
                                        lineHeight = lineHeightSp,
                                        fontFamily = resolvedFontFamily,
                                        color = textColor,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }

                                if (pageData.isLastPage) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "--- Hết chương ---",
                                        fontSize = (fontSize - 2).sp,
                                        fontFamily = resolvedFontFamily,
                                        color = textColor.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    if (hasNextChapter) {
                                        OutlinedButton(
                                            onClick = onNextChapter,
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = textColor
                                            )
                                        ) {
                                            Text("Sang chương tiếp theo")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                        }
                                    }
                                }
                            }

                            // Page footer counter
                            Text(
                                text = "Trang ${pageIndex + 1} / ${pages.size}",
                                fontSize = 12.sp,
                                fontFamily = resolvedFontFamily,
                                color = textColor.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // ================= SCROLL MODE (LazyColumn) =================
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialScrollPosition)

            LaunchedEffect(listState) {
                snapshotFlow { listState.firstVisibleItemIndex }
                    .collect { index -> onScrollPositionChanged(index) }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .reader3ZoneTapGesture(
                        onLeftTap = {
                            coroutineScope.launch {
                                listState.animateScrollBy(-700f)
                            }
                        },
                        onCenterTap = onToggleMenu,
                        onRightTap = {
                            coroutineScope.launch {
                                listState.animateScrollBy(700f)
                            }
                        }
                    )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    // Chapter title header
                    item(key = "header_${chapter.id}") {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = chapter.title,
                                fontSize = (fontSize + 4).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = resolvedFontFamily,
                                color = textColor,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            HorizontalDivider(
                                color = textColor.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                        }
                    }

                    // Chapter text paragraphs
                    itemsIndexed(
                        items = paragraphs,
                        key = { index, _ -> "p_${chapter.id}_$index" }
                    ) { _, paragraph ->
                        Text(
                            text = paragraph,
                            fontSize = fontSize.sp,
                            lineHeight = lineHeightSp,
                            fontFamily = resolvedFontFamily,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Chapter bottom navigation footer
                    item(key = "footer_${chapter.id}") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(
                                color = textColor.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "--- Hết chương ---",
                                fontSize = (fontSize - 2).sp,
                                fontFamily = resolvedFontFamily,
                                color = textColor.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = onPrevChapter,
                                    enabled = hasPrevChapter,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = textColor
                                    )
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chương trước")
                                }

                                OutlinedButton(
                                    onClick = onNextChapter,
                                    enabled = hasNextChapter,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = textColor
                                    )
                                ) {
                                    Text("Chương sau")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Splits chapter paragraphs into distinct pages according to font size and text length.
 */
private fun paginateParagraphs(
    title: String,
    paragraphs: List<String>,
    fontSize: Float
): List<ReaderPageData> {
    if (paragraphs.isEmpty()) {
        return listOf(
            ReaderPageData(
                pageIndex = 0,
                isFirstPage = true,
                isLastPage = true,
                title = title,
                paragraphs = emptyList()
            )
        )
    }

    // Dynamic character target per screen page based on font size
    val targetCharsPerPage = (850 * (18f / fontSize.coerceAtLeast(12f))).toInt().coerceIn(350, 1800)

    val pages = mutableListOf<ReaderPageData>()
    var currentPageParagraphs = mutableListOf<String>()
    var currentLength = 0

    for (paragraph in paragraphs) {
        // If a single paragraph exceeds target chars, split into sentence chunks
        if (paragraph.length > targetCharsPerPage) {
            val sentences = paragraph.split(". ")
            for (sentence in sentences) {
                val s = if (sentence.endsWith(".")) sentence else "$sentence."
                if (currentLength + s.length > targetCharsPerPage && currentPageParagraphs.isNotEmpty()) {
                    pages.add(
                        ReaderPageData(
                            pageIndex = pages.size,
                            isFirstPage = pages.isEmpty(),
                            isLastPage = false,
                            title = title,
                            paragraphs = currentPageParagraphs
                        )
                    )
                    currentPageParagraphs = mutableListOf()
                    currentLength = 0
                }
                currentPageParagraphs.add(s)
                currentLength += s.length
            }
        } else {
            if (currentLength + paragraph.length > targetCharsPerPage && currentPageParagraphs.isNotEmpty()) {
                pages.add(
                    ReaderPageData(
                        pageIndex = pages.size,
                        isFirstPage = pages.isEmpty(),
                        isLastPage = false,
                        title = title,
                        paragraphs = currentPageParagraphs
                    )
                )
                currentPageParagraphs = mutableListOf()
                currentLength = 0
            }
            currentPageParagraphs.add(paragraph)
            currentLength += paragraph.length
        }
    }

    if (currentPageParagraphs.isNotEmpty() || pages.isEmpty()) {
        pages.add(
            ReaderPageData(
                pageIndex = pages.size,
                isFirstPage = pages.isEmpty(),
                isLastPage = true,
                title = title,
                paragraphs = currentPageParagraphs
            )
        )
    }

    // Update last page flag
    return pages.mapIndexed { idx, page ->
        page.copy(
            isFirstPage = idx == 0,
            isLastPage = idx == pages.size - 1
        )
    }
}
