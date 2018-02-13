package com.github.mydogtom.ktlint.rules

import com.github.mydogtom.ktlint.custom.rules.ClassAndFunctionHeaderFormatRule
import com.github.shyiko.ktlint.test.lint
import com.github.shyiko.ktlint.core.LintError
import com.github.shyiko.ktlint.test.format
import org.assertj.core.api.Assertions
import org.junit.Test

class ClassAndFunctionHeaderFormatRuleTest {
    private val errorMessageMissedNewLineForParameter = "Parameter should be on separate line with indentation"
    private val errorMessageMissedNewLineForParenthesis = "Parenthesis should be on new line"
    private val expectedRuleId = "class-and-function-header-format"
    private val configUserData = mapOf(
        "indent_size" to "4",
        "continuation_indent_size" to "6",
        "max_line_length" to "100")
    private fun errorMessageWhenWrongIndent(actualIndent: Int, expectedIndent: Int): String {
        return "Unexpected indentation for parameter $actualIndent (should be $expectedIndent)"
    }

    @Test
    fun testClassWithAbsentLineBreak() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            class ClassA(paramA: String, paramB: String,
                         paramC: String)
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            listOf(
                LintError(1, 14, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(1, 30, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(2, 14, expectedRuleId, errorMessageWhenWrongIndent(13, 4)),
                LintError(2, 28, expectedRuleId, errorMessageMissedNewLineForParenthesis))
        )
    }

