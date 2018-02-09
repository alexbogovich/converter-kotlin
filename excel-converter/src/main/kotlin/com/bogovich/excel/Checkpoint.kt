package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging

data class Checkpoint(private val startCheck: CheckStatement,
                      private val endCheck: CheckStatement,
                      private val callback: WriterWithCursor,
                      val type: Type = Type.PRE,
                      var state: State = State.NEW
) {

    companion object : KLogging()

    fun check(cursor: Cursor): Boolean {
        return startCheck(cursor) && !endCheck(cursor)
    }

    fun run(cursor: Cursor, xmlWriter: DslXMLStreamWriter) {
        if (check(cursor)) {
            logger.info { "invoke Checkpoint.run" }
            if (state == State.NEW) {
                logger.info { "open state" }
                state = State.USE
            }
            callback(xmlWriter, cursor)
        } else {
            if (state == State.USE) {
                logger.info { "close state" }
                state = State.CLOSE
                if( cursor.mode == Cursor.ReadMode.STREAM) {
                    logger.info { "switch to meta mode" }
                    cursor.mode = Cursor.ReadMode.META
                }
            }
        }
    }

    enum class Type {
        POST,
        PRE
    }

    enum class State {
        NEW,
        PREPARE_USE,
        USE,
        STOP_USE,
        CLOSE
    }
}