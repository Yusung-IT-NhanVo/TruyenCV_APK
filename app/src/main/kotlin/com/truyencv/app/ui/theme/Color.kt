package com.truyencv.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Material 3 Light Scheme Colors
// ==========================================
val primaryLight = Color(0xFF00668B)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC3E8FF)
val onPrimaryContainerLight = Color(0xFF001E2C)

val secondaryLight = Color(0xFF4E616D)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFD1E5F4)
val onSecondaryContainerLight = Color(0xFF0A1E28)

val tertiaryLight = Color(0xFF615A7C)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFE7DEFF)
val onTertiaryContainerLight = Color(0xFF1D1735)

val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)

val backgroundLight = Color(0xFFFBFCFE)
val onBackgroundLight = Color(0xFF191C1E)
val surfaceLight = Color(0xFFFBFCFE)
val onSurfaceLight = Color(0xFF191C1E)
val surfaceVariantLight = Color(0xFFDCE3E9)
val onSurfaceVariantLight = Color(0xFF40484D)
val surfaceTintLight = Color(0xFF00668B)
val outlineLight = Color(0xFF71787D)
val outlineVariantLight = Color(0xFFC0C7CD)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E3133)
val inverseOnSurfaceLight = Color(0xFFEFF1F3)
val inversePrimaryLight = Color(0xFF7CD0FF)

val surfaceDimLight = Color(0xFFD9DADC)
val surfaceBrightLight = Color(0xFFF9F9FB)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF3F3F6)
val surfaceContainerLight = Color(0xFFEDEDF0)
val surfaceContainerHighLight = Color(0xFFE7E8EA)
val surfaceContainerHighestLight = Color(0xFFE2E2E5)

// ==========================================
// Material 3 Dark Scheme Colors
// ==========================================
val primaryDark = Color(0xFF7CD0FF)
val onPrimaryDark = Color(0xFF00344A)
val primaryContainerDark = Color(0xFF004C6A)
val onPrimaryContainerDark = Color(0xFFC3E8FF)

val secondaryDark = Color(0xFFB5C9D7)
val onSecondaryDark = Color(0xFF20333E)
val secondaryContainerDark = Color(0xFF364955)
val onSecondaryContainerDark = Color(0xFFD1E5F4)

val tertiaryDark = Color(0xFFCBC1E9)
val onTertiaryDark = Color(0xFF322C4C)
val tertiaryContainerDark = Color(0xFF494263)
val onTertiaryContainerDark = Color(0xFFE7DEFF)

val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)

val backgroundDark = Color(0xFF111415)
val onBackgroundDark = Color(0xFFE2E2E5)
val surfaceDark = Color(0xFF111415)
val onSurfaceDark = Color(0xFFE2E2E5)
val surfaceVariantDark = Color(0xFF40484D)
val onSurfaceVariantDark = Color(0xFFC0C7CD)
val surfaceTintDark = Color(0xFF7CD0FF)
val outlineDark = Color(0xFF8A9297)
val outlineVariantDark = Color(0xFF40484D)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE2E2E5)
val inverseOnSurfaceDark = Color(0xFF2E3133)
val inversePrimaryDark = Color(0xFF00668B)

val surfaceDimDark = Color(0xFF111415)
val surfaceBrightDark = Color(0xFF37393B)
val surfaceContainerLowestDark = Color(0xFF0C0F10)
val surfaceContainerLowDark = Color(0xFF191C1E)
val surfaceContainerDark = Color(0xFF1D2022)
val surfaceContainerHighDark = Color(0xFF272A2C)
val surfaceContainerHighestDark = Color(0xFF323537)

// ==========================================
// Source Brand Colors
// ==========================================
val SourceTruyenCvColor = Color(0xFF0288D1)
val SourceTruyenCvOnColor = Color(0xFFFFFFFF)
val SourceTruyenCvContainer = Color(0xFFE1F5FE)
val SourceTruyenCvOnContainer = Color(0xFF01579B)

val SourceMotTruyenColor = Color(0xFFF57C00)
val SourceMotTruyenOnColor = Color(0xFFFFFFFF)
val SourceMotTruyenContainer = Color(0xFFFFF3E0)
val SourceMotTruyenOnContainer = Color(0xFFE65100)

// ==========================================
// Status Badge Colors
// ==========================================
val StatusOngoingColor = Color(0xFF2E7D32)
val StatusOngoingContainer = Color(0xFFE8F5E9)
val StatusOngoingOnContainer = Color(0xFF1B5E20)

val StatusCompletedColor = Color(0xFF7B1FA2)
val StatusCompletedContainer = Color(0xFFF3E5F5)
val StatusCompletedOnContainer = Color(0xFF4A148C)

// ==========================================
// Shimmer Effect Colors
// ==========================================
val ShimmerBaseLight = Color(0xFFE0E0E0)
val ShimmerHighlightLight = Color(0xFFF5F5F5)

val ShimmerBaseDark = Color(0xFF242729)
val ShimmerHighlightDark = Color(0xFF383B3E)

// ==========================================
// 5 Reader Color Schemes
// ==========================================
/**
 * 5 Bộ màu dành riêng cho màn hình đọc truyện:
 * 1. Light (Trắng): Nền #FFFFFF, Text #212121
 * 2. Sepia (Kem/Giấy cũ): Nền #FBF0D9, Text #5F4B32
 * 3. Dark (Tối): Nền #1E1E1E, Text #E0E0E0
 * 4. AMOLED (Đen tuyệt đối): Nền #000000, Text #B0B0B0
 * 5. Green / E-ink (Bảo vệ mắt): Nền #E8F5E9, Text #2E7D32
 */
enum class ReaderTheme(
    val id: String,
    val title: String,
    val backgroundColor: Color,
    val textColor: Color,
    val secondaryTextColor: Color,
    val surfaceColor: Color
) {
    LIGHT(
        id = "light",
        title = "Trắng",
        backgroundColor = Color(0xFFFFFFFF),
        textColor = Color(0xFF212121),
        secondaryTextColor = Color(0xFF757575),
        surfaceColor = Color(0xFFF5F5F5)
    ),
    SEPIA(
        id = "sepia",
        title = "Giấy cũ",
        backgroundColor = Color(0xFFFBF0D9),
        textColor = Color(0xFF5F4B32),
        secondaryTextColor = Color(0xFF8D704B),
        surfaceColor = Color(0xFFF4E5C4)
    ),
    DARK(
        id = "dark",
        title = "Tối",
        backgroundColor = Color(0xFF1E1E1E),
        textColor = Color(0xFFE0E0E0),
        secondaryTextColor = Color(0xFF9E9E9E),
        surfaceColor = Color(0xFF2C2C2C)
    ),
    AMOLED(
        id = "amoled",
        title = "AMOLED",
        backgroundColor = Color(0xFF000000),
        textColor = Color(0xFFB0B0B0),
        secondaryTextColor = Color(0xFF616161),
        surfaceColor = Color(0xFF121212)
    ),
    GREEN(
        id = "green",
        title = "Bảo vệ mắt",
        backgroundColor = Color(0xFFE8F5E9),
        textColor = Color(0xFF2E7D32),
        secondaryTextColor = Color(0xFF4CAF50),
        surfaceColor = Color(0xFFDCEDC8)
    );

    companion object {
        fun fromId(id: String?): ReaderTheme {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: SEPIA
        }
    }
}
