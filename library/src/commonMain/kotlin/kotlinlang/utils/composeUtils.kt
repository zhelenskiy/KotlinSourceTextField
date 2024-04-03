package kotlinlang.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import com.zhelenskiy.library.generated.resources.Res
import com.zhelenskiy.library.generated.resources.jetbrains_mono_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


@Composable
internal expect fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    hoverColor: Color,
    unhoverColor: Color,
)

@Composable
internal expect fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    hoverColor: Color,
    unhoverColor: Color,
)

@OptIn(ExperimentalResourceApi::class)
public val jetbrainsMono: FontFamily @Composable get() = FontFamily(Font(Res.font.jetbrains_mono_regular))

internal expect fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
): Modifier

internal fun Modifier.size(size: Dp?) = if (size == null) this else size(size)
