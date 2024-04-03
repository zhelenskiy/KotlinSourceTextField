package kotlinlang.tokens

import kotlin.text.CharCategory.DECIMAL_DIGIT_NUMBER
import kotlin.text.CharCategory.LOWERCASE_LETTER
import kotlin.text.CharCategory.MODIFIER_LETTER
import kotlin.text.CharCategory.OTHER_LETTER
import kotlin.text.CharCategory.TITLECASE_LETTER
import kotlin.text.CharCategory.UPPERCASE_LETTER

private fun <T> Lexer<T>.readLetter() = readIf {
    when (it.category) {
        UPPERCASE_LETTER, LOWERCASE_LETTER, TITLECASE_LETTER, MODIFIER_LETTER, OTHER_LETTER -> true
        else -> false
    }
}

private fun <T> Lexer<T>.readQuotedSymbol() = readIf { it != '`' && it != '\r' && it != '\n' }

private fun <T> Lexer<T>.readUnicodeDigit() = readIf { it.category == DECIMAL_DIGIT_NUMBER }

private fun <T> Lexer<T>.readQuotedIdentifier(): String? = buildString {
    readString("`")?.let(::append) ?: return null
    while (true) {
        append(readQuotedSymbol() ?: break)
    }
    readString("`")?.let(::append)
}

private fun <T> Lexer<T>.readRegularIdentifier(): String? = buildString {
    append(readLetterOrUnderscore() ?: return null)
    while (true) {
        append(readNonFirstRegularIdentifierSymbol() ?: break)
    }
}

internal fun <T> Lexer<T>.readNonFirstRegularIdentifierSymbol() = readLetterOrUnderscore() ?: readUnicodeDigit()

private fun <T> Lexer<T>.readLetterOrUnderscore() = readLetter() ?: readIf { it == '_' }

internal fun <T> Lexer<T>.readIdentifier(): KotlinToken.Identifier? =
    (readQuotedIdentifier() ?: readRegularIdentifier())?.let(KotlinToken::Identifier)
