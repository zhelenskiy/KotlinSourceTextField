package kotlinlang.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import editor.basic.ScopeChange
import editor.basic.ScopeChangingToken
import editor.basic.SingleStyleToken
import editor.basic.SymbolToken
import editor.basic.Token
import editor.basic.WhiteSpaceToken
import kotlinlang.tokens.KotlinToken
import kotlinlang.tokens.KotlinToken.Literal.Text.CharLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.CharLiteralStart
import kotlinlang.tokens.KotlinToken.Literal.Text.MultiLineStringLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.MultiLineStringLiteralStart
import kotlinlang.tokens.KotlinToken.Literal.Text.SingleLineStringLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.SingleLineStringLiteralStart
import kotlinlang.tokens.KotlinToken.Literal.Text.StringExpressionTemplateEndLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.StringExpressionTemplateStartLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.StringFieldTemplateStartLiteral


public interface SingleStyleTokenChangingScope : SingleStyleToken, ScopeChangingToken
public sealed class KotlinComposeToken(public val kotlinToken: KotlinToken, color: Color) : SingleStyleToken {
    override val text: String get() = kotlinToken.string
    override var style: SpanStyle = SpanStyle(color = color)

    public class NewLine(newLine: KotlinToken.NewLine) :
        KotlinComposeToken(newLine, Color.Unspecified), WhiteSpaceToken

    public class ShebangLine(colorScheme: KotlinColorScheme, comment: KotlinToken.ShebangLine) :
        KotlinComposeToken(comment, colorScheme.shebangColor)

    public class DelimitedComment(colorScheme: KotlinColorScheme, comment: KotlinToken.DelimitedComment) :
        KotlinComposeToken(comment, colorScheme.commentColor)

    public class LineComment(colorScheme: KotlinColorScheme, comment: KotlinToken.LineComment) :
        KotlinComposeToken(comment, colorScheme.commentColor)

    public class WhiteSpace(whiteSpace: KotlinToken.WhiteSpace) :
        KotlinComposeToken(whiteSpace, Color.Unspecified), WhiteSpaceToken

    public class Identifier(
        colorScheme: KotlinColorScheme,
        private val identifier: KotlinToken.Identifier,
        isAtNearby: Boolean = false,
    ) : SymbolToken<Identifier>, KotlinComposeToken(
        kotlinToken = identifier,
        color = if (isAtNearby) colorScheme.operatorColorScheme.atColor else colorScheme.identifierColor,
    ) {
        override fun isSameSymbolWith(symbol: Identifier): Boolean =
            this.identifier.unquoted == symbol.identifier.unquoted
    }

    public sealed class Operator private constructor(
        colorScheme: KotlinColorScheme, operator: KotlinToken.Operator
    ) : KotlinComposeToken(
        kotlinToken = operator,
        color = when (operator) {
            KotlinToken.Operator.At -> colorScheme.operatorColorScheme.atColor
            else -> colorScheme.operatorColorScheme.regularOperatorColor
        }
    ) {
        public companion object {
            public operator fun invoke(colorScheme: KotlinColorScheme, operator: KotlinToken.Operator): Operator =
                when (operator) {
                    KotlinToken.Operator.LParen -> OpeningParenthesis(colorScheme)
                    KotlinToken.Operator.RParen -> ClosingParenthesis(colorScheme)
                    KotlinToken.Operator.LSquare -> OpeningBracket(colorScheme)
                    KotlinToken.Operator.RSquare -> ClosingBracket(colorScheme)
                    KotlinToken.Operator.LCurl -> OpeningBrace(colorScheme)
                    KotlinToken.Operator.RCurl -> ClosingBrace(colorScheme)
                    else -> RegularOperator(colorScheme, operator)
                }
        }

        public class RegularOperator internal constructor(
            colorScheme: KotlinColorScheme,
            operator: KotlinToken.Operator
        ) : Operator(colorScheme, operator)

        public class OpeningParenthesis(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.LParen), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.OpensScope
            override fun matches(token: Token): Boolean = token is ClosingParenthesis
        }

