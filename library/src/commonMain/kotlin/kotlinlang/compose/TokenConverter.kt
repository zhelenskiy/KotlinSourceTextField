package kotlinlang.compose

import kotlinlang.tokens.KotlinToken
import kotlinlang.tokens.KotlinToken.Literal
import kotlinlang.compose.KotlinComposeToken.Literal as ComposeLiteral

internal fun KotlinToken.toCompose(colorScheme: KotlinColorScheme, isAtNearby: Boolean = false): KotlinComposeToken = when (this) {
    is KotlinToken.NewLine -> KotlinComposeToken.NewLine(this)
    is KotlinToken.ShebangLine -> KotlinComposeToken.ShebangLine(colorScheme, this)
    is KotlinToken.DelimitedComment -> KotlinComposeToken.DelimitedComment(colorScheme, this)
    is KotlinToken.LineComment -> KotlinComposeToken.LineComment(colorScheme, this)
    is KotlinToken.WhiteSpace -> KotlinComposeToken.WhiteSpace(this)
    is KotlinToken.Identifier -> KotlinComposeToken.Identifier(colorScheme, this, isAtNearby = isAtNearby)
    is KotlinToken.Operator -> KotlinComposeToken.Operator(colorScheme, this)
    is KotlinToken.SoftKeyword -> KotlinComposeToken.SoftKeyword(colorScheme, this)
    is KotlinToken.HardKeyword -> KotlinComposeToken.HardKeyword(colorScheme, this)
    is Literal.IntegralNumberLiteral -> ComposeLiteral.IntegralNumberLiteral(colorScheme, this)
    is Literal.RealNumberLiteral -> ComposeLiteral.RealNumberLiteral(colorScheme, this)
    is Literal.BooleanLiteral -> ComposeLiteral.BooleanLiteral(colorScheme, this)
    is Literal.NullLiteral -> ComposeLiteral.NullLiteral(colorScheme)
    Literal.Text.CharLiteralStart -> ComposeLiteral.CharLiteralStart(colorScheme)
    Literal.Text.CharLiteralEnd -> ComposeLiteral.CharLiteralEnd(colorScheme)
    Literal.Text.SingleLineStringLiteralStart -> ComposeLiteral.SingleLineStringLiteralStart(colorScheme)
    Literal.Text.SingleLineStringLiteralEnd -> ComposeLiteral.SingleLineStringLiteralEnd(colorScheme)
    Literal.Text.MultiLineStringLiteralStart -> ComposeLiteral.MultiLineStringLiteralStart(colorScheme)
    Literal.Text.MultiLineStringLiteralEnd -> ComposeLiteral.MultiLineStringLiteralEnd(colorScheme)
    is Literal.Text.EscapedStringLiteral -> ComposeLiteral.EscapedStringLiteral(colorScheme, this)
    is Literal.Text.RegularLiteral -> ComposeLiteral.RegularLiteral(colorScheme, this)
    Literal.Text.StringFieldTemplateStartLiteral -> ComposeLiteral.StringFieldTemplateStartLiteral(colorScheme)
    Literal.Text.StringExpressionTemplateStartLiteral ->
        ComposeLiteral.StringExpressionTemplateStartLiteral(colorScheme)

    Literal.Text.StringExpressionTemplateEndLiteral ->
        ComposeLiteral.StringExpressionTemplateEndLiteral(colorScheme)

    is KotlinToken.Verbatim -> KotlinComposeToken.Verbatim(colorScheme, this)
}

internal fun List<KotlinToken>.toCompose(colorScheme: KotlinColorScheme): List<KotlinComposeToken> =
    mapIndexed { index, token ->
        val atNearBy = when {
            index > 0 && get(index - 1) == KotlinToken.Operator.At -> true
            index < lastIndex && get(index + 1) == KotlinToken.Operator.At -> true
            else -> false
        }
        token.toCompose(colorScheme, atNearBy)
    }
