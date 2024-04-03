package kotlinlang.compose

import kotlinx.serialization.Serializable
import androidx.compose.ui.graphics.Color as ComposeColor

internal typealias Color = @Serializable(ColorSerializer::class) ComposeColor

@Serializable
public data class KotlinColorScheme(
    val backgroundColor: Color,
    val cursorColor: Color,
    val stickyHeaderSeparatorColor: Color,
    val lineNumbersColor: Color,
    val indentationColor: Color,
    val commentColor: Color,
    val shebangColor: Color = commentColor,
    val identifierColor: Color,
    val operatorColorScheme: OperatorColorScheme,
    val keywordColorScheme: KeywordColorScheme,
    val literalColorScheme: LiteralColorScheme,
    val verbatimColor: Color,
    val findAndReplaceColorScheme: FindAndReplaceColorScheme,
    val diagnosticsColorScheme: DiagnosticSeverityColorScheme,
    val tooltipsColorScheme: TooltipsColorScheme,
    val diagnosticsPopupColorScheme: DiagnosticsPopupColorScheme,
    val scrollbarsColorScheme: ScrollbarsColorScheme,
)

@Serializable
public data class ScrollbarsColorScheme(
    val hoveredColor: Color,
    val notHoveredColor: Color,
)

@Serializable
public data class DiagnosticsPopupColorScheme(
    val diagnosticsHeaderColorScheme: DiagnosticsPopupHeaderColorScheme,
    val diagnosticsListColorScheme: DiagnosticsListColorScheme,
)

@Serializable
public data class DiagnosticsPopupHeaderColorScheme(
    val backgroundColor: Color,
    val iconColorScheme: DiagnosticSeverityColorScheme,
    val countLabelColor: Color,
)

@Serializable
public data class DiagnosticsListColorScheme(
    val backgroundColor: Color,
    val shadowColor: Color,
    val separatorColor: Color,
    val iconColorScheme: DiagnosticSeverityColorScheme,
    val messageColor: Color,
    val copyButtonColorScheme: CopyButtonColorScheme,
    val scrollbarsColorScheme: ScrollbarsColorScheme,
)

@Serializable
public data class CopyButtonColorScheme(
    val defaultColor: Color,
    val doneColor: Color = defaultColor,
)

@Serializable
public data class TooltipsColorScheme(
    val backgroundColor: Color,
    val textColor: Color,
    val iconColorScheme: DiagnosticSeverityColorScheme,
    val separatorColor: Color,
    val shadowColor: Color,
    val scrollbarsColorScheme: ScrollbarsColorScheme,
)

@Serializable
public data class FindAndReplaceColorScheme(
    val textColor: Color,
    val cursorColor: Color,
    val placeHolderTextColor: Color,
    val findReplaceFieldsSeparatorColor: Color,
    val findReplaceSeparatorColor: Color,
    val backgroundColor: Color,
    val selectedSearchModeColor: Color,
    val errorColor: Color,
    val foundMatchColor: Color,
    val currentFoundMatchColor: Color,
    val replaceButtonsColorScheme: ReplaceButtonsColorScheme,
)

@Serializable
public data class ReplaceButtonsColorScheme(
    val backgroundColor: Color,
    val textColor: Color,
)

@Serializable
public data class OperatorColorScheme(
    val regularOperatorColor: Color,
    val rainbowBrackets: List<Color> = emptyList(),
    val matchedBracketsBackgroundColor: Color = Color.Transparent,
    val atColor: Color = regularOperatorColor,
)

@Serializable
public data class KeywordColorScheme(
    val softKeywordColor: Color,
    val hardKeywordColor: Color,
) {
    public constructor(keywordColor: Color) : this(keywordColor, keywordColor)
}

@Serializable
public data class LiteralColorScheme(
    val textLiteralColorScheme: TextLiteralColorScheme,
    val numberLiteralColorScheme: NumberLiteralColorScheme,
    val nullLiteralColor: Color,
    val booleanLiteralColor: Color,
)

@Serializable
public data class NumberLiteralColorScheme(
    val integralNumberColor: Color,
    val realNumberColor: Color,
) {
    public constructor(numberColor: Color): this(numberColor, numberColor)
}

@Serializable
public data class TextLiteralColorScheme(
    val regularLiteralColor: Color,
    val openingQuoteColor: Color,
    val closingQuoteColor: Color,
    val escapeSequenceColor: Color,
    val fieldTemplateColor: Color,
    val expressionTemplateStartColor: Color,
    val expressionTemplateEndColor: Color,
) {
    public constructor(primaryColor: Color, secondaryColor: Color) : this(
        regularLiteralColor = primaryColor,
        openingQuoteColor = primaryColor,
        closingQuoteColor = primaryColor,
        escapeSequenceColor = secondaryColor,
        fieldTemplateColor = secondaryColor,
        expressionTemplateStartColor = secondaryColor,
        expressionTemplateEndColor = secondaryColor,
    )
}

@Serializable
public data class DiagnosticSeverityColorScheme(
    val errorColor: Color,
    val warningColor: Color,
    val informationColor: Color,
)
