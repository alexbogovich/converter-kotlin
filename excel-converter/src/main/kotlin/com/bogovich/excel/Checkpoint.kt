package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging

data class Checkpoint(private val startCheck: CheckStatement,
                      private val endCheck: CheckStatement,
                      private val callback: WriterWithCursor,
                      val type: Type = Type.PRE,
                      var state: State = State.NEW,
                      val prepareCallback: () -> Unit = {},
                      val endCallback: () -> Unit = {}
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
                if (cursor.mode == Cursor.ReadMode.STREAM) {
                    prepareCallback()
                }
            }
            if (cursor.mode == Cursor.ReadMode.STREAM) {
                callback(xmlWriter, cursor)
            }
        } else {
            if (state == State.USE) {
                if (cursor.mode == Cursor.ReadMode.META) {
                    if (endCheck(cursor)) {
                        logger.info { "close state" }
                        callback(xmlWriter, cursor)
                        state = State.CLOSE
                    }
                } else {
                    logger.info { "close state" }
                    endCallback()
                    state = State.CLOSE
                }
                if( cursor.mode == Cursor.ReadMode.STREAM) {
                    logger.info { "switch to meta mode" }
                    cursor.mode = Cursor.ReadMode.META
                }
            }
        }
    }

    fun writeAndClose(cursor: Cursor, xmlWriter: DslXMLStreamWriter) {
        if (state == State.USE) {
            logger.info { "write meta" }
            callback(xmlWriter, cursor)
            logger.info { "close meta" }
            state = State.CLOSE
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