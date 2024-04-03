package kotlinlang.compose

import androidx.compose.ui.graphics.Color

/**
 * Method for generating predefined light color scheme
 *
 * @param buttonColorScheme color scheme for `Replace` and `ReplaceAll` buttons
 * @return The newly generated color scheme
 */
public fun makeLightColorScheme(buttonColorScheme: ReplaceButtonsColorScheme): KotlinColorScheme {
    val keywordColor = Color(224, 90, 0)
    val darkYellow = Color(255, 215, 0)
    val mainColor = Color.Black
    val scrollbarsColorScheme = ScrollbarsColorScheme(
        hoveredColor = mainColor.copy(alpha = 0.5f),
        notHoveredColor = mainColor.copy(alpha = 0.25f),
    )
    val surfaceBackgroundColor = Color(255, 255, 210)
    val backgroundColor = Color.White
    val diagnosticsColorScheme = DiagnosticSeverityColorScheme(
        warningColor = darkYellow,
        errorColor = Color.Red,
        informationColor = Color.Gray,
    )
    return KotlinColorScheme(
        backgroundColor = backgroundColor,
        cursorColor = mainColor,
        lineNumbersColor = Color.DarkGray,
        indentationColor = Color.Gray,
        commentColor = Color.Gray,
        shebangColor = Color.Gray,
        identifierColor = mainColor,
        operatorColorScheme = OperatorColorScheme(
            regularOperatorColor = Color(194, 0, 194),
            rainbowBrackets = listOf(
                Color(220, 20, 60),
                Color(255, 165, 0),
                Color(243, 218, 11),
                Color(0, 179, 0),
                Color(0, 127, 255),
                Color.Blue,
                Color(139, 0, 255)
            ),
            matchedBracketsBackgroundColor = Color.LightGray,
            atColor = Color(244, 200, 0),
        ),
        keywordColorScheme = KeywordColorScheme(
            keywordColor = keywordColor
        ),
        literalColorScheme = LiteralColorScheme(
            textLiteralColorScheme = TextLiteralColorScheme(
                primaryColor = Color(0, 128, 0),
                secondaryColor = keywordColor,
            ),
            numberLiteralColorScheme = NumberLiteralColorScheme(
                numberColor = Color(65, 105, 225),
            ),
            nullLiteralColor = keywordColor,
            booleanLiteralColor = keywordColor,
        ),
        verbatimColor = mainColor,
        findAndReplaceColorScheme = FindAndReplaceColorScheme(
            textColor = mainColor,
            cursorColor = mainColor,
            placeHolderTextColor = Color.Gray,
            findReplaceFieldsSeparatorColor = Color.DarkGray,
            findReplaceSeparatorColor = Color.DarkGray,
            backgroundColor = backgroundColor,
            selectedSearchModeColor = Color.LightGray,
            errorColor = Color.Red,
            foundMatchColor = Color(0xFF, 0xE8, 0x7C),
            currentFoundMatchColor = Color(0xD4, 0xA0, 0x17),
            replaceButtonsColorScheme = buttonColorScheme,
        ),
        diagnosticsColorScheme = diagnosticsColorScheme,
        tooltipsColorScheme = TooltipsColorScheme(
            backgroundColor = surfaceBackgroundColor,
            textColor = mainColor,
            iconColorScheme = diagnosticsColorScheme,
            separatorColor = Color.Gray,
            shadowColor = mainColor,
            scrollbarsColorScheme = scrollbarsColorScheme,
        ),
        stickyHeaderSeparatorColor = Color.Gray,
        diagnosticsPopupColorScheme = DiagnosticsPopupColorScheme(
            diagnosticsHeaderColorScheme = DiagnosticsPopupHeaderColorScheme(
                backgroundColor = backgroundColor.copy(alpha = 0.5f),
                iconColorScheme = diagnosticsColorScheme,
                countLabelColor = mainColor,
            ),
            diagnosticsListColorScheme = DiagnosticsListColorScheme(
                backgroundColor = surfaceBackgroundColor,
                shadowColor = mainColor,
                separatorColor = Color.Gray,
                iconColorScheme = diagnosticsColorScheme,
                messageColor = mainColor,
                copyButtonColorScheme = CopyButtonColorScheme(
                    defaultColor = Color.DarkGray,
                    doneColor = Color.DarkGray,
                ),
                scrollbarsColorScheme = scrollbarsColorScheme,
            ),
        ),
        scrollbarsColorScheme = scrollbarsColorScheme,
    )
}

