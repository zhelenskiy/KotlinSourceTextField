package kotlinlang.compose

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import editor.basic.BasicSourceCodeTextFieldState
import editor.basic.KeyboardEventFilter
import editor.basic.KeyboardEventHandler
import editor.basic.Token

internal fun <T : Token> handleDuplicateLinesAndStrings(
    state: BasicSourceCodeTextFieldState<T>,
    keyboardEventFilter: KeyboardEventFilter,
): KeyboardEventHandler = f@{ keyboardEvent ->
    if (!keyboardEventFilter(keyboardEvent)) return@f null

    val copySelection = if (state.selection.collapsed) {
        val lineStart = generateSequence(state.selection.start - 1) { it - 1 }
            .takeWhile { it >= 0 }
            .firstOrNull { state.text[it] in "\n\r" }?.let(Int::inc) ?: 0
        val lineEnd = generateSequence(state.selection.start) { it + 1 }
            .takeWhile { it < state.text.length }
            .firstOrNull { state.text[it] in "\n\r" } ?: state.text.length
        TextRange(lineStart, lineEnd)
    } else {
        state.selection
    }
    val newText = buildAnnotatedString {
        append(state.annotatedString, 0, copySelection.min)
        append(state.annotatedString, copySelection.min, copySelection.max)
        if (state.selection.collapsed) append('\n')
        append(state.annotatedString, copySelection.min, copySelection.max)
        append(state.annotatedString, copySelection.max, state.text.length)
    }
    val length = copySelection.length + if (state.selection.collapsed) 1 else 0
    val newSelection = TextRange(
        state.selection.start + length,
        state.selection.end + length
    )
    val newComposition = state.composition?.let {
        val newStart =
            if (it.start < copySelection.min) it.start else it.start + length
        val newEnd =
            if (it.end < copySelection.min) it.end else it.end + length
        TextRange(newStart, newEnd)
    }
    TextFieldValue(newText, newSelection, newComposition)
}