    @Test
    fun testFormatClassWithAbsentLineBreak() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().format(
                """
            class ClassA(paramA: String, paramB: String,
                         paramC: String)
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            """
            class ClassA(
                paramA: String,
                paramB: String,
                paramC: String
            )
            """.trimIndent()
        )
    }

    @Test
    fun testValidClassDefinitionWithMultipleLines() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            class ClassA(
                paramA: String,
                paramB: String,
                paramC: String
            )
            """.trimIndent(),
                configUserData
            )
        ).isEmpty()
    }

    @Test
    fun testValidClassDefinitionOnOneLine() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            class ClassA(paramA: String, paramB: String, paramC: String)
            """.trimIndent(),
                configUserData
            )
        ).isEmpty()
    }

    @Test
    fun testErrorWhenFirstParameterIsNotOnNewLine() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            fun f(a: Any,
                  b: Any,
                  c: Any) {
            }
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            listOf(
                LintError(1, 7, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(2, 7, expectedRuleId, errorMessageWhenWrongIndent(6, 4)),
                LintError(3, 7, expectedRuleId, errorMessageWhenWrongIndent(6, 4)),
                LintError(3, 13, expectedRuleId, errorMessageMissedNewLineForParenthesis)
            )
        )
    }

    @Test
    fun testFormatWhenFirstParameterIsNoOnNewLine() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().format(
                """
            fun f(a: Any,
                  b: Any,
                  c: Any) {
            }
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            """
            fun f(
                a: Any,
                b: Any,
                c: Any
            ) {
            }
            """.trimIndent()
        )
    }

    @Test
    fun testIgnoreLambdaParameters() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            val fieldExample =
                  LongNameClass { paramA,
                                  paramB,
                                  paramC ->
                      ClassB(paramA, paramB, paramC)
                  }
            """.trimIndent(),
                configUserData
            )
        ).isEmpty()
    }

    @Test
    fun testFailWhenWrongIndentIsUsed() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            class A {
                fun f(
                   a: Any,
                   b: Any) {
                }
            }
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            listOf(
                LintError(3, 8, expectedRuleId, errorMessageWhenWrongIndent(3, 4)),
                LintError(4, 8, expectedRuleId, errorMessageWhenWrongIndent(3, 4)),
                LintError(4, 14, expectedRuleId, errorMessageMissedNewLineForParenthesis)
            )
        )
    }

    @Test
    fun testRespectOuterIndent() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().format(
                """
            class A {
                fun f(a: Any,
                      b: Any,
                      c: Any) {
                }
            }
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            """
            class A {
                fun f(
                    a: Any,
                    b: Any,
                    c: Any
                ) {
                }
            }
            """.trimIndent()
        )
    }

    @Test
    fun testRespectOuterIndentWhenCalculateParanthesisIndent() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().format(
                """
            class A {
                fun f(a: Any,
                      b: Any,
                      c: Any
                   ) {
                }
            }
            """.trimIndent(),
                configUserData
            )
        ).isEqualTo(
            """
            class A {
                fun f(
                    a: Any,
                    b: Any,
                    c: Any
                ) {
                }
            }
            """.trimIndent()
        )
    }

    @Test
    fun testFailWhenHeaderIsTooLong() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
            class WithLongClassHeader(parameter1: Int, parameter2: Int, parameter3: Int) {
              fun a() = ""
            }
            """.trimIndent(),
                mapOf("max_line_length" to "50")
            )
        ).isEqualTo(
            listOf(
                LintError(1, 27, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(1, 44, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(1, 61, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(1, 76, expectedRuleId, errorMessageMissedNewLineForParenthesis)
            )
        )
    }

    @Test
    fun testFormatClassWithAllCases() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().format(
                """
            class WithLongClassHeader(parameter1: Int, parameter2: Int, parameter3: Int) {
                constructor(parameter1: Int, parameter2: Int, parameter3: Int) {
                }

                constructor() {
                    val defaultParameter1 = 0
                    val defaultParameter2 = 0
                    val defaultParameter3 = 0
                    this(defaultParameter1, defaultParameter2, defaultParameter3)
                }

                fun withLongDefinition(param1: Int, parameter2: Int, parameter3: Int): String {
                    return " "
                }

                fun a(param1: Int): String {
                    return "very long line that exceeds fifty characters, to make sure that this doesn't count"
                }
            }
            """.trimIndent(),
                mapOf("max_line_length" to "50")
            )
        ).isEqualTo(
            """
            class WithLongClassHeader(
                parameter1: Int,
                parameter2: Int,
                parameter3: Int
            ) {
                constructor(
                    parameter1: Int,
                    parameter2: Int,
                    parameter3: Int
                ) {
                }

                constructor() {
                    val defaultParameter1 = 0
                    val defaultParameter2 = 0
                    val defaultParameter3 = 0
                    this(defaultParameter1, defaultParameter2, defaultParameter3)
                }

                fun withLongDefinition(
                    param1: Int,
                    parameter2: Int,
                    parameter3: Int
                ): String {
                    return " "
                }

                fun a(param1: Int): String {
                    return "very long line that exceeds fifty characters, to make sure that this doesn't count"
                }
            }
            """.trimIndent()
        )
    }

    @Test
    fun testNoErrorWhenShorterThanMaxLengthSize() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
                class A {
                    fun f(a: Any, b: Any) = ""
                }
                """.trimIndent(),
                mapOf("max_line_length" to "31")
            )
        ).isEmpty()
    }

    @Test
    fun testNoErrorWhenEqualToMaxLengthSize() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
                class A {
                    fun f(a: Any, b: Any) = ""
                }
                """.trimIndent(),
                mapOf("max_line_length" to "30")
            )
        ).isEmpty()
    }

    @Test
    fun testErrorWhenLongerThanMaxLengthSize() {
        Assertions.assertThat(
            ClassAndFunctionHeaderFormatRule().lint(
                """
                class A {
                    fun f(a: Any, b: Any) = ""
                }
                """.trimIndent(),
                mapOf("max_line_length" to "29")
            )
        ).isEqualTo(
            listOf(
                LintError(2, 11, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(2, 19, expectedRuleId, errorMessageMissedNewLineForParameter),
                LintError(2, 25, expectedRuleId, errorMessageMissedNewLineForParenthesis)
            )
        )
    }
}
