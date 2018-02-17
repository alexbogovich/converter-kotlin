package com.bogovich.utils

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InsuredPersonUtilsTest {

    @ParameterizedTest
    @MethodSource("validInsuredPersonProvider")
    fun `calculate control number`(data:CodeTestData) {
        Assertions.assertThat(InsuredPersonUtils.controlNumber(data.number)).isEqualTo(data.code)
    }

    @ParameterizedTest
    @MethodSource("validInsuredPersonProvider")
    fun `calculate insured number`(data:CodeTestData) {
        Assertions.assertThat(InsuredPersonUtils.formattedNumber(data.number)).isEqualTo(data.resultCode)
    }

    private fun validInsuredPersonProvider() = Stream.of(
            CodeTestData(86_754_303, "00", "086-754-303 00"),
            CodeTestData(87_654_303, "00", "087-654-303 00"),
            CodeTestData(87_654_302, "00", "087-654-302 00"),
            CodeTestData(112_233_445, "95", "112-233-445 95"),
            CodeTestData(116_973_385, "89", "116-973-385 89")
    )
}

internal data class CodeTestData(val number: Long, val code: String, val resultCode: String)