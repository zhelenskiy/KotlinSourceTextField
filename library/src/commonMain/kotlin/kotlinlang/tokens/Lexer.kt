package kotlinlang.tokens

internal abstract class Lexer<Token>(private val input: String) {
    protected var position = 0
    val currentChar get() = input.getOrNull(position)
    
    internal fun readString(string: String): String? {
        if (position + string.length > input.length) return null
        for (i in string.indices) {
            if (input[position + i] != string[i]) return null
        }
        position += string.length
        return string
    }
    
    internal fun readIf(condition: (Char) -> Boolean): Char? =
        if (position + 1 > input.length || !condition(input[position])) null else input[position++]
    
    internal fun readWhile(condition: (Char) -> Boolean): String = buildString {
        while (true) {
            readIf(condition)?.let { append(it) } ?: break
        }
    }
    
    abstract fun readAllTokens(): List<Token>

    val isEOF get() = currentChar == null
}
