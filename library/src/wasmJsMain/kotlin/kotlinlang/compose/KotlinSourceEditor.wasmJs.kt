package kotlinlang.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

@Composable
internal actual fun TooltipBox(
    tooltip: @Composable () -> Unit,
    delayMillis: Int,
    offset: IntOffset,
    modifier: Modifier,
    content: @Composable () -> Unit
) = Box(modifier) { content() }
