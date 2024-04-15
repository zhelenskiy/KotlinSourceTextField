package kotlinlang.compose

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import editor.basic.*

internal fun commentBlock(
    state: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    keyboardEventFilter: KeyboardEventFilter,
): KeyboardEventHandler = f@{ keyboardEvent ->
    if (!keyboardEventFilter(keyboardEvent) || state.selection.collapsed) return@f null

    val min = state.selection.min
    val max = state.selection.max
    val startCharsBeforeAndAfter = when {
        state.selection.length >= 2 && state.text[min] == '/' && state.text[min + 1] == '*' -> 0 to 2
        min >= 2 && state.text[min - 2] == '/' && state.text[min - 1] == '*' -> 2 to 0
        state.selection.length >= 1 && min >= 1 && state.text[min - 1] == '/' && state.text[min] == '*' -> 1 to 1
        else -> null
    }
    val endCharsBeforeAndAfter = when {
        state.selection.length >= 2 && state.text[max - 2] == '*' && state.text[max - 1] == '/' -> 2 to 0
        max + 2 < state.text.length && state.text[max] == '*' && state.text[max + 1] == '/' -> 0 to 2
        state.selection.length >= 1 && max + 1 < state.text.length && state.text[max - 1] == '*' && state.text[max] == '/' -> 1 to 1
        else -> null
    }
    if (startCharsBeforeAndAfter != null && endCharsBeforeAndAfter != null) {
        val (startCharsBefore, startCharsAfter) = startCharsBeforeAndAfter
        val (endCharsBefore, endCharsAfter) = endCharsBeforeAndAfter
        val newText = buildAnnotatedString {
            append(state.annotatedString, 0, min - startCharsBefore)
            append(state.annotatedString, min + startCharsAfter, max - endCharsBefore)
            append(state.annotatedString, max + endCharsAfter, state.text.length)
        }

        fun convertOffset(oldOffset: Int) = when {
            oldOffset <= min - startCharsBefore -> oldOffset
            oldOffset <= min + startCharsAfter -> min - startCharsBefore
            oldOffset < max - endCharsBefore -> oldOffset - 2
            oldOffset < max + endCharsAfter -> max - endCharsBefore - 2
            else -> oldOffset - 4
        }

        TextFieldValue(
            annotatedString = newText,
            selection = state.selection.let {
                TextRange(convertOffset(it.start), convertOffset(it.end))
            },
            composition = state.composition?.let {
                TextRange(convertOffset(it.start), convertOffset(it.end))
            },
        )
    } else {
        val newText = buildAnnotatedString {
            append(state.annotatedString, 0, min)
            append("/*")
            append(state.annotatedString, min, max)
            append("*/")
            append(state.annotatedString, max, state.text.length)
        }

        fun convertOffset(oldOffset: Int) = when {
            oldOffset <= min -> oldOffset
            oldOffset < max -> oldOffset + 2
            else -> oldOffset + 4
        }

        TextFieldValue(
            annotatedString = newText,
            selection = state.selection.let {
                TextRange(convertOffset(it.start), convertOffset(it.end))
            },
            composition = state.composition?.let {
                TextRange(convertOffset(it.start), convertOffset(it.end))
            },
        )
    }
}

internal fun commentLines(
    state: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    keyboardEventFilter: KeyboardEventFilter,
): KeyboardEventHandler = f@{ keyboardEvent ->
    if (!keyboardEventFilter(keyboardEvent)) return@f null
    val min = state.selection.min
    val max = state.selection.max
    val minLine = state.sourceCodePositions[min].line
    val maxLine = state.sourceCodePositions[max].line
    val isCommented = (minLine..maxLine).all {
        val contentStart = state.lineOffsets[it] ?: return@all false
        if (contentStart !in state.offsets[it].indices || contentStart + 1 !in state.offsets[it].indices) return@all false
        val commentOffset = state.offsets[it][contentStart]
        state.text[commentOffset] == '/' && state.text[commentOffset + 1] == '/'
    }
    var selectionStart = state.selection.start
    var selectionEnd = state.selection.end
    val composition = state.composition
    var compositionStart = composition?.start ?: 0
    var compositionEnd = composition?.end ?: 0
    val newText = if (isCommented) {
        buildAnnotatedString {
            append(state.annotatedString, 0, state.offsets[minLine].first())
            for (line in minLine..maxLine) {
                val minOffset = state.offsets[line].first()
                val contentOffset = minOffset + state.lineOffsets[line]!!
                val maxOffset = state.offsets[line].last()
                append(state.annotatedString, minOffset, contentOffset)
                append(state.annotatedString, contentOffset + 2, maxOffset)
                if (line != maxLine) append(state.text, maxOffset, maxOffset + 1)
                if (state.selection.start >= contentOffset + 2) selectionStart -= 2
                else if (state.selection.start == contentOffset + 1) selectionStart--
                if (state.selection.end >= contentOffset + 2) selectionEnd -= 2
                else if (state.selection.end == contentOffset + 1) selectionEnd--
                if (composition != null) {
                    if (composition.start >= contentOffset + 2) compositionStart -= 2
                    else if (composition.start == contentOffset + 1) compositionStart--
                    if (composition.end >= contentOffset + 2) compositionEnd -= 2
                    else if (composition.end == contentOffset + 1) compositionEnd--
                }
            }
            append(state.annotatedString, state.offsets[maxLine].last(), state.text.length)
        }
    } else {
        buildAnnotatedString {
            append(state.annotatedString, 0, state.offsets[minLine].first())
            for (line in minLine..maxLine) {
                append("//")
                val minOffset = state.offsets[line].first()
                val maxOffset = state.offsets[line].last()
                append(state.annotatedString, minOffset, maxOffset)
                if (line != maxLine) append(state.text, maxOffset, maxOffset + 1)
                if (state.selection.start >= minOffset) selectionStart += 2
                if (state.selection.end >= minOffset) selectionEnd += 2
                if (composition != null) {
                    if (composition.start >= minOffset) compositionStart += 2
                    if (composition.end >= minOffset) compositionEnd += 2
                }
            }
            append(state.annotatedString, state.offsets[maxLine].last(), state.text.length)
        }
    }
    TextFieldValue(
        annotatedString = newText,
        selection = TextRange(selectionStart, selectionEnd),
        composition = if (composition != null) TextRange(compositionStart, compositionEnd) else null,
    )
}
