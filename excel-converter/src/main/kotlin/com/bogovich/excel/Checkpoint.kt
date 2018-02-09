package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter

data class Checkpoint(private val startCheck: (cursor: Cursor) -> Boolean,
                      private val endCheck: (cursor: Cursor) -> Boolean,
                      private val callback: DslXMLStreamWriter.(cursor: Cursor) -> Unit
) {
    fun check(cursor: Cursor): Boolean {
        return startCheck(cursor) && !endCheck(cursor)
    }

    fun run(cursor: Cursor, xmlWriter: DslXMLStreamWriter) {
        if (check(cursor)) {
            callback(xmlWriter, cursor)
        }
    }
}