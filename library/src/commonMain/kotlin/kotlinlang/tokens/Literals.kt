package kotlinlang.tokens

import kotlinlang.tokens.KotlinToken.Literal.BooleanLiteral
import kotlinlang.tokens.KotlinToken.Literal.IntegralNumberLiteral
import kotlinlang.tokens.KotlinToken.Literal.NullLiteral
import kotlinlang.tokens.KotlinToken.Literal.NumberLiteral
import kotlinlang.tokens.KotlinToken.Literal.RealNumberLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text

private fun <T> Lexer<T>.readDecDigit() = readIf { it in '0'..'9' }

private fun KotlinLexer.readDecDigits(): String? = readDigits(::readDecDigit)
private fun KotlinLexer.readDigits(readDigit: () -> Char?): String? {
    fun readDigitsWithoutSeparator(): String? {
        return buildString {
            append(readDigit() ?: return null)
            while (true) {
                append(readDigit() ?: break)
            }
        }
    }
    return buildString {
        append(readDigitsWithoutSeparator() ?: return null)
        while (true) {
            branch {
                val separator =
                    readWhile { it == '_' }.takeIf { it.isNotEmpty() } ?: return@branch null
                val next = readDigitsWithoutSeparator() ?: return@branch null
                append(separator)
                append(next)
            } ?: break
        }
    }
}

private fun KotlinLexer.readDoubleExponent(): String? = branch {
    val e = readIf { it == 'e' || it == 'E' } ?: return@branch null
    val sign = readIf { it == '+' || it == '-' }
    val digits = readDecDigits() ?: return@branch null
    "$e${sign ?: ""}$digits"
}

private fun KotlinLexer.readRealLiteral(): RealNumberLiteral? = branch {
    when (val digits = readDecDigits()) {
        null -> {
            readIf { it == '.' } ?: return@branch null
            val fractionDigits = readDecDigits() ?: return@branch null
            val exponent = readDoubleExponent()
            val float = readIf { it == 'f' || it == 'F' }
            RealNumberLiteral(".$fractionDigits${exponent ?: ""}${float ?: ""}")
        }

        else -> buildString {
            append(digits)
            val hasFractionalPart = branch inner@{
                readIf { it == '.' } ?: return@inner null
                val fractionDigits = readDecDigits() ?: return@inner null
                append('.')
                append(fractionDigits)
            } != null
            val hasExponentPart = readDoubleExponent()?.let(::append) != null
            val isFloat = readIf { it == 'f' || it == 'F' }?.let(::append) != null
            if (!hasFractionalPart && !hasExponentPart && !isFloat) return@branch null
        }.let(::RealNumberLiteral)
    }
}

private fun KotlinLexer.readBinaryIntegerLiteral(): String? = branch {
    val prefix = readString("0b") ?: readString("0B") ?: return@branch null
    val digit = readDigits(::readDecDigit) ?: return@branch null
    "$prefix$digit"
}

private fun <T> Lexer<T>.readHexDigit() =
    readIf { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }

private fun KotlinLexer.readHexIntegerLiteral(): String? = branch {
    val prefix = readString("0x") ?: readString("0X") ?: return@branch null
    val digit = readDigits(::readHexDigit) ?: return@branch null
    "$prefix$digit"
}

private fun KotlinLexer.readIntegralLiteral(): IntegralNumberLiteral? {
    val mainPart =
        readBinaryIntegerLiteral() ?: readHexIntegerLiteral() ?: readDecDigits() ?: return null
    return IntegralNumberLiteral(
        "$mainPart${readIf { it == 'u' || it == 'U' } ?: ""}${readIf { it == 'l' || it == 'L' } ?: ""}"
    )
}

internal fun KotlinLexer.readNumberLiteral(): NumberLiteral? = readRealLiteral() ?: readIntegralLiteral()

internal fun KotlinLexer.readBooleanLiteral(): BooleanLiteral? = branch {
    val value = readString("false") ?: readString("true") ?: return@branch null
    if (readNonFirstRegularIdentifierSymbol() != null) null else BooleanLiteral(value)
}

internal fun KotlinLexer.readNullLiteral(): NullLiteral? = branch {
    readString("null") ?: return@branch null
    if (readNonFirstRegularIdentifierSymbol() != null) null else NullLiteral
}

private fun <T> Lexer<T>.readEscapedIdentifier(): String? = buildString {
    readString("\\")?.let(::append) ?: return null
    readIf { it != '\n' && it != '\r' }?.let(::append)
}

private fun KotlinLexer.readUniCharacterLiteral(): String? = branch {
    buildString {
        readString("\\u")?.let(::append) ?: return@branch null
        repeat(4) {
            readHexDigit()?.let(::append) ?: return@branch null
        }
    }
}

private fun KotlinLexer.readEscapeSeq() = readUniCharacterLiteral() ?: readEscapedIdentifier()

