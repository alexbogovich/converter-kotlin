package com.bogovich.utils

import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

object InsuredPersonUtils : Format() {

    fun format(insuredPersonNumberValue: Long?): String {
        return format(insuredPersonNumberValue, StringBuffer(), null).toString()
    }

    fun format(insuredPersonNumberValue: String): String {
        return format(insuredPersonNumberValue, StringBuffer(), null).toString()
    }

    override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition?): StringBuffer {
        if (obj == null) {
            toAppendTo.append("null")
            return toAppendTo
        }
        return if (obj is Long)
            format(obj as Long?, toAppendTo, pos)
        else if (obj is String)
            format(obj as String, toAppendTo, pos)
        else
            throw IllegalArgumentException("Insured number need be Long. (obj is " + obj.javaClass + ")")
    }

    fun format(insuredPersonNumberValue: String, toAppendTo: StringBuffer, pos: FieldPosition?): StringBuffer {
        var insuredPersonNumberValue = insuredPersonNumberValue
        val insuredPersonNumberValueBuilder = StringBuilder(insuredPersonNumberValue)
        while (insuredPersonNumberValueBuilder.length < 9)
            insuredPersonNumberValueBuilder.insert(0, "0")
        insuredPersonNumberValue = insuredPersonNumberValueBuilder.toString()
        toAppendTo.append(insuredPersonNumberValue.substring(0, 3))
        toAppendTo.append('-')
        toAppendTo.append(insuredPersonNumberValue.substring(3, 6))
        toAppendTo.append('-')
        toAppendTo.append(insuredPersonNumberValue.substring(6, 9))
        return toAppendTo
    }

    fun format(insuredPersonNumberValue: Long?, toAppendTo: StringBuffer, pos: FieldPosition?): StringBuffer {
        if (insuredPersonNumberValue!! < MIN_INSURED_PERSON_NUMBER_VALUE)
            throw IllegalArgumentException("Cannot format insured person number value lt " + MIN_INSURED_PERSON_NUMBER_VALUE)
        val firstGroup = (insuredPersonNumberValue / 1000000).toInt()
        val secondGroup = (insuredPersonNumberValue % 1000000 / 1000).toInt()
        val thirdGroup = (insuredPersonNumberValue % 1000).toInt()
        if (firstGroup < 100) toAppendTo.append('0')
        if (firstGroup < 10) toAppendTo.append('0')
        toAppendTo.append(firstGroup)
        toAppendTo.append('-')
        if (secondGroup < 100) toAppendTo.append('0')
        if (secondGroup < 10) toAppendTo.append('0')
        toAppendTo.append(secondGroup)
        toAppendTo.append('-')
        if (thirdGroup < 100) toAppendTo.append('0')
        if (thirdGroup < 10) toAppendTo.append('0')
        toAppendTo.append(thirdGroup)
        return toAppendTo
    }

    override fun parseObject(source: String, pos: ParsePosition): Any? {
        return null
    }

    private const val MIN_INSURED_PERSON_NUMBER_VALUE = 1001998
}