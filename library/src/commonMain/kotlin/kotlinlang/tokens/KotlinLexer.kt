package kotlinlang.tokens

import kotlinlang.tokens.KotlinToken.Verbatim
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public sealed interface KotlinToken {

    public val string: String

    public sealed interface HiddenOrNewLine : KotlinToken
    public data class NewLine(override val string: String) : HiddenOrNewLine
    public data class ShebangLine(override val string: String) : KotlinToken

    public sealed interface Hidden : HiddenOrNewLine

    public sealed interface Comment : Hidden
    public data class DelimitedComment(override val string: String) : Comment
    public data class LineComment(override val string: String) : Comment
    public data class WhiteSpace(override val string: String) : Hidden

    public sealed interface IdentifierOrSoftKeyword : KotlinToken
    public data class Identifier(override val string: String) : IdentifierOrSoftKeyword {
        val unquoted: String = string.removePrefix("`").removeSuffix("`")
    }

    public enum class Operator(override val string: String) : KotlinToken {
        Reserved("..."),
        Dot("."),
        Comma(","),
        LParen("("),
        RParen(")"),
        LSquare("["),
        RSquare("]"),
        LCurl("{"),
        RCurl("}"),
        Mult("*"),
        Mod("%"),
        Div("/"),
        Add("+"),
        Sub("-"),
        Incr("++"),
        Decr("--"),
        Conj("&&"),
        Disj("||"),
        Excl("!"),
        Colon(":"),
        Semicolon(";"),
        Assignment("="),
        AddAssignment("+="),
        SubAssignment("-="),
        MultAssignment("*="),
        DivAssignment("/="),
        ModAssignment("%="),
        Arrow("->"),
        DoubleArrow("=>"),
        Range(".."),
        RangeExclusive("..<"),
        ColonColon("::"),
        DoubleSemicolon(";;"),
        Hash("#"),
        At("@"),
        Quest("?"),
        LAngle("<"),
        RAngle(">"),
        Le("<="),
        Ge(">="),
        ExclEq("!="),
        ExclEqEq("!=="),
        EqEq("=="),
        EqEqEq("==="),
        SingleQuote("'")
    }

    public sealed interface Keyword : KotlinToken

    public enum class SoftKeyword(override val string: String) : Keyword, IdentifierOrSoftKeyword {
        File("file"), Field("field"), Property("property"),
        Get("get"), Set("set"), Receiver("receiver"), Param("param"),
        SetParam("setparam"), Delegate("delegate"), Import("import"),
        Constructor("constructor"), By("by"), Companion("companion"),
        Init("init"), Catch("catch"), Finally("finally"),
        Out("out"), Dynamic("dynamic"), Public("public"),
        Private("private"), Protected("protected"), Internal("internal"),
        Enum("enum"), Sealed("sealed"), Annotation("annotation"),
        Data("data"), Inner("inner"), Tailrec("tailrec"),
        Operator("operator"), Inline("inline"), Infix("infix"),
        External("external"), Suspend("suspend"), Override("override"),
        Abstract("abstract"), Final("final"), Open("open"),
        Const("const"), Lateinit("lateinit"), Vararg("vararg"),
        Noinline("noinline"), Crossinline("crossinline"),
        Reified("reified"), Expect("expect"), Actual("actual"),
        Where("where"),
    }

    public enum class HardKeyword(override val string: String) : Keyword {
        Package("package"), Class("class"), Interface("interface"),
        Fun("fun"), Object("object"), Val("val"),
        Var("var"), TypeAlias("typealias"), This("this"),
        Super("super"), Typeof("typeof"), If("if"),
        Else("else"), When("when"), Try("try"),
        For("for"), Do("do"), While("while"),
        Throw("throw"), Return("return"), Continue("continue"),
        Break("break"), AsSafe("as?"), As("as"), Is("is"),
        In("in"), NotIs("!is"), NotIn("!in"),
    }

    public sealed interface Literal : KotlinToken {
        public sealed interface NumberLiteral : Literal
        public data class IntegralNumberLiteral(override val string: String) : NumberLiteral
        public data class RealNumberLiteral(override val string: String) : NumberLiteral
        public data class BooleanLiteral(override val string: String) : Literal
        public data object NullLiteral : Literal {
            override val string: String get() = "null"
        }

        public sealed interface Text : Literal {
            public sealed interface CharLiteralPart : Text
            public sealed interface SingleLineStringLiteralPart : Text
            public sealed interface MultiLineStringLiteralPart : Text

            public data object CharLiteralStart : CharLiteralPart {
                override val string: String get() = "'"
            }

            public data object CharLiteralEnd : CharLiteralPart {
                override val string: String get() = "'"
            }

            public data object SingleLineStringLiteralStart : SingleLineStringLiteralPart {
                override val string: String get() = "\""
            }

            public data object SingleLineStringLiteralEnd : SingleLineStringLiteralPart {
                override val string: String get() = "\""
            }


            public data object MultiLineStringLiteralStart : MultiLineStringLiteralPart {
                override val string: String get() = "\"\"\""
            }

            public data object MultiLineStringLiteralEnd : MultiLineStringLiteralPart {
                override val string: String get() = "\"\"\""
            }

            public data class EscapedStringLiteral(override val string: String) :
                CharLiteralPart, SingleLineStringLiteralPart, MultiLineStringLiteralPart

            public data class RegularLiteral(override val string: String) :
                CharLiteralPart, SingleLineStringLiteralPart, MultiLineStringLiteralPart

            public data object StringFieldTemplateStartLiteral : SingleLineStringLiteralPart,
                MultiLineStringLiteralPart {
                override val string: String get() = "$"
            }

            public data object StringExpressionTemplateStartLiteral : SingleLineStringLiteralPart,
                MultiLineStringLiteralPart {
                override val string: String get() = "\${"
            }

            public data object StringExpressionTemplateEndLiteral : SingleLineStringLiteralPart,
                MultiLineStringLiteralPart {
                override val string: String get() = "}"
            }
        }
    }

    public data class Verbatim(val char: Char) : KotlinToken {
        override val string: String = char.toString()
    }
}

