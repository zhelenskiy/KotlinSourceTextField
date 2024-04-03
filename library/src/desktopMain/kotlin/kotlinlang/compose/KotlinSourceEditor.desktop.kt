package kotlinlang.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal actual fun TooltipBox(
    tooltip: @Composable () -> Unit,
    delayMillis: Int,
    offset: IntOffset,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var tooltipSize by remember { mutableStateOf(IntSize(0, 0)) }
    val windowSize = LocalWindowInfo.current.containerSize
    var tooltipAreaPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    val tooltipPosition = tooltipAreaPosition + Offset(offset.x.toFloat(), offset.y.toFloat())
    val alignToBottom = tooltipPosition.y + tooltipSize.height < windowSize.height
    TooltipArea(
        tooltip = {
            Box(modifier = Modifier.onSizeChanged { tooltipSize = it }) {
                tooltip()
            }
        },
        modifier = modifier
            .onGloballyPositioned { tooltipAreaPosition = it.positionInWindow() },
        delayMillis = delayMillis,
        tooltipPlacement = TooltipPlacement.ComponentRect(
            anchor = Alignment.TopStart,
            alignment = if (alignToBottom) Alignment.BottomEnd else Alignment.TopEnd,
            offset = density.run { DpOffset(offset.x.toDp(), offset.y.toDp()) },
        ),
        content = content,
    )
}
