package com.truyencv.app.ui.reader

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Three distinct horizontal tap zones across the screen:
 * - Left zone (0% - 25%): Page back in PAGE mode, or scroll up in SCROLL mode.
 * - Center zone (25% - 75%): Toggle Reader controls (TopBar / BottomBar overlay).
 * - Right zone (75% - 100%): Page forward in PAGE mode, or scroll down in SCROLL mode.
 */
enum class ReaderTapZone {
    LEFT,
    CENTER,
    RIGHT
}

/**
 * Modifier detecting 3-zone horizontal taps for novel reading.
 */
fun Modifier.reader3ZoneTapGesture(
    onLeftTap: () -> Unit,
    onCenterTap: () -> Unit,
    onRightTap: () -> Unit
): Modifier = pointerInput(onLeftTap, onCenterTap, onRightTap) {
    detectTapGestures { offset ->
        val width = size.width
        if (width <= 0) return@detectTapGestures
        val ratio = offset.x / width.toFloat()
        when {
            ratio < 0.25f -> onLeftTap()
            ratio > 0.75f -> onRightTap()
            else -> onCenterTap()
        }
    }
}

/**
 * Composable container wrapping content and detecting 3-zone taps.
 */
@Composable
fun ReaderGestureDetector(
    onLeftTap: () -> Unit,
    onCenterTap: () -> Unit,
    onRightTap: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val currentLeft by rememberUpdatedState(onLeftTap)
    val currentCenter by rememberUpdatedState(onCenterTap)
    val currentRight by rememberUpdatedState(onRightTap)

    Box(
        modifier = modifier.reader3ZoneTapGesture(
            onLeftTap = { currentLeft() },
            onCenterTap = { currentCenter() },
            onRightTap = { currentRight() }
        ),
        content = content
    )
}
