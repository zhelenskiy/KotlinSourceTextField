package kotlinlang.compose

import editor.basic.BasicSourceCodeTextFieldState
import editor.basic.CharEventHandler
import editor.basic.Token
import editor.basic.openingBracketCharEventHandler

internal fun <T : Token> openingMultiLineStringQuoteCharHandler(
    textFieldState: BasicSourceCodeTextFieldState<T>,
    indent: String? = " ".repeat(4),
): CharEventHandler = f@{ keyEvent ->
    val oldSelection = textFieldState.selection
    if (!oldSelection.collapsed || oldSelection.end < 2) return@f null
    if (textFieldState.text[oldSelection.end - 1] != '"' || textFieldState.text[oldSelection.end - 2] != '"') return@f null
    openingBracketCharEventHandler(
        textFieldState = textFieldState,
        openingChar = '"',
        openingBracket = "\"",
        closingBracket = "\"\"\"",
        addNewLinesForSelection = { false },
        indent = indent,
    )(keyEvent)
}
