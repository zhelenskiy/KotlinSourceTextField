package kotlinlang.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


public data class DiagnosticDescriptor(
    val message: String,
    val severity: DiagnosticSeverity,
    val interval: TextInterval?
)

public enum class DiagnosticSeverity {
    ERROR,
    WARNING,
    INFO,
    ;

    public val imageVector: ImageVector
        get() = when (this) {
            ERROR -> Icons.Default.Error
            WARNING -> Icons.Default.Warning
            INFO -> Icons.Default.Info
        }

    public fun getColor(colorScheme: DiagnosticSeverityColorScheme): Color = when (this) {
        ERROR -> colorScheme.errorColor
        WARNING -> colorScheme.warningColor
        INFO -> colorScheme.informationColor
    }
}

@Composable
public fun DiagnosticSeverity.icon(
    colorScheme: DiagnosticSeverityColorScheme,
    contentDescription: String?,
    modifier: Modifier = Modifier,
): Unit = Icon(
    imageVector = imageVector,
    contentDescription = contentDescription,
    tint = getColor(colorScheme),
    modifier = modifier,
)


@Serializable
public data class TextInterval(val start: TextPosition, val end: TextPosition) {
    @Serializable
    public data class TextPosition(val line: Int, val ch: Int) : Comparable<TextPosition> {
        override fun compareTo(other: TextPosition): Int = compareValuesBy(this, other, { it.line }, { it.ch })
    }
}