        public class ClosingParenthesis(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.RParen), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.ClosesScope
            override fun matches(token: Token): Boolean = token is OpeningParenthesis
        }

        public class OpeningBracket(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.LSquare), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.OpensScope
            override fun matches(token: Token): Boolean = token is ClosingBracket
        }

        public class ClosingBracket(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.RSquare), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.ClosesScope
            override fun matches(token: Token): Boolean = token is OpeningBracket
        }

        public class OpeningBrace(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.LCurl), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.OpensScope
            override fun matches(token: Token): Boolean = token is ClosingBrace
        }

        public class ClosingBrace(colorScheme: KotlinColorScheme) :
            Operator(colorScheme, KotlinToken.Operator.RCurl), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.ClosesScope
            override fun matches(token: Token): Boolean = token is OpeningBrace
        }
    }

    public sealed class Keyword(keyword: KotlinToken.Keyword, color: Color) :
        KotlinComposeToken(keyword, color)

    public class SoftKeyword(colorScheme: KotlinColorScheme, keyword: KotlinToken.SoftKeyword) :
        Keyword(keyword = keyword, color = colorScheme.keywordColorScheme.softKeywordColor)

    public class HardKeyword(colorScheme: KotlinColorScheme, keyword: KotlinToken.HardKeyword) :
        Keyword(keyword = keyword, color = colorScheme.keywordColorScheme.hardKeywordColor)

    public sealed class Literal(kotlinToken: KotlinToken, color: Color) :
        KotlinComposeToken(kotlinToken, color) {
        public sealed class NumberLiteral(
            kotlinToken: KotlinToken.Literal.NumberLiteral,
            numberColor: Color,
        ) : Literal(kotlinToken, numberColor)

        public class IntegralNumberLiteral(
            colorScheme: KotlinColorScheme,
            kotlinToken: KotlinToken.Literal.IntegralNumberLiteral
        ) : Literal(
            kotlinToken = kotlinToken,
            color = colorScheme.literalColorScheme.numberLiteralColorScheme.integralNumberColor
        )

        public class RealNumberLiteral(
            colorScheme: KotlinColorScheme,
            kotlinToken: KotlinToken.Literal.RealNumberLiteral
        ) : Literal(
            kotlinToken = kotlinToken,
            color = colorScheme.literalColorScheme.numberLiteralColorScheme.realNumberColor,
        )

        public class BooleanLiteral(
            colorScheme: KotlinColorScheme,
            kotlinToken: KotlinToken.Literal.BooleanLiteral
        ) : Literal(
            kotlinToken = kotlinToken,
            color = colorScheme.literalColorScheme.booleanLiteralColor,
        )

        public class NullLiteral(colorScheme: KotlinColorScheme) : Literal(
            kotlinToken = KotlinToken.Literal.NullLiteral,
            color = colorScheme.literalColorScheme.nullLiteralColor,
        )


        public sealed class OpeningQuote(
            colorScheme: KotlinColorScheme,
            thisQuote: KotlinToken,
            private val pairedQuote: KotlinToken
        ) : Literal(
            kotlinToken = thisQuote,
            color = colorScheme
                .literalColorScheme
                .textLiteralColorScheme
                .openingQuoteColor
        ), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange
                get() = ScopeChange.OpensScope

            override fun matches(token: Token): Boolean {
                return token is KotlinComposeToken && token.kotlinToken == pairedQuote
            }
        }

        public sealed class ClosingQuote(
            colorScheme: KotlinColorScheme,
            thisQuote: KotlinToken,
            private val pairedQuote: KotlinToken
        ) : Literal(
            kotlinToken = thisQuote,
            color = colorScheme
                .literalColorScheme
                .textLiteralColorScheme
                .closingQuoteColor
        ), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange
                get() = ScopeChange.ClosesScope

            override fun matches(token: Token): Boolean {
                return token is KotlinComposeToken && token.kotlinToken == pairedQuote
            }
        }

        public class CharLiteralStart(colorScheme: KotlinColorScheme) : OpeningQuote(
            colorScheme = colorScheme,
            thisQuote = CharLiteralStart,
            pairedQuote = CharLiteralEnd,
        )

        public class SingleLineStringLiteralStart(colorScheme: KotlinColorScheme) : OpeningQuote(
            colorScheme = colorScheme,
            thisQuote = SingleLineStringLiteralStart,
            pairedQuote = SingleLineStringLiteralEnd,
        )

        public class MultiLineStringLiteralStart(colorScheme: KotlinColorScheme) : OpeningQuote(
            colorScheme = colorScheme,
            thisQuote = MultiLineStringLiteralStart,
            pairedQuote = MultiLineStringLiteralEnd,
        )

        public class CharLiteralEnd(colorScheme: KotlinColorScheme) : ClosingQuote(
            colorScheme = colorScheme,
            thisQuote = CharLiteralEnd,
            pairedQuote = CharLiteralStart,
        )

        public class SingleLineStringLiteralEnd(colorScheme: KotlinColorScheme) : ClosingQuote(
            colorScheme,
            thisQuote = SingleLineStringLiteralEnd,
            pairedQuote = SingleLineStringLiteralStart,
        )

        public class MultiLineStringLiteralEnd(colorScheme: KotlinColorScheme) : ClosingQuote(
            colorScheme = colorScheme,
            thisQuote = MultiLineStringLiteralEnd,
            pairedQuote = MultiLineStringLiteralStart,
        )

        public class EscapedStringLiteral(
            colorScheme: KotlinColorScheme,
            escapedChar: KotlinToken.Literal.Text.EscapedStringLiteral
        ) : Literal(
            kotlinToken = escapedChar,
            color = colorScheme.literalColorScheme.textLiteralColorScheme.escapeSequenceColor
        )

        public class RegularLiteral(
            colorScheme: KotlinColorScheme, textLiteral: KotlinToken.Literal.Text.RegularLiteral
        ) : Literal(
            kotlinToken = textLiteral,
            color = colorScheme.literalColorScheme.textLiteralColorScheme.regularLiteralColor
        )

        public class StringFieldTemplateStartLiteral(colorScheme: KotlinColorScheme) : Literal(
            kotlinToken = StringFieldTemplateStartLiteral,
            color = colorScheme.literalColorScheme.textLiteralColorScheme.fieldTemplateColor
        )

        public class StringExpressionTemplateStartLiteral(colorScheme: KotlinColorScheme) : Literal(
            kotlinToken = StringExpressionTemplateStartLiteral,
            color = colorScheme.literalColorScheme.textLiteralColorScheme.expressionTemplateStartColor
        ), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.OpensScope
            override fun matches(token: Token): Boolean =
                token is StringExpressionTemplateEndLiteral
        }

        public class StringExpressionTemplateEndLiteral(colorScheme: KotlinColorScheme) : Literal(
            kotlinToken = StringExpressionTemplateEndLiteral,
            color = colorScheme.literalColorScheme.textLiteralColorScheme.expressionTemplateEndColor
        ), SingleStyleTokenChangingScope {
            override val scopeChange: ScopeChange get() = ScopeChange.ClosesScope
            override fun matches(token: Token): Boolean =
                token is StringExpressionTemplateStartLiteral
        }
    }

    public class Verbatim(colorScheme: KotlinColorScheme, kotlinToken: KotlinToken.Verbatim) :
        KotlinComposeToken(kotlinToken = kotlinToken, color = colorScheme.verbatimColor)
}