internal class KotlinLexer(input: String) : Lexer<KotlinToken>(input) {
    internal var curlyBracesLevel = 0

    @OptIn(ExperimentalContracts::class)
    fun <T : Any> branch(action: () -> T?): T? {
        contract {
            callsInPlace(action, InvocationKind.EXACTLY_ONCE)
        }
        val oldPosition = position
        val oldLevel = curlyBracesLevel
        action()?.let { return it }
        position = oldPosition
        curlyBracesLevel = oldLevel
        return null
    }

    override fun readAllTokens(): List<KotlinToken> = buildList {
        while (true) {
            val portion = readTokensBatch() ?: readIf { true }?.let { listOf(Verbatim(it)) } ?: break
            addAll(portion)
        }
    }
}

public fun tokenizeKotlin(input: String): List<KotlinToken> = KotlinLexer(input).readAllTokens()

internal fun KotlinLexer.readTokensBatch(): List<KotlinToken>? = null
    ?: readShebang()?.let(::listOf)
    ?: readHiddenOrNewLine()?.let(::listOf)
    ?: readBooleanLiteral()?.let(::listOf)
    ?: readNullLiteral()?.let(::listOf)
    ?: readNumberLiteral()?.let(::listOf)
    ?: readCharacterLiteral()
    ?: readMultiLineLiteral()
    ?: readSingleLineLiteral()
    ?: readHardKeywordWithRelatedTokens() // !in is NOT_IN, not [NOT, in], AT, etc.
    ?: readOperatorWithRelatedTokens()?.let(::listOf)
    ?: readSoftKeyword()?.let(::listOf)
    ?: readIdentifier()?.let(::listOf)

