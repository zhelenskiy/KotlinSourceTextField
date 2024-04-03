package kotlinlang.tokens

import kotlinlang.tokens.KotlinToken.HardKeyword.AsSafe
import kotlinlang.tokens.KotlinToken.HardKeyword.Break
import kotlinlang.tokens.KotlinToken.HardKeyword.Continue
import kotlinlang.tokens.KotlinToken.HardKeyword.NotIn
import kotlinlang.tokens.KotlinToken.HardKeyword.NotIs
import kotlinlang.tokens.KotlinToken.HardKeyword.Return
import kotlinlang.tokens.KotlinToken.HardKeyword.Super
import kotlinlang.tokens.KotlinToken.HardKeyword.This
import kotlinlang.tokens.KotlinToken.Operator.At
import kotlinlang.tokens.KotlinToken.Operator.LCurl
import kotlinlang.tokens.KotlinToken.Operator.RCurl

private val operatorsByLetter = KotlinToken.Operator.entries
    .filterNot { it == LCurl || it == RCurl }
    .groupBy { it.string.first() }
    .mapValues { (_, entries) -> entries.sortedByDescending { it.string.length } }

internal fun KotlinLexer.readOperatorWithRelatedTokens(): KotlinToken? {
   readString("{")?.let {
        curlyBracesLevel++
        return LCurl
    }
    readString("}")?.let {
        curlyBracesLevel--
        return RCurl
    }
    return operatorsByLetter[currentChar ?: return null]?.firstOrNull { readString(it.string) != null }
}

private val hardKeywordsByLetter = KotlinToken.HardKeyword.entries
    .filterNot { it == NotIn || it == NotIs || it == AsSafe }
    .groupBy { it.string.first() }
    .mapValues { (_, entries) -> entries.sortedByDescending { it.string.length } }

internal fun <T: KotlinToken> KotlinLexer.readIdentifierLikeKeyword(keyword: String, token: T): T? = branch {
    readString(keyword) ?: return@branch null
    if (readNonFirstRegularIdentifierSymbol() != null) null else token
}

internal fun KotlinLexer.readHardKeywordWithRelatedTokens(): List<KotlinToken>? {
    readString("as?")?.let { return listOf(AsSafe) }
    branch {
        readString("return@") ?: return@branch null
        val identifier = readIdentifier()
        if (identifier == null) null else listOf(Return, At, identifier)
    }?.let { return it }
    branch {
        readString("continue@") ?: return@branch null
        val identifier = readIdentifier()
        if (identifier == null) null else listOf(Continue,At,identifier)
    }?.let { return it }
    branch {
        readString("break@") ?: return@branch null
        val identifier = readIdentifier()
        if (identifier == null) null else listOf(Break, At, identifier)
    }?.let { return it }
    branch {
        readString("this@") ?: return@branch null
        val identifier = readIdentifier()
        if (identifier == null) null else listOf(This, At, identifier)
    }?.let { return it }
    branch {
        readString("super@") ?: return@branch null
        val identifier = readIdentifier()
        if (identifier == null) null else listOf(Super, At, identifier)
    }?.let { return it }
    branch {
        readString("!is") ?: return@branch null
        readNonFirstRegularIdentifierSymbol()?.let { return@branch null }
        listOf(NotIs)
    }?.let { return it }
    branch {
        readString("!in") ?: return@branch null
        readNonFirstRegularIdentifierSymbol()?.let { return@branch null }
        listOf(NotIn)
    }?.let { return it }
    return hardKeywordsByLetter[currentChar ?: return null]?.firstNotNullOfOrNull { readIdentifierLikeKeyword(it.string, it) }
        ?.let(::listOf)
}

private val softKeywordsByLetter = KotlinToken.SoftKeyword.entries
    .groupBy { it.string.first() }
    .mapValues { (_, entries) -> entries.sortedByDescending { it.string.length } }


internal fun KotlinLexer.readSoftKeyword(): KotlinToken.SoftKeyword? {
    return softKeywordsByLetter[currentChar ?: return null]?.firstNotNullOfOrNull { readIdentifierLikeKeyword(it.string, it) }
}
