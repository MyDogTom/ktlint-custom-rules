package com.github.mydogtom.ktlint.rules

import com.github.shyiko.ktlint.core.LintError
import com.github.shyiko.ktlint.test.format
import com.github.shyiko.ktlint.test.lint
import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test

class IndentationRuleTest {

    @Test
    fun testRule() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            /**
             * _
             */
            fun main() {
                val a = 0
                    val b = 0
                if (a == 0) {
                    println(a)
                }
                val b = builder().setX().setY()
                    .build()
               val c = builder("long_string" +
                    "")
            }

            class A {
                var x: String
                    get() = ""
                    set(v: String) { x = v }
            }
            """.trimIndent()
            )
        ).isEqualTo(listOf(
            LintError(12, 1, "indent", "Unexpected indentation (3) (it should be 4)"),
            LintError(13, 1, "indent", "Unexpected indentation (5) (it should be 4)")
        ))
    }

    @Test
    fun testVerticallyAlignedParametersDoNotTriggerAnError() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            data class D(val a: Any,
                         @Test val b: Any,
                         val c: Any = 0) {
            }

            data class D2(
                val a: Any,
                val b: Any,
                val c: Any
            ) {
            }

            fun f(a: Any,
                  b: Any,
                  c: Any) {
            }

            fun f2(
                a: Any,
                b: Any,
                c: Any
            ) {
            }
            """.trimIndent()
            )
        ).isEmpty()
        Assertions.assertThat(
            IndentationRule().lint(
                """
            class A(
               //
            ) {}
            """.trimIndent()
            )
        ).isEqualTo(listOf(
            LintError(2, 1, "indent", "Unexpected indentation (3) (it should be 4)")
        ))
    }

    @Test
    fun testWithCustomIndentSize() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            /**
             * _
             */
            fun main() {
              val v = ""
              println(v)
            }

            class A {
              var x: String
                get() = ""
                set(v: String) { x = v }
            }
            """.trimIndent(),
                mapOf("indent_size" to "2")
            )
        ).isEmpty()
    }

    @Test
    fun testErrorWithCustomIndentSize() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun main() {
               val v = ""
                println(v)
            }
            """.trimIndent(),
                mapOf("indent_size" to "3")
            )
        ).isEqualTo(listOf(
            LintError(3, 1, "indent", "Unexpected indentation (4) (it should be 3)")
        ))
    }

    @Test
    fun testErrorWithIndentSizeUnset() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun main() {
               val v = ""
                println(v)
            }
            """.trimIndent(),
                mapOf("indent_size" to "unset")
            )
        ).isEmpty()
    }

    @Test
    fun testShouldReportIncorrectIndentOfFirstParameter() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun x(
                 x: Int = 0,
                y: Int = 0
            ) {
            }
            """.trimIndent(),
                script = true
            )
        ).isEqualTo(listOf(
            LintError(2, 1, "indent", "Unexpected indentation (5) (it should be 4)"),
            LintError(3, 1, "indent", "Unexpected indentation (4) (parameters should be vertically aligned)")
        ))
    }

    @Test
    fun testShouldRespectContinuationIndent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                class TestContinuation {
                    fun main() {
                        val list = listOf(
                              listOf(
                                    "string",
                                    "another string"
                              ),
                              listOf("one", "two")
                        )
                    }
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun `testUseContinuationIndentForConcatenation`() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                class TestSubClass {
                    fun asdf(string: String) = string
                    val c = asdf("long_string" +
                          "")
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForDotQualifiedExpression() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                fun funA() {
                    ClassA()
                          .methodA()
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseIndentForObjectImplementation() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                @Test fun field() {
                    field.validateWith()
                          .handleWith(object : InterfaceA {
                              override fun handleFailed(input: String, errors: List<String>) {
                                  failedMessages.addAll(errors)
                              }

                              override fun handleSucceeded() {
                                  succeededMessages.add("success")
                              }
                          })
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testComplexAssignmentQualifiedAccessAndFunctionBody() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun funA() =
                  doStuff().use {
                      while (it.moveToNext()) {
                          doMore()
                      }
                  }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForArgumentList() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                fun funA() {
                    val valueA =
                          listOf(ClassA(),
                                ClassB())
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentInsideParenthesis() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                fun funA() {
                    val valA = ClassA(
                          field = (
                                ClassB(
                                      fieldBOne = "one",
                                      fieldBTwo = "two",
                                      fieldBThree = 0
                                ))
                    )
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseIndentForCustomGetter() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                val storyBody: String
                    get() = String.format(body, "")
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentAfterAssignment() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
        val valueA =
              "{\"title\"}"
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForSuperTypeList() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            class ClassA(fieldA: TypeA,
                         fieldB: TypeB = DefaultB) :
                  SuperClassA(fieldA, fieldB)
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseIndentForFunctionBody() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                fun funA() {
                    val valueA = ClassA()
                    valueA.doStuff()
                    assertThat(valueA.getFieldB()).isEqualTo(100L)
                }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentInsideSuperTypeList() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            class ClassA : ClassB(), InterfaceA,
                  InterfaceB {
            }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForTypeProjection() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            val variable: SuperTpe<TypeA,
                  TypeB> = Implementation()
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test()
    @Ignore
    //not sure if it should use continuation indent or same as parameters
    fun testCommentBetweenParameterListShouldUseSameIndent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            data class MyClass(val a: String,
                               val b: String,
                  //comment between properties
                               val c: String)
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForAssignment() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun funA() {
                val (a, b, c) =
                      anotherFun()
            }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test()
    fun testUseContinuationIndentForTypeCasting() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun funA() = funB() as
                  TypeA
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForConstructorDelegation() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            class A : B() {
                constructor(a: String) :
                      this(a)
            }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun shouldUseContinuationInsideSafeQualifiedExpression() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            val valueA = call()
                  //comment
                  ?.chainCallC { it.anotherCall() }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testUseContinuationIndentForTypeDeclaration() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            private fun funA(a: Int, b: String):
                  MyTypeA {
                return MyTypeA(a, b)
            }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testIgnoreSuperTypeListWhenCalculatePreviousIndent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            class ClassA(a: TypeA) :
                  BasePresenter<View>() {

                private lateinit var view: View
            }
              """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testIgnoreConstructorDelegationCallWhenCalculatingPreviousIntent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
                class MyClass{
                    constructor(a: TypeA) :
                          super(a) {
                        init(a)
                    }
                }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test()
    @Ignore
    //Not sure it should be supported. Recommended way can be to put each argument on separate line
    fun testFuncIndent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun funA(a: A, b: B) {
                return funB(a,
                      b, { (id) ->
                    funC(id)
                }
                )
            }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testComplexValueArgumentUsage() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun data() = listOf(
                  with(ClassA()) {
                      arrayOf({ paramA: TypeA ->
                          paramA.build()
                      }, funB())
                  },
                  arrayOf({ paramA: TypeA -> paramA.build() },
                        funB()
                  )
            )
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testIgnoreCommentWhenCalculateParentIndent() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            fun funA(argA: String) =
                  // comment
                  // comment
                  call(argA)
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun testFormatWithRegularIndent() {
        Assertions.assertThat(
            IndentationRule().format(
                """
            fun funA(argA: String) {
              return argA
            }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        )
            .isEqualTo(
                """
            fun funA(argA: String) {
                return argA
            }
            """.trimIndent()
            )
    }

    @Test
    fun testFormatWithContinuationIndent() {
        Assertions.assertThat(
            IndentationRule().format(
                """
            val valueA = call()
             ?.chainCallC { it.anotherCall() }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        )
            .isEqualTo(
                """
            val valueA = call()
                  ?.chainCallC { it.anotherCall() }
            """.trimIndent()
            )
    }

    @Test
    fun shouldAlignParameters() {
        Assertions.assertThat(
            IndentationRule().format(
                """
            fun funA(a: A,
             b: B) {
                return ""
            }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEqualTo(
            """
            fun funA(a: A,
                     b: B) {
                return ""
            }
            """.trimIndent()
        )
    }

    @Test
    fun testLambdaParametersShouldBeAligned() {
        Assertions.assertThat(
            IndentationRule().lint(
                """
            val fieldExample =
                  LongNameClass { paramA,
                                  paramB,
                                  paramC ->
                      ClassB(paramA, paramB, paramC)
                  }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEmpty()
    }

    @Test
    fun shouldRespectPreviousIntent() {
        Assertions.assertThat(
            IndentationRule().format(
                """
            fun setUp() {
                 helper = ClassA(
                    paramA,
                    paramB
                )
            }
            """.trimIndent(),
                mapOf(
                    "indent_size" to "4",
                    "continuation_indent_size" to "6"
                )
            )
        ).isEqualTo(
            """
            fun setUp() {
                helper = ClassA(
                      paramA,
                      paramB
                )
            }
            """.trimIndent()
        )
    }
}