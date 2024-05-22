import kotlinlang.tokens.KotlinToken
import kotlinlang.tokens.KotlinToken.DelimitedComment
import kotlinlang.tokens.KotlinToken.HardKeyword.As
import kotlinlang.tokens.KotlinToken.HardKeyword.AsSafe
import kotlinlang.tokens.KotlinToken.HardKeyword.Break
import kotlinlang.tokens.KotlinToken.HardKeyword.Class
import kotlinlang.tokens.KotlinToken.HardKeyword.Continue
import kotlinlang.tokens.KotlinToken.HardKeyword.Do
import kotlinlang.tokens.KotlinToken.HardKeyword.Else
import kotlinlang.tokens.KotlinToken.HardKeyword.For
import kotlinlang.tokens.KotlinToken.HardKeyword.Fun
import kotlinlang.tokens.KotlinToken.HardKeyword.If
import kotlinlang.tokens.KotlinToken.HardKeyword.In
import kotlinlang.tokens.KotlinToken.HardKeyword.Interface
import kotlinlang.tokens.KotlinToken.HardKeyword.Is
import kotlinlang.tokens.KotlinToken.HardKeyword.NotIn
import kotlinlang.tokens.KotlinToken.HardKeyword.NotIs
import kotlinlang.tokens.KotlinToken.HardKeyword.Object
import kotlinlang.tokens.KotlinToken.HardKeyword.Package
import kotlinlang.tokens.KotlinToken.HardKeyword.Return
import kotlinlang.tokens.KotlinToken.HardKeyword.Super
import kotlinlang.tokens.KotlinToken.HardKeyword.This
import kotlinlang.tokens.KotlinToken.HardKeyword.Throw
import kotlinlang.tokens.KotlinToken.HardKeyword.Try
import kotlinlang.tokens.KotlinToken.HardKeyword.TypeAlias
import kotlinlang.tokens.KotlinToken.HardKeyword.Typeof
import kotlinlang.tokens.KotlinToken.HardKeyword.Val
import kotlinlang.tokens.KotlinToken.HardKeyword.Var
import kotlinlang.tokens.KotlinToken.HardKeyword.When
import kotlinlang.tokens.KotlinToken.HardKeyword.While
import kotlinlang.tokens.KotlinToken.Identifier
import kotlinlang.tokens.KotlinToken.LineComment
import kotlinlang.tokens.KotlinToken.Literal.BooleanLiteral
import kotlinlang.tokens.KotlinToken.Literal.IntegralNumberLiteral
import kotlinlang.tokens.KotlinToken.Literal.NullLiteral
import kotlinlang.tokens.KotlinToken.Literal.RealNumberLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.CharLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.CharLiteralStart
import kotlinlang.tokens.KotlinToken.Literal.Text.EscapedStringLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.StringExpressionTemplateEndLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.StringExpressionTemplateStartLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.StringFieldTemplateStartLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.MultiLineStringLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.MultiLineStringLiteralStart
import kotlinlang.tokens.KotlinToken.Literal.Text.RegularLiteral
import kotlinlang.tokens.KotlinToken.Literal.Text.SingleLineStringLiteralEnd
import kotlinlang.tokens.KotlinToken.Literal.Text.SingleLineStringLiteralStart
import kotlinlang.tokens.KotlinToken.NewLine
import kotlinlang.tokens.KotlinToken.Operator.Add
import kotlinlang.tokens.KotlinToken.Operator.AddAssignment
import kotlinlang.tokens.KotlinToken.Operator.Arrow
import kotlinlang.tokens.KotlinToken.Operator.Assignment
import kotlinlang.tokens.KotlinToken.Operator.At
import kotlinlang.tokens.KotlinToken.Operator.Colon
import kotlinlang.tokens.KotlinToken.Operator.ColonColon
import kotlinlang.tokens.KotlinToken.Operator.Comma
import kotlinlang.tokens.KotlinToken.Operator.Conj
import kotlinlang.tokens.KotlinToken.Operator.Decr
import kotlinlang.tokens.KotlinToken.Operator.Disj
import kotlinlang.tokens.KotlinToken.Operator.Div
import kotlinlang.tokens.KotlinToken.Operator.DivAssignment
import kotlinlang.tokens.KotlinToken.Operator.Dot
import kotlinlang.tokens.KotlinToken.Operator.DoubleArrow
import kotlinlang.tokens.KotlinToken.Operator.DoubleSemicolon
import kotlinlang.tokens.KotlinToken.Operator.EqEq
import kotlinlang.tokens.KotlinToken.Operator.EqEqEq
import kotlinlang.tokens.KotlinToken.Operator.Excl
import kotlinlang.tokens.KotlinToken.Operator.ExclEq
import kotlinlang.tokens.KotlinToken.Operator.ExclEqEq
import kotlinlang.tokens.KotlinToken.Operator.Ge
import kotlinlang.tokens.KotlinToken.Operator.Hash
import kotlinlang.tokens.KotlinToken.Operator.Incr
import kotlinlang.tokens.KotlinToken.Operator.LAngle
import kotlinlang.tokens.KotlinToken.Operator.LCurl
import kotlinlang.tokens.KotlinToken.Operator.LParen
import kotlinlang.tokens.KotlinToken.Operator.LSquare
import kotlinlang.tokens.KotlinToken.Operator.Le
import kotlinlang.tokens.KotlinToken.Operator.Mod
import kotlinlang.tokens.KotlinToken.Operator.ModAssignment
import kotlinlang.tokens.KotlinToken.Operator.Mult
import kotlinlang.tokens.KotlinToken.Operator.MultAssignment
import kotlinlang.tokens.KotlinToken.Operator.Quest
import kotlinlang.tokens.KotlinToken.Operator.RAngle
import kotlinlang.tokens.KotlinToken.Operator.RCurl
import kotlinlang.tokens.KotlinToken.Operator.RParen
import kotlinlang.tokens.KotlinToken.Operator.RSquare
import kotlinlang.tokens.KotlinToken.Operator.Range
import kotlinlang.tokens.KotlinToken.Operator.RangeExclusive
import kotlinlang.tokens.KotlinToken.Operator.Reserved
import kotlinlang.tokens.KotlinToken.Operator.Semicolon
import kotlinlang.tokens.KotlinToken.Operator.Sub
import kotlinlang.tokens.KotlinToken.Operator.SubAssignment
import kotlinlang.tokens.KotlinToken.ShebangLine
import kotlinlang.tokens.KotlinToken.SoftKeyword.Abstract
import kotlinlang.tokens.KotlinToken.SoftKeyword.Actual
import kotlinlang.tokens.KotlinToken.SoftKeyword.Annotation
import kotlinlang.tokens.KotlinToken.SoftKeyword.By
import kotlinlang.tokens.KotlinToken.SoftKeyword.Catch
import kotlinlang.tokens.KotlinToken.SoftKeyword.Companion
import kotlinlang.tokens.KotlinToken.SoftKeyword.Const
import kotlinlang.tokens.KotlinToken.SoftKeyword.Constructor
import kotlinlang.tokens.KotlinToken.SoftKeyword.Crossinline
import kotlinlang.tokens.KotlinToken.SoftKeyword.Data
import kotlinlang.tokens.KotlinToken.SoftKeyword.Delegate
import kotlinlang.tokens.KotlinToken.SoftKeyword.Dynamic
import kotlinlang.tokens.KotlinToken.SoftKeyword.Enum
import kotlinlang.tokens.KotlinToken.SoftKeyword.Expect
import kotlinlang.tokens.KotlinToken.SoftKeyword.External
import kotlinlang.tokens.KotlinToken.SoftKeyword.Field
import kotlinlang.tokens.KotlinToken.SoftKeyword.File
import kotlinlang.tokens.KotlinToken.SoftKeyword.Final
import kotlinlang.tokens.KotlinToken.SoftKeyword.Finally
import kotlinlang.tokens.KotlinToken.SoftKeyword.Get
import kotlinlang.tokens.KotlinToken.SoftKeyword.Import
import kotlinlang.tokens.KotlinToken.SoftKeyword.Infix
import kotlinlang.tokens.KotlinToken.SoftKeyword.Init
import kotlinlang.tokens.KotlinToken.SoftKeyword.Inline
import kotlinlang.tokens.KotlinToken.SoftKeyword.Inner
import kotlinlang.tokens.KotlinToken.SoftKeyword.Internal
import kotlinlang.tokens.KotlinToken.SoftKeyword.Lateinit
import kotlinlang.tokens.KotlinToken.SoftKeyword.Noinline
import kotlinlang.tokens.KotlinToken.SoftKeyword.Open
import kotlinlang.tokens.KotlinToken.SoftKeyword.Operator
import kotlinlang.tokens.KotlinToken.SoftKeyword.Out
import kotlinlang.tokens.KotlinToken.SoftKeyword.Override
import kotlinlang.tokens.KotlinToken.SoftKeyword.Param
import kotlinlang.tokens.KotlinToken.SoftKeyword.Private
import kotlinlang.tokens.KotlinToken.SoftKeyword.Property
import kotlinlang.tokens.KotlinToken.SoftKeyword.Protected
import kotlinlang.tokens.KotlinToken.SoftKeyword.Public
import kotlinlang.tokens.KotlinToken.SoftKeyword.Receiver
import kotlinlang.tokens.KotlinToken.SoftKeyword.Reified
import kotlinlang.tokens.KotlinToken.SoftKeyword.Sealed
import kotlinlang.tokens.KotlinToken.SoftKeyword.Set
import kotlinlang.tokens.KotlinToken.SoftKeyword.SetParam
import kotlinlang.tokens.KotlinToken.SoftKeyword.Suspend
import kotlinlang.tokens.KotlinToken.SoftKeyword.Tailrec
import kotlinlang.tokens.KotlinToken.SoftKeyword.Vararg
import kotlinlang.tokens.KotlinToken.SoftKeyword.Where
import kotlinlang.tokens.KotlinToken.Verbatim
import kotlinlang.tokens.KotlinToken.WhiteSpace
import kotlinlang.tokens.tokenizeKotlin
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class LexerTests {

    private fun verifyOutput(input: String, expectedTokens: List<KotlinToken>): List<KotlinToken> {
        val actualTokens = tokenizeKotlin(input)
        assertContentEquals(expectedTokens, actualTokens, actualTokens.joinToString("\n"))
        assertEquals(input, actualTokens.joinToString("") { it.string })
        return actualTokens
    }


    @Test
    fun `Whitespaces and comments`() {
        val input = """
            #! some/path
            fun f() // fun f
            some c /* comment start
            comment end*/ not comment /*
            outer start /* inner */ outer end */ not comment again
        """.trimIndent()

        val expectedTokens: List<KotlinToken> = listOf(
            ShebangLine("#! some/path"), NewLine("\n"),

            Fun, WhiteSpace(" "), Identifier("f"), LParen, RParen,
            WhiteSpace(" "), LineComment("// fun f"), NewLine("\n"),

            Identifier("some"), WhiteSpace(" "), Identifier("c"),
            WhiteSpace(" "), DelimitedComment("/* comment start\ncomment end*/"),
            WhiteSpace(" "), Identifier("not"), WhiteSpace(" "),
            Identifier("comment"), WhiteSpace(" "),
            DelimitedComment("/*\nouter start /* inner */ outer end */"),
            WhiteSpace(" "), Identifier("not"), WhiteSpace(" "),
            Identifier("comment"), WhiteSpace(" "), Identifier("again"),
        )

        verifyOutput(input, expectedTokens)
        verifyOutput("/**/", listOf(DelimitedComment("/**/")))
        verifyOutput("/**", listOf(DelimitedComment("/**")))
        verifyOutput("/*", listOf(DelimitedComment("/*")))
        verifyOutput("/", listOf(Div))
        verifyOutput("", listOf())
    }

    @Test
    fun `Identifiers`() {
        val tokens1 = verifyOutput(
            input = "fun foo()",
            expectedTokens = listOf(Fun, WhiteSpace(" "), Identifier("foo"), LParen, RParen)
        )
        val tokens2 = verifyOutput(
            input = "fun `foo`()",
            expectedTokens = listOf(Fun, WhiteSpace(" "), Identifier("`foo`"), LParen, RParen)
        )

        assertEquals(
            tokens1.filterIsInstance<Identifier>().first().unquoted,
            tokens2.filterIsInstance<Identifier>().first().unquoted
        )

        verifyOutput(
            input = "fun `some interesting name`()",
            expectedTokens = listOf(
                Fun,
                WhiteSpace(" "),
                Identifier("`some interesting name`"),
                LParen,
                RParen
            )
        )

        verifyOutput(
            input = "fun `some interesting name",
            expectedTokens = listOf(Fun, WhiteSpace(" "), Identifier("`some interesting name"))
        )

        verifyOutput(
            input = "fun `",
            expectedTokens = listOf(Fun, WhiteSpace(" "), Identifier("`"))
        )
    }

    @Test
    fun `Operators`() {
        verifyOutput("a...b", listOf(Identifier("a"), Reserved, Identifier("b")))
        verifyOutput("a..b", listOf(Identifier("a"), Range, Identifier("b")))
        verifyOutput("a..<b", listOf(Identifier("a"), RangeExclusive, Identifier("b")))
        verifyOutput("a.b", listOf(Identifier("a"), Dot, Identifier("b")))
        verifyOutput("a......b", listOf(Identifier("a"), Reserved, Reserved, Identifier("b")))
        verifyOutput("a.....b", listOf(Identifier("a"), Reserved, Range, Identifier("b")))
        verifyOutput("a....b", listOf(Identifier("a"), Reserved, Dot, Identifier("b")))

        verifyOutput("a,b", listOf(Identifier("a"), Comma, Identifier("b")))

        verifyOutput("(a)", listOf(LParen, Identifier("a"), RParen))
        verifyOutput("()", listOf(LParen, RParen))
        verifyOutput("(", listOf(LParen))
        verifyOutput(")", listOf(RParen))

        verifyOutput("[a]", listOf(LSquare, Identifier("a"), RSquare))
        verifyOutput("[]", listOf(LSquare, RSquare))
        verifyOutput("[", listOf(LSquare))
        verifyOutput("]", listOf(RSquare))

        verifyOutput("{a}", listOf(LCurl, Identifier("a"), RCurl))
        verifyOutput("{}", listOf(LCurl, RCurl))
        verifyOutput("{", listOf(LCurl))
        verifyOutput("}", listOf(RCurl))

        verifyOutput("<a>", listOf(LAngle, Identifier("a"), RAngle))
        verifyOutput("<>", listOf(LAngle, RAngle))
        verifyOutput("<", listOf(LAngle))
        verifyOutput(">", listOf(RAngle))

        verifyOutput("a*b", listOf(Identifier("a"), Mult, Identifier("b")))
        verifyOutput("a%b", listOf(Identifier("a"), Mod, Identifier("b")))
        verifyOutput("a/b", listOf(Identifier("a"), Div, Identifier("b")))

        verifyOutput("a+b", listOf(Identifier("a"), Add, Identifier("b")))
        verifyOutput("a-b", listOf(Identifier("a"), Sub, Identifier("b")))
        verifyOutput("a++b", listOf(Identifier("a"), Incr, Identifier("b")))
        verifyOutput("a--b", listOf(Identifier("a"), Decr, Identifier("b")))
        verifyOutput("a+-b", listOf(Identifier("a"), Add, Sub, Identifier("b")))
        verifyOutput("a-+b", listOf(Identifier("a"), Sub, Add, Identifier("b")))
        verifyOutput("a+++b", listOf(Identifier("a"), Incr, Add, Identifier("b")))
        verifyOutput("a--+b", listOf(Identifier("a"), Decr, Add, Identifier("b")))
        verifyOutput("a+-+b", listOf(Identifier("a"), Add, Sub, Add, Identifier("b")))
        verifyOutput("a-++b", listOf(Identifier("a"), Sub, Incr, Identifier("b")))
        verifyOutput("a++-b", listOf(Identifier("a"), Incr, Sub, Identifier("b")))
        verifyOutput("a---b", listOf(Identifier("a"), Decr, Sub, Identifier("b")))
        verifyOutput("a+--b", listOf(Identifier("a"), Add, Decr, Identifier("b")))
        verifyOutput("a-+-b", listOf(Identifier("a"), Sub, Add, Sub, Identifier("b")))

        verifyOutput("a&b", listOf(Identifier("a"), Verbatim('&'), Identifier("b")))
        verifyOutput("a&&b", listOf(Identifier("a"), Conj, Identifier("b")))
        verifyOutput("a|b", listOf(Identifier("a"), Verbatim('|'), Identifier("b")))
        verifyOutput("a||b", listOf(Identifier("a"), Disj, Identifier("b")))

        verifyOutput("!", listOf(Excl))
        verifyOutput("!a", listOf(Excl, Identifier("a")))
        verifyOutput("!in", listOf(NotIn))
        verifyOutput("!is", listOf(NotIs))
        verifyOutput("!ins", listOf(Excl, Identifier("ins")))
        verifyOutput("!isn", listOf(Excl, Identifier("isn")))
        verifyOutput("!in2", listOf(Excl, Identifier("in2")))
        verifyOutput("!is2", listOf(Excl, Identifier("is2")))
        verifyOutput("!in s", listOf(NotIn, WhiteSpace(" "), Identifier("s")))
        verifyOutput("!is n", listOf(NotIs, WhiteSpace(" "), Identifier("n")))
        verifyOutput("!in/**/s", listOf(NotIn, DelimitedComment("/**/"), Identifier("s")))
        verifyOutput("!is/**/n", listOf(NotIs, DelimitedComment("/**/"), Identifier("n")))
        verifyOutput("!in`s", listOf(NotIn, Identifier("`s")))
        verifyOutput("!is`n", listOf(NotIs, Identifier("`n")))
        verifyOutput("!in`", listOf(NotIn, Identifier("`")))
        verifyOutput("!is`", listOf(NotIs, Identifier("`")))
        verifyOutput("!=", listOf(ExclEq))
        verifyOutput("!==", listOf(ExclEqEq))
        verifyOutput("!===", listOf(ExclEqEq, Assignment))
        verifyOutput("!====", listOf(ExclEqEq, EqEq))

        verifyOutput(":", listOf(Colon))
        verifyOutput("::", listOf(ColonColon))
        verifyOutput(":::", listOf(ColonColon, Colon))
        verifyOutput("::::", listOf(ColonColon, ColonColon))

        verifyOutput(";", listOf(Semicolon))
        verifyOutput(";;", listOf(DoubleSemicolon))
        verifyOutput(";;;", listOf(DoubleSemicolon, Semicolon))
        verifyOutput(";;;;", listOf(DoubleSemicolon, DoubleSemicolon))

        verifyOutput("=", listOf(Assignment))
        verifyOutput("==", listOf(EqEq))
        verifyOutput("===", listOf(EqEqEq))
        verifyOutput("====", listOf(EqEqEq, Assignment))
        verifyOutput("=====", listOf(EqEqEq, EqEq))
        verifyOutput("+=", listOf(AddAssignment))
        verifyOutput("-=", listOf(SubAssignment))
        verifyOutput("*=", listOf(MultAssignment))
        verifyOutput("/=", listOf(DivAssignment))
        verifyOutput("%=", listOf(ModAssignment))

        verifyOutput("->", listOf(Arrow))
        verifyOutput("=>", listOf(DoubleArrow))

        verifyOutput("#", listOf(Hash))
        verifyOutput("@", listOf(At))
        verifyOutput("super@", listOf(Super, At))
        verifyOutput("super@cat", listOf(Super, At, Identifier("cat")))
        verifyOutput("this@", listOf(This, At))
        verifyOutput("this@cat", listOf(This, At, Identifier("cat")))
        verifyOutput("break@", listOf(Break, At))
        verifyOutput("break@cat", listOf(Break, At, Identifier("cat")))
        verifyOutput("continue@", listOf(Continue, At))
        verifyOutput("continue@cat", listOf(Continue, At, Identifier("cat")))
        verifyOutput("return@", listOf(Return, At))
        verifyOutput("return@cat", listOf(Return, At, Identifier("cat")))

        verifyOutput("?", listOf(Quest))

        verifyOutput("<=", listOf(Le))
        verifyOutput("<==", listOf(Le, Assignment))
        verifyOutput("<===", listOf(Le, EqEq))
        verifyOutput(">=", listOf(Ge))
        verifyOutput(">==", listOf(Ge, Assignment))
        verifyOutput(">===", listOf(Ge, EqEq))

        verifyOutput("'", listOf(CharLiteralStart))
    }

    private fun checkKeyword(
        string: String,
        keyword: KotlinToken.Keyword,
        includeRegularId: Boolean = true
    ) {
        verifyOutput(string, listOf(keyword))
        verifyOutput("$string`", listOf(keyword, Identifier("`")))
        verifyOutput("$string`s", listOf(keyword, Identifier("`s")))
        verifyOutput("$string`s`", listOf(keyword, Identifier("`s`")))
        if (includeRegularId) {
            verifyOutput("${string}s", listOf(Identifier("${string}s")))
            verifyOutput("${string}2", listOf(Identifier("${string}2")))
        }
        verifyOutput("$string ", listOf(keyword, WhiteSpace(" ")))
        verifyOutput("${string}/**/", listOf(keyword, DelimitedComment("/**/")))
    }

    private fun checkSoftKeyword(
        string: String,
        keyword: KotlinToken.SoftKeyword,
        includeRegularId: Boolean = true
    ) {
        checkKeyword(string, keyword, includeRegularId)

        verifyOutput(
            input = "\"\${$string}\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                StringExpressionTemplateStartLiteral,
                Identifier(string),
                StringExpressionTemplateEndLiteral,
                SingleLineStringLiteralEnd
            )
        )
        verifyOutput(
            input = "\"\$$string\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                StringFieldTemplateStartLiteral,
                Identifier(string),
                SingleLineStringLiteralEnd
            )
        )
    }

    private fun checkHardKeyword(
        string: String, keyword: KotlinToken.HardKeyword,
        includeRegularId: Boolean = true,
        includeExpressionTemplates: Boolean = true,
    ) {
        checkKeyword(string, keyword, includeRegularId)

        verifyOutput(
            input = "\"\${$string}\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                StringExpressionTemplateStartLiteral,
                keyword,
                StringExpressionTemplateEndLiteral,
                SingleLineStringLiteralEnd
            )
        )
        if (includeExpressionTemplates) {
            verifyOutput(
                input = "\"\$$string\"",
                expectedTokens = listOf(
                    SingleLineStringLiteralStart,
                    StringFieldTemplateStartLiteral,
                    keyword,
                    SingleLineStringLiteralEnd
                )
            )
        }
    }

    @Test
    fun `Soft keywords`() {
        checkSoftKeyword("file", File)
        checkSoftKeyword("field", Field)
        checkSoftKeyword("property", Property)
        checkSoftKeyword("get", Get)
        checkSoftKeyword("set", Set)
        checkSoftKeyword("receiver", Receiver)
        checkSoftKeyword("param", Param)
        checkSoftKeyword("setparam", SetParam)
        checkSoftKeyword("delegate", Delegate)
        checkSoftKeyword("import", Import)
        checkSoftKeyword("constructor", Constructor)
        checkSoftKeyword("by", By)
        checkSoftKeyword("companion", Companion)
        checkSoftKeyword("init", Init)
        checkSoftKeyword("catch", Catch)
        checkSoftKeyword("finally", Finally)
        checkSoftKeyword("out", Out)
        checkSoftKeyword("dynamic", Dynamic)
        checkSoftKeyword("public", Public)
        checkSoftKeyword("private", Private)
        checkSoftKeyword("protected", Protected)
        checkSoftKeyword("internal", Internal)
        checkSoftKeyword("enum", Enum)
        checkSoftKeyword("sealed", Sealed)
        checkSoftKeyword("annotation", Annotation)
        checkSoftKeyword("data", Data)
        checkSoftKeyword("inner", Inner)
        checkSoftKeyword("tailrec", Tailrec)
        checkSoftKeyword("operator", Operator)
        checkSoftKeyword("inline", Inline)
        checkSoftKeyword("infix", Infix)
        checkSoftKeyword("external", External)
        checkSoftKeyword("suspend", Suspend)
        checkSoftKeyword("override", Override)
        checkSoftKeyword("abstract", Abstract)
        checkSoftKeyword("final", Final)
        checkSoftKeyword("open", Open)
        checkSoftKeyword("const", Const)
        checkSoftKeyword("lateinit", Lateinit)
        checkSoftKeyword("vararg", Vararg)
        checkSoftKeyword("noinline", Noinline)
        checkSoftKeyword("crossinline", Crossinline)
        checkSoftKeyword("reified", Reified)
        checkSoftKeyword("expect", Expect)
        checkSoftKeyword("actual", Actual)
        checkSoftKeyword("where", Where)
    }

    @Test
    fun `Hard keywords`() {
        checkHardKeyword("package", Package)
        checkHardKeyword("class", Class)
        checkHardKeyword("interface", Interface)
        checkHardKeyword("fun", Fun)
        checkHardKeyword("object", Object)
        checkHardKeyword("val", Val)
        checkHardKeyword("var", Var)
        checkHardKeyword("typealias", TypeAlias)
        checkHardKeyword("this", This)
        checkHardKeyword("super", Super)
        checkHardKeyword("typeof", Typeof)
        checkHardKeyword("if", If)
        checkHardKeyword("else", Else)
        checkHardKeyword("when", When)
        checkHardKeyword("try", Try)
        checkHardKeyword("for", For)
        checkHardKeyword("do", Do)
        checkHardKeyword("while", While)
        checkHardKeyword("throw", Throw)
        checkHardKeyword("return", Return)
        checkHardKeyword("continue", Continue)
        checkHardKeyword("break", Break)

        checkHardKeyword(
            "as?",
            AsSafe,
            includeRegularId = false,
            includeExpressionTemplates = false
        )
        verifyOutput("as?2", listOf(AsSafe, IntegralNumberLiteral("2")))
        verifyOutput("as?s", listOf(AsSafe, Identifier("s")))
        verifyOutput(
            input = "\"\$as?\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart, StringFieldTemplateStartLiteral, As,
                RegularLiteral("?"), SingleLineStringLiteralEnd
            ),
        )

        checkHardKeyword("as", As)
        checkHardKeyword("is", Is)
        checkHardKeyword("in", In)

        checkHardKeyword("!is", NotIs, includeRegularId = false, includeExpressionTemplates = false)
        verifyOutput("!is2", listOf(Excl, Identifier("is2")))
        verifyOutput("!iss", listOf(Excl, Identifier("iss")))
        verifyOutput(
            input = "\"\$!is\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                RegularLiteral("$!is"),
                SingleLineStringLiteralEnd
            ),
        )

        checkHardKeyword("!in", NotIn, includeRegularId = false, includeExpressionTemplates = false)
        verifyOutput("!in2", listOf(Excl, Identifier("in2")))
        verifyOutput("!ins", listOf(Excl, Identifier("ins")))
        verifyOutput(
            input = "\"\$!in\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                RegularLiteral("$!in"),
                SingleLineStringLiteralEnd
            ),
        )
    }

    @Test
    fun `Integral number literals`() {
        val validIntegerNumbers = listOf(
            "1234", "1234u", "1234l", "1234ul", "1234U", "1234L", "1234UL", "1234uL", "1234Ul",
            "0123", "0123u", "0123l", "0123ul", "0123U", "0123L", "0123UL", "0123uL", "0123Ul",
            "1__2_3", "01__2_3", "01__2_3u", "01__2_3ul", "01__2_3L",
            "0x01_234_567_890_abc_def_ABC_DEF", "0x01_234_567_890_abc_def_ABC_DEFUL", "0b0_1_001",
            "0X01_234_567_890_abc_def_ABC_DEF", "0X01_234_567_890_abc_def_ABC_DEFUL", "0B0_1_001",
        )
        for (number in validIntegerNumbers) {
            verifyOutput(number, listOf(IntegralNumberLiteral(number)))
        }
        verifyOutput("_1_2_3_4_", listOf(Identifier("_1_2_3_4_")))
        verifyOutput("1_2_3_4__", listOf(IntegralNumberLiteral("1_2_3_4"), Identifier("__")))
        verifyOutput("-1_2_3_4", listOf(Sub, IntegralNumberLiteral("1_2_3_4")))
        verifyOutput("0123456789ag", listOf(IntegralNumberLiteral("0123456789"), Identifier("ag")))
        verifyOutput(
            "0b0123456789ag",
            listOf(IntegralNumberLiteral("0b0123456789"), Identifier("ag"))
        )
        verifyOutput("0c3", listOf(IntegralNumberLiteral("0"), Identifier("c3")))
        verifyOutput(
            "0x0123456789ag",
            listOf(IntegralNumberLiteral("0x0123456789a"), Identifier("g"))
        )
    }

    @Test
    fun `Real Number literals`() {
        val validRealNumbers = listOf(
            ".2_3f",
            ".2_3e8_2f",
            ".2_3E+8_2f",
            ".2_3e-8_2f",
            ".2_3",
            ".2_3e8_2",
            ".2_3E+8_2",
            ".2_3e-8_2",
            "1_2.3_4f",
            "1_2.3_4e5_6F",
            "1_2.3_4e+5_6f",
            "1_2.3_4e-5_6f",
            "1_2e5_6f",
            "1_2e+5_6F",
            "1_2e-5_6f",
            "1_2.3_4",
            "1_2.3_4e5_6",
            "1_2.3_4e+5_6",
            "1_2.3_4e-5_6",
            "1_2e5_6",
            "1_2e+5_6",
            "1_2e-5_6",
        )
        for (number in validRealNumbers) {
            verifyOutput(number, listOf(RealNumberLiteral(number)))
        }
        verifyOutput("123fu", listOf(RealNumberLiteral("123f"), Identifier("u")))
        verifyOutput("123uf", listOf(IntegralNumberLiteral("123u"), Identifier("f")))
        verifyOutput("123fl", listOf(RealNumberLiteral("123f"), Identifier("l")))
        verifyOutput("123lf", listOf(IntegralNumberLiteral("123l"), Identifier("f")))
        verifyOutput(".0x2f", listOf(RealNumberLiteral(".0"), Identifier("x2f")))
        verifyOutput("2e+0x2f", listOf(RealNumberLiteral("2e+0"), Identifier("x2f")))
        verifyOutput("2.0x2f", listOf(RealNumberLiteral("2.0"), Identifier("x2f")))
        verifyOutput(".0b2f", listOf(RealNumberLiteral(".0"), Identifier("b2f")))
        verifyOutput("2e+0b2f", listOf(RealNumberLiteral("2e+0"), Identifier("b2f")))
        verifyOutput("2.0b2f", listOf(RealNumberLiteral("2.0"), Identifier("b2f")))
    }

    private fun checkEnumLiteral(string: String, literal: KotlinToken.Literal) {
        verifyOutput(string, listOf(literal))
        verifyOutput("$string`", listOf(literal, Identifier("`")))
        verifyOutput("$string`s", listOf(literal, Identifier("`s")))
        verifyOutput("$string`s`", listOf(literal, Identifier("`s`")))
        verifyOutput("${string}s", listOf(Identifier("${string}s")))
        verifyOutput("${string}2", listOf(Identifier("${string}2")))
        verifyOutput("$string ", listOf(literal, WhiteSpace(" ")))
        verifyOutput("${string}/**/", listOf(literal, DelimitedComment("/**/")))
        verifyOutput(
            input = "\"\${$string}\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                StringExpressionTemplateStartLiteral,
                literal,
                StringExpressionTemplateEndLiteral,
                SingleLineStringLiteralEnd
            )
        )
        verifyOutput(
            input = "\"\$$string\"",
            expectedTokens = listOf(
                SingleLineStringLiteralStart,
                StringFieldTemplateStartLiteral,
                literal,
                SingleLineStringLiteralEnd
            )
        )
    }

    @Test
    fun `Boolean literals`() {
        checkEnumLiteral("true", BooleanLiteral("true"))
        checkEnumLiteral("false", BooleanLiteral("false"))
    }

    @Test
    fun `Null literal`() {
        checkEnumLiteral("null", NullLiteral)
    }

    private fun singleLineTextLiteralCommonTests(
        quote: Char, startLiteral: KotlinToken.Literal.Text, endLiteral: KotlinToken.Literal.Text
    ) {
        verifyOutput(
            input = "${quote}a${quote}",
            expectedTokens = listOf(startLiteral, RegularLiteral("a"), endLiteral)
        )
        verifyOutput(
            input = "${quote}abb${quote}",
            expectedTokens = listOf(startLiteral, RegularLiteral("abb"), endLiteral)
        )
        verifyOutput(
            input = "${quote}abb",
            expectedTokens = listOf(startLiteral, RegularLiteral("abb"))
        )
        verifyOutput(
            input = "${quote}a",
            expectedTokens = listOf(startLiteral, RegularLiteral("a"))
        )
        verifyOutput(
            input = "${quote}",
            expectedTokens = listOf(startLiteral)
        )
        verifyOutput(
            input = "${quote}aa\n",
            expectedTokens = listOf(startLiteral, RegularLiteral("aa"), NewLine("\n"))
        )
        verifyOutput(
            input = "${quote}aa\n${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("aa"), NewLine("\n"),
                startLiteral,
            )
        )
        verifyOutput(
            input = "${quote}aa\nbb${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("aa"), NewLine("\n"), Identifier("bb"),
                startLiteral,
            )
        )

        verifyOutput(
            input = "${quote}\\n${quote}",
            expectedTokens = listOf(startLiteral, EscapedStringLiteral("\\n"), endLiteral)
        )
        verifyOutput(
            input = "${quote}\\\\${quote}",
            expectedTokens = listOf(
                startLiteral,
                EscapedStringLiteral("\\\\"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\\\\n${quote}",
            expectedTokens = listOf(
                startLiteral,
                EscapedStringLiteral("\\\\"), RegularLiteral("n"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\\\\\\n${quote}",
            expectedTokens = listOf(
                startLiteral,
                EscapedStringLiteral("\\\\"), EscapedStringLiteral("\\n"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\na${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\n"), RegularLiteral("a"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\aa${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\a"), RegularLiteral("a"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\ua${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\u"), RegularLiteral("a"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\u000${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\u"), RegularLiteral("000"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\u000a${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\u000a"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\u000A${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\u000A"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}a\\U000A${quote}",
            expectedTokens = listOf(
                startLiteral,
                RegularLiteral("a"), EscapedStringLiteral("\\U"), RegularLiteral("000A"),
                endLiteral
            )
        )
    }

    @Test
    fun `Char literals`() {
        singleLineTextLiteralCommonTests('\'', CharLiteralStart, CharLiteralEnd)
        verifyOutput(
            input = "'\$a'",
            expectedTokens = listOf(CharLiteralStart, RegularLiteral("\$a"), CharLiteralEnd)
        )
        verifyOutput(
            input = "'\${a}'",
            expectedTokens = listOf(CharLiteralStart, RegularLiteral("\${a}"), CharLiteralEnd)
        )
    }

    private fun interpolationTests(
        quote: String, startLiteral: KotlinToken.Literal.Text, endLiteral: KotlinToken.Literal.Text
    ) {
        verifyOutput(
            input = "${quote}\$a${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringFieldTemplateStartLiteral, Identifier("a"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\$file${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringFieldTemplateStartLiteral, Identifier("file"), // soft keyword
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\$file2 k${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringFieldTemplateStartLiteral, Identifier("file2"), RegularLiteral(" k"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\$fun k${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringFieldTemplateStartLiteral, Fun /* hard keyword */, RegularLiteral(" k"),
                endLiteral
            )
        )

        verifyOutput(
            input = "${quote}\${a}${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringExpressionTemplateStartLiteral,
                Identifier("a"),
                StringExpressionTemplateEndLiteral,
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\${file}${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringExpressionTemplateStartLiteral,
                Identifier("file") /* soft keyword */,
                StringExpressionTemplateEndLiteral,
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\${file }${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringExpressionTemplateStartLiteral,
                File /* soft keyword */, WhiteSpace(" "), // the hack does not work
                StringExpressionTemplateEndLiteral,
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\${file2} k${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringExpressionTemplateStartLiteral,
                Identifier("file2"),
                StringExpressionTemplateEndLiteral,
                RegularLiteral(" k"),
                endLiteral
            )
        )
        verifyOutput(
            input = "${quote}\${fun} k${quote}",
            expectedTokens = listOf(
                startLiteral,
                StringExpressionTemplateStartLiteral,
                Fun /* hard keyword */,
                StringExpressionTemplateEndLiteral,
                RegularLiteral(" k"),
                endLiteral
            )
        )
    }

    @Test
    fun `Single line string literals`() {
        singleLineTextLiteralCommonTests(
            '"', SingleLineStringLiteralStart, SingleLineStringLiteralEnd
        )

        interpolationTests("\"", SingleLineStringLiteralStart, SingleLineStringLiteralEnd)
    }

    @Test
    fun `Multi line string literals`() {
        verifyOutput(
            input = "\"\"\"a  \\a \\n \\u \\u0000 a\"\"\"",
            expectedTokens = listOf(
                MultiLineStringLiteralStart,
                RegularLiteral("a  \\a \\n \\u \\u0000 a"),
                MultiLineStringLiteralEnd
            )
        )
        verifyOutput(
            input = "\"\"\"a\nb\nc\"\"\"",
            expectedTokens = listOf(
                MultiLineStringLiteralStart, RegularLiteral("a\nb\nc"), MultiLineStringLiteralEnd
            )
        )
        verifyOutput(
            input = "\"\"\"a\nb\nc\"\"",
            expectedTokens = listOf(MultiLineStringLiteralStart, RegularLiteral("a\nb\nc\"\""))
        )
        verifyOutput(
            input = "\"\"\"a\nb\nc\"",
            expectedTokens = listOf(MultiLineStringLiteralStart, RegularLiteral("a\nb\nc\""))
        )
        verifyOutput(
            input = "\"\"\"a\nb\nc",
            expectedTokens = listOf(MultiLineStringLiteralStart, RegularLiteral("a\nb\nc"))
        )
        verifyOutput(
            input = "\"\"\"",
            expectedTokens = listOf(MultiLineStringLiteralStart)
        )
        verifyOutput(
            input = "\"\"",
            expectedTokens = listOf(SingleLineStringLiteralStart, SingleLineStringLiteralEnd)
        )
        verifyOutput(
            input = "\"",
            expectedTokens = listOf(SingleLineStringLiteralStart)
        )
        val quote = "\"\"\""

        val codeSnippet = """
            // comment1
            /* comment2 */
            "string"
            
        """.trimIndent()
        verifyOutput(
            input = "$quote$codeSnippet$quote",
            expectedTokens = listOf(
                MultiLineStringLiteralStart, RegularLiteral(codeSnippet), MultiLineStringLiteralEnd
            )
        )

        interpolationTests(quote, MultiLineStringLiteralStart, MultiLineStringLiteralEnd)
        
        for (quotNumber in 0..20) {
            verifyOutput(
                input = "\"".repeat(quotNumber + 6),
                expectedTokens = listOfNotNull(
                    MultiLineStringLiteralStart,
                    RegularLiteral("\"".repeat(quotNumber)).takeIf { quotNumber > 0 },
                    MultiLineStringLiteralEnd
                )
            )
            verifyOutput(
                input = "\"".repeat(quotNumber + 6) +"+3",
                expectedTokens = listOfNotNull(
                    MultiLineStringLiteralStart,
                    RegularLiteral("\"".repeat(quotNumber)).takeIf { quotNumber > 0 },
                    MultiLineStringLiteralEnd,
                    Add, IntegralNumberLiteral("3")
                )
            )
        }
    }

    @Test
    fun `Verbatim character`() {
        verifyOutput(
            input = "#±/\\",
            expectedTokens = listOf(Hash, Verbatim('±'), Div, Verbatim('\\'))
        )
    }
}
