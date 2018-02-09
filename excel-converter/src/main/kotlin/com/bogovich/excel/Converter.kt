package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging

class Converter(val xmlWriter: DslXMLStreamWriter) {

    companion object : KLogging()

    val checkpointsMeta = mutableListOf<Checkpoint>()
    val checkpointsStream = mutableListOf<Checkpoint>()

//    fun meta(startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, metaCallback: writerLambdaWithCursor) {
//        meta({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
//                { cursor -> cursor.isCheckpoint(endRow, endSheet) },
//                metaCallback)
//    }
//
//    fun meta(startRow: Int = 0, startSheet: Int = 1, endCheck: checkStatement, metaCallback: writerLambdaWithCursor) {
//        meta({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, metaCallback)
//    }
//
//    fun meta(startCheck: checkStatement, endRow: Int, endSheet: Int = 1, metaCallback: writerLambdaWithCursor) {
//        meta(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, metaCallback)
//    }

    fun meta(startCheck: CheckStatement, endCheck: CheckStatement, metaCallback: WriterWithCursor) {
        checkpointsMeta.add(Checkpoint(startCheck, endCheck, metaCallback))
    }

//    fun stream(startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, streamCallback:
//    writerLambdaWithCursor) {
//        stream({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
//                { cursor -> cursor.isCheckpoint(endRow, endSheet) },
//                streamCallback)
//    }
//
//    fun stream(startRow: Int = 0, startSheet: Int = 1, endCheck: checkStatement, streamCallback: writerLambdaWithCursor) {
//        stream({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, streamCallback)
//    }
//
//    fun stream(startCheck: checkStatement, endRow: Int, endSheet: Int = 1, streamCallback: writerLambdaWithCursor) {
//        stream(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, streamCallback)
//    }

    fun stream(startCheck: CheckStatement, endCheck: CheckStatement, streamCallback: WriterWithCursor) {
        checkpointsStream.add(Checkpoint(startCheck, endCheck, streamCallback))
    }

    fun setUp(workSheetHandler: ExcelWorkSheetHandler) {
        workSheetHandler.cellCallback = { cursor ->
            //            map.add(cursor.reference to cursor.value)
        }
        workSheetHandler.rowStartCallback = { cursor ->
            var statusChanged = false
            checkpointsStream.forEach { checkpoint ->
                if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.STREAM) {
                    if (statusChanged) {
                        logger.error { "mode already changed! $cursor" }
                    }
                    statusChanged = true
                    cursor.mode = Cursor.ReadMode.STREAM
                }
            }
            checkpointsMeta.forEach { checkpoint ->
                if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.META) {
                    if (statusChanged) {
                        logger.error { "mode already changed! $cursor" }
                    }
                    statusChanged = true
                    cursor.mode = Cursor.ReadMode.META
                }
            }
        }
        workSheetHandler.rowEndCallback = { cursor ->
            //            if (cursor.streamData["A"].isNullOrEmpty()) {
//                cursor.mode = Cursor.ReadMode.META
//                cursor.saveStreamToMeta()
//            }
            if (cursor.mode == Cursor.ReadMode.STREAM) {
                checkpointsStream.forEach { checkpoint ->
                    checkpoint.run(cursor, xmlWriter)
                }
                println("STREAM ${cursor.streamData}")
            }
            if (cursor.mode == Cursor.ReadMode.META) {
                checkpointsMeta.forEach { checkpoint ->
                    checkpoint.run(cursor, xmlWriter)
                }
            }
        }
    }
}