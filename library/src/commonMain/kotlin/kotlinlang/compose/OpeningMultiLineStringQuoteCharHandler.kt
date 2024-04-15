package kotlinlang.compose

import editor.basic.*

internal fun <T : Token> openingMultiLineStringQuoteCharHandler(
    textFieldState: BasicSourceCodeTextFieldState<T>,
    indent: String? = " ".repeat(4),
): KeyboardEventHandler = f@{ keyboardEvent ->
    val oldSelection = textFieldState.selection
    if (!oldSelection.collapsed || oldSelection.end < 2) return@f null
    if (textFieldState.text[oldSelection.end - 1] != '"' || textFieldState.text[oldSelection.end - 2] != '"') return@f null
    openingBracketEventHandler(
        textFieldState = textFieldState,
        openingChar = '"',
        openingBracket = "\"",
        closingBracket = "\"\"\"",
        addNewLinesForSelection = { false },
        indent = indent,
    )(keyboardEvent)
}
