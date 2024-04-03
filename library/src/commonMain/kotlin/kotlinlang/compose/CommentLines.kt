package kotlinlang.compose

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import editor.basic.BasicSourceCodeTextFieldState
import editor.basic.CharEvent
import editor.basic.CharEventHandler

internal fun commentBlock(
    state: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    char: Char = '/',
): CharEventHandler = f@{ charEvent ->
    if (charEvent !is CharEvent.Insert || charEvent.char != char || state.selection.collapsed) return@f null

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
