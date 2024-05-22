package kotlinlang.compose

import editor.basic.*

internal fun <T : Token> openingMultiLineStringQuoteCharHandler(
    textFieldState: BasicSourceCodeTextFieldState<T>,
    indent: String? = " ".repeat(4),
): KeyboardEventHandler = f@{ keyboardEvent ->
    val oldSelection = textFieldState.selection
    if (!oldSelection.collapsed || oldSelection.end < 2) return@f null
    if (textFieldState.text[oldSelection.end - 1] != '"' || textFieldState.text[oldSelection.end - 2] != '"') return@f null
    val ending = if (oldSelection.end >= 3 && textFieldState.text[oldSelection.end - 3] == '"') "" else "\"\"\""
    openingBracketEventHandler(
        textFieldState = textFieldState,
        openingChar = '"',
        openingBracket = "\"",
        closingBracket = ending,
        addNewLinesForSelection = { false },
        indent = indent,
    )(keyboardEvent)
}