internal fun KotlinLexer.readCharacterLiteral(): List<Text.CharLiteralPart>? = buildList {
    readString("'")?.let { add(Text.CharLiteralStart) } ?: return@buildList
    while (true) {
        fun Char.isRegular() = this != '\n' && this != '\r' && this != '\'' && this != '\\'
        val token = readEscapeSeq()?.let(Text::EscapedStringLiteral)
            ?: readWhile(Char::isRegular).takeIf { it.isNotEmpty() }?.let(Text::RegularLiteral)
            ?: break
        add(token)
    }
    readString("'")?.let { add(Text.CharLiteralEnd) }
}.takeIf { it.isNotEmpty() }

private val hardKeywordsSimilarToIdentifiers = KotlinToken.HardKeyword.entries
    .filter { KotlinLexer(it.string).run { readIdentifier() != null && isEOF } }
    .groupBy { it.string[0] }
    .mapValues { (_, list) -> list.sortedByDescending { it.string.length } }

private fun KotlinLexer.readIdentifierLikeHardKeyword(): KotlinToken.HardKeyword? {
    return hardKeywordsSimilarToIdentifiers[currentChar ?: return null]
        ?.firstNotNullOfOrNull { readIdentifierLikeKeyword(it.string, it) }
}

private fun KotlinLexer.readFieldAccess(): List<KotlinToken>? = branch {
    readString("$") ?: return@branch null
    val identifier = readIdentifierLikeExpression() ?: return@branch null
    listOf(Text.StringFieldTemplateStartLiteral, identifier)
}

private fun KotlinLexer.readIdentifierLikeExpression() =
    readNullLiteral() ?: readBooleanLiteral() ?: readIdentifierLikeHardKeyword() ?: readIdentifier()

private fun KotlinLexer.readExprAccess(): List<KotlinToken>? = buildList {
    readString("\${") ?: return null
    add(Text.StringExpressionTemplateStartLiteral)
    curlyBracesLevel++
    val currentCurlyBracesLevel = curlyBracesLevel
    var firstTime = true
    while (true) {
        if (curlyBracesLevel <= currentCurlyBracesLevel && readString("}") != null) {
            add(Text.StringExpressionTemplateEndLiteral)
            curlyBracesLevel--
            break
        }
        if (firstTime) {
            val singleIdentifier = branch {
                val identifier = readIdentifierLikeExpression() ?: return@branch null
                if (readString("}") != null) identifier else null
            }
            if (singleIdentifier != null) {
                add(singleIdentifier)
                add(Text.StringExpressionTemplateEndLiteral)
                curlyBracesLevel--
                break
            }
            firstTime = false
        }
        addAll(readTokensBatch() ?: break)
    }
}

private fun MutableList<KotlinToken>.mergeLastRegularLiterals() {
    val firstRegularIndex = indexOfLast { it !is Text.RegularLiteral } + 1
    if (firstRegularIndex >= lastIndex) return
    val newString = buildString {
        for (i in firstRegularIndex..this@mergeLastRegularLiterals.lastIndex) {
            append(this@mergeLastRegularLiterals[i].string)
        }
    }
    set(firstRegularIndex, Text.RegularLiteral(newString))
    while (firstRegularIndex < lastIndex) {
        removeLast()
    }
}

internal fun KotlinLexer.readSingleLineLiteral(): List<KotlinToken>? = buildList {
    readString("\"")?.let { add(Text.SingleLineStringLiteralStart) } ?: return null
    while (true) {
        val escaped = readEscapeSeq()
        if (escaped != null) {
            mergeLastRegularLiterals()
            add(Text.EscapedStringLiteral(escaped))
            continue
        }
        val exprAccess = readExprAccess()
        if (exprAccess != null) {
            mergeLastRegularLiterals()
            addAll(exprAccess)
            continue
        }
        val fieldAccess = readFieldAccess()
        if (fieldAccess != null) {
            mergeLastRegularLiterals()
            addAll(fieldAccess)
            continue
        }
        val regularLiteral = readIf { it != '\n' && it != '\r' && it != '"' && it != '\\' }
        // not readWhile because of jfhhf${ which needs to be parsed as
        // RegularLiteral + ExpressionTemplateStartStringLiteral, not just RegularLiteral
        if (regularLiteral != null) {
            // no mergeLastRegularLiterals() because we accumulate the literals here
            add(Text.RegularLiteral(regularLiteral.toString()))
            continue
        }
        mergeLastRegularLiterals()
        break
    }
    readString("\"")?.let { add(Text.SingleLineStringLiteralEnd) }
}


internal fun KotlinLexer.readMultiLineLiteral(): List<KotlinToken>? = buildList {
    readString("\"\"\"")?.let { add(Text.MultiLineStringLiteralStart) } ?: return null
    while (true) {
        val exprAccess = readExprAccess()
        if (exprAccess != null) {
            mergeLastRegularLiterals()
            addAll(exprAccess)
            continue
        }
        val fieldAccess = readFieldAccess()
        if (fieldAccess != null) {
            mergeLastRegularLiterals()
            addAll(fieldAccess)
            continue
        }
        val end = readString("\"\"\"")
        if (end != null) {
            mergeLastRegularLiterals()
            add(Text.MultiLineStringLiteralEnd)
            break
        }
        val char = readIf { true }
        if (char != null) {
            add(Text.RegularLiteral(char.toString()))
            continue
        }
        mergeLastRegularLiterals()
        break
    }
}
