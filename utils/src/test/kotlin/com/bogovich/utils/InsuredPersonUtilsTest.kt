package com.bogovich.utils

import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InsuredPersonUtilsTest {

    @TestFactory
    fun `calculate control number`() = validInsuredPersonProvider.map { data ->
        DynamicTest.dynamicTest("calculate control code ${data.code} from ${data.number}") {
            Assertions.assertEquals(data.code, InsuredPersonUtils.controlNumber(data.number))
        }
    }

    @TestFactory
    fun `calculate insured number`() = validInsuredPersonProvider.map { data ->
        DynamicTest.dynamicTest("get insured number ${data.resultCode} from ${data.number}") {
            Assertions.assertEquals(data.resultCode, InsuredPersonUtils.formattedNumber(data.number))
        }
    }

    private val validInsuredPersonProvider = listOf(
            CodeTestData(86_754_303, "00", "086-754-303 00"),
            CodeTestData(87_654_303, "00", "087-654-303 00"),
            CodeTestData(87_654_302, "00", "087-654-302 00"),
            CodeTestData(112_233_445, "95", "112-233-445 95"),
            CodeTestData(116_973_385, "89", "116-973-385 89")
    )

    @Nested
    inner class ErrorCases {
        @TestFactory
        fun `exception on invalid insured number`() = invalidNumber.map { invalidNumber ->
            DynamicTest.dynamicTest("exception for $invalidNumber") {
                Assertions.assertThrows(IllegalArgumentException::class.java) {
                    InsuredPersonUtils.formattedNumber(invalidNumber)
                }
            }
        }

        private val invalidNumber = listOf(
                -100,
                -1,
                1_000_000_000,
                9_999_999_999
        )

    }

}

internal data class CodeTestData(val number: Long, val code: String, val resultCode: String)