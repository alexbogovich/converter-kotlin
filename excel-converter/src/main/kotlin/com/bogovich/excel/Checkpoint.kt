package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter

data class Checkpoint(private val startCheck: CheckStatement,
                      private val endCheck: CheckStatement,
                      private val callback: WriterWithCursor
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