package com.bogovich.xml.writer.dsl

interface EmptyElementDsl {
    infix fun String.attr(value: Any)
}