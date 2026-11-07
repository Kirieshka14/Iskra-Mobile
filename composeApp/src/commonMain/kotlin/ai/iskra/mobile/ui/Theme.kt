package ai.iskra.mobile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class PaletteOption(
    val id: String,
    val label: String,
    val darkAccent: Color,
    val lightAccent: Color,
    val darkCanvas: Color,
    val darkSurface: Color,
    val darkSurface2: Color
)

val ISKRA_PALETTES = listOf(
    PaletteOption("classic", "Классика", Color(0xFFF5F5F4), Color(0xFF181816), Color(0xFF131313), Color(0xFF1A1A1A), Color(0xFF232323)),
    PaletteOption("lavender", "Лаванда", Color(0xFFA78BFA), Color(0xFF6D4AFF), Color(0xFF12101B), Color(0xFF1A1726), Color(0xFF231F33)),
    PaletteOption("terracotta", "Терракота", Color(0xFFEA7A57), Color(0xFFC85A38), Color(0xFF181210), Color(0xFF221A17), Color(0xFF2D221E)),
    PaletteOption("blue", "Океан", Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF0E131B), Color(0xFF151C28), Color(0xFF1D2636)),
    PaletteOption("emerald", "Изумруд", Color(0xFF34D399), Color(0xFF059669), Color(0xFF0E1613), Color(0xFF14201C), Color(0xFF1B2B25))
)

val GENRE_LABELS = mapOf(
    "all" to "Все",
    "fantasy" to "Фэнтези",
    "romance" to "Романтика",
    "sci_fi" to "Научная фантастика",
    "horror" to "Хоррор",
    "drama" to "Драма",
    "comedy" to "Комедия",
    "slice_of_life" to "Повседневность",
    "historical" to "История",
    "other" to "Другое"
)

val AFFINITY_LABELS = mapOf(
    "hatred" to "Ненависть",
    "distrust" to "Недоверие",
    "neutral" to "Нейтрально",
    "trust" to "Доверие",
    "love" to "Любовь"
)

@Composable
fun IskraMobileTheme(
    themeMode: String = "dark",
    paletteId: String = "classic",
    content: @Composable () -> Unit
) {
    val palette = ISKRA_PALETTES.find { it.id == paletteId } ?: ISKRA_PALETTES.first()
    val isDark = themeMode != "light"

    val scheme = if (isDark) {
        darkColorScheme(
            primary = palette.darkAccent,
            onPrimary = Color(0xFF141414),
            background = palette.darkCanvas,
            surface = palette.darkSurface,
            surfaceVariant = palette.darkSurface2,
            onBackground = Color(0xFFF5F5F4),
            onSurface = Color(0xFFF5F5F4),
            onSurfaceVariant = Color(0xFFA3A3A3),
            outline = Color(0xFF2E2E2E)
        )
    } else {
        lightColorScheme(
            primary = palette.lightAccent,
            onPrimary = Color(0xFFFAF9F7),
            background = Color(0xFFF7F6F3),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0EEEA),
            onBackground = Color(0xFF181816),
            onSurface = Color(0xFF181816),
            onSurfaceVariant = Color(0xFF6B6A65),
            outline = Color(0xFFE5E2DC)
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}
