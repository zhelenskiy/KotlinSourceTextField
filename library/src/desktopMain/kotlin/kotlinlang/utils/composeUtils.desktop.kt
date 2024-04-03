package kotlinlang.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@Composable
internal actual fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    hoverColor: Color,
    unhoverColor: Color,
) {
    if (scrollState.maxValue > 0) {
        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            modifier,
            style = defaultScrollbarStyle().copy(
                hoverColor = hoverColor,
                unhoverColor = unhoverColor
            )
        )
    }
}

@Composable
internal actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    hoverColor: Color,
    unhoverColor: Color,
) {
    if (scrollState.maxValue > 0) {
        HorizontalScrollbar(
            rememberScrollbarAdapter(scrollState),
            modifier,
            style = defaultScrollbarStyle().copy(
                hoverColor = hoverColor,
                unhoverColor = unhoverColor
            )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
): Modifier = onPointerEvent(eventType, pass, onEvent)