/**
 * Method for generating predefined dark color scheme
 *
 * @param buttonColorScheme color scheme for `Replace` and `ReplaceAll` buttons
 * @return The newly generated color scheme
 */
public fun makeDarkColorScheme(buttonColorScheme: ReplaceButtonsColorScheme): KotlinColorScheme {
    val keywordColor = Color(255, 140, 0)
    val darkYellow = Color(255, 215, 0)
    val mainColor = Color.LightGray
    val scrollbarsColorScheme = ScrollbarsColorScheme(
        hoveredColor = mainColor.copy(alpha = 0.5f),
        notHoveredColor = mainColor.copy(alpha = 0.25f),
    )
    val surfaceBackgroundColor = Color(50, 50, 50)
    val backgroundColor = Color(30, 30, 30)
    val errorColor = Color(200, 0, 0)
    val diagnosticsColorScheme = DiagnosticSeverityColorScheme(
        warningColor = darkYellow,
        errorColor = errorColor,
        informationColor = Color.LightGray,
    )
    return KotlinColorScheme(
        backgroundColor = backgroundColor,
        cursorColor = mainColor,
        lineNumbersColor = Color(180, 180, 180),
        indentationColor = Color.Gray,
        commentColor = Color.Gray,
        shebangColor = Color.Gray,
        identifierColor = mainColor,
        operatorColorScheme = OperatorColorScheme(
            regularOperatorColor = Color.Magenta,
            rainbowBrackets = listOf(
                Color(245, 0, 41),
                Color(255, 165, 0),
                Color(243, 218, 11),
                Color(0, 204, 0),
                Color(51, 153, 255),
                Color(90, 90, 255),
                Color(186, 102, 255),
            ),
            matchedBracketsBackgroundColor = Color.DarkGray,
            atColor = Color(200, 200, 0),
        ),
        keywordColorScheme = KeywordColorScheme(
            keywordColor = keywordColor
        ),
        literalColorScheme = LiteralColorScheme(
            textLiteralColorScheme = TextLiteralColorScheme(
                primaryColor = Color(0, 179, 0),
                secondaryColor = keywordColor,
            ),
            numberLiteralColorScheme = NumberLiteralColorScheme(
                numberColor = Color(90, 90, 255),
            ),
            nullLiteralColor = keywordColor,
            booleanLiteralColor = keywordColor,
        ),
        verbatimColor = mainColor,
        findAndReplaceColorScheme = FindAndReplaceColorScheme(
            textColor = mainColor,
            cursorColor = mainColor,
            placeHolderTextColor = Color.Gray,
            findReplaceFieldsSeparatorColor = Color.LightGray,
            findReplaceSeparatorColor = Color.LightGray,
            backgroundColor = backgroundColor,
            selectedSearchModeColor = Color.DarkGray,
            errorColor = errorColor,
            foundMatchColor = Color(0xFF, 0xE8, 0x7C),
            currentFoundMatchColor = Color(0xD4, 0xA0, 0x17),
            replaceButtonsColorScheme = buttonColorScheme,
        ),
        diagnosticsColorScheme = diagnosticsColorScheme,
        tooltipsColorScheme = TooltipsColorScheme(
            backgroundColor = surfaceBackgroundColor,
            textColor = mainColor,
            iconColorScheme = diagnosticsColorScheme,
            separatorColor = Color.Gray,
            shadowColor = mainColor,
            scrollbarsColorScheme = scrollbarsColorScheme,
        ),
        stickyHeaderSeparatorColor = Color.Gray,
        diagnosticsPopupColorScheme = DiagnosticsPopupColorScheme(
            diagnosticsHeaderColorScheme = DiagnosticsPopupHeaderColorScheme(
                backgroundColor = backgroundColor.copy(alpha = 0.5f),
                iconColorScheme = diagnosticsColorScheme,
                countLabelColor = mainColor,
            ),
            diagnosticsListColorScheme = DiagnosticsListColorScheme(
                backgroundColor = surfaceBackgroundColor,
                shadowColor = mainColor,
                separatorColor = Color.Gray,
                iconColorScheme = diagnosticsColorScheme,
                messageColor = mainColor,
                copyButtonColorScheme = CopyButtonColorScheme(
                    defaultColor = Color.Gray,
                    doneColor = Color.Gray,
                ),
                scrollbarsColorScheme = scrollbarsColorScheme,
            ),
        ),
        scrollbarsColorScheme = scrollbarsColorScheme,
    )
}
