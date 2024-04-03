package kotlinlang.tokens


internal fun Lexer<KotlinToken>.readHidden() = readDelimitedComment() ?: readLineComment() ?: readWhiteSpace()

internal fun Lexer<KotlinToken>.readHiddenOrNewLine() = readHidden() ?: readNewLine()

internal fun Lexer<KotlinToken>.readWhiteSpace(): KotlinToken.WhiteSpace? =
    readIf { it == ' ' || it == '\t' || it == '' }?.toString()?.let(KotlinToken::WhiteSpace)

internal fun Lexer<KotlinToken>.readLineComment(): KotlinToken.LineComment? {
    val start = readString("//") ?: return null
    val rest = readWhile { it != '\n' && it != '\r' }
    return KotlinToken.LineComment(start + rest)
}

internal fun Lexer<KotlinToken>.readDelimitedComment(): KotlinToken.DelimitedComment? {
    val comment = buildString {
        readString("/*")?.let(this::append) ?: return null
        while (true) {
            readDelimitedComment()?.string?.let(this::append)
            readString("*/")?.let {
                append(it)
                return@buildString
            }
            readIf { true }?.let(::append) ?: break
        }
    }
    return KotlinToken.DelimitedComment(comment)
}

internal fun Lexer<KotlinToken>.readShebang(): KotlinToken.ShebangLine? {
    val shebang = readString("#!") ?: return null
    val rest = readWhile { it != '\n' && it != '\r' }
    return KotlinToken.ShebangLine(shebang + rest)
}

internal fun Lexer<KotlinToken>.readNewLine(): KotlinToken.NewLine? =
    (readString("\r\n") ?: readString("\n") ?: readString("\r"))?.let(KotlinToken::NewLine)
