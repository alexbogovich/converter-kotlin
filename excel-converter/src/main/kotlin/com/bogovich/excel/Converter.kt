package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging

class Converter(val xmlWriter: DslXMLStreamWriter) {

    companion object : KLogging()

    val checkpointsMeta = mutableListOf<Checkpoint>()
    val checkpointsStream = mutableListOf<Checkpoint>()

    fun meta(startCheck: CheckStatement, endCheck: CheckStatement, metaCallback: WriterWithCursor, type: Checkpoint
    .Type = Checkpoint.Type.PRE) {
        checkpointsMeta.add(Checkpoint(startCheck, endCheck, metaCallback, type))
    }

    fun stream(startCheck: CheckStatement, endCheck: CheckStatement, streamCallback: WriterWithCursor, type: Checkpoint
    .Type = Checkpoint.Type.PRE) {
        checkpointsStream.add(Checkpoint(startCheck, endCheck, streamCallback, type))
    }

    fun setUp(workSheetHandler: ExcelWorkSheetHandler) {
        workSheetHandler.cellCallback = { cursor ->
            //            map.add(cursor.reference to cursor.value)
        }
        workSheetHandler.rowStartCallback = { cursor ->
            var statusChanged = false
            checkpointsStream
                    .filter { it.type == Checkpoint.Type.PRE }
                    .map { checkpoint ->
                        if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.STREAM) {
                            logger.info { "Change status in pre checkpointsStream" }
                            if (statusChanged) {
                                logger.error { "mode already changed! $cursor" }
                            }
                            statusChanged = true
                            cursor.mode = Cursor.ReadMode.STREAM
                        }
                    }
            checkpointsMeta
                    .filter { it.type == Checkpoint.Type.PRE }
                    .map { checkpoint ->
                        if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.META) {
                            logger.info { "Change status in pre checkpointsMeta" }
                            if (statusChanged) {
                                logger.error { "mode already changed! $cursor" }
                            }
                            statusChanged = true
                            cursor.mode = Cursor.ReadMode.META
                        }
                    }
        }
        workSheetHandler.rowEndCallback = { cursor ->
            var statusChanged = false

            checkpointsStream
                    .filter { it.type == Checkpoint.Type.POST }
                    .map { checkpoint ->
                        if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.STREAM) {
                            logger.info { "Change status in post checkpointsStream" }
                            if (statusChanged) {
                                logger.error { "mode already changed! $cursor" }
                            }
                            statusChanged = true
                            cursor.mode = Cursor.ReadMode.STREAM
                        }
                    }
            checkpointsMeta
                    .filter { it.type == Checkpoint.Type.POST }
                    .map { checkpoint ->
                        if (checkpoint.check(cursor) && cursor.mode != Cursor.ReadMode.META) {
                            logger.info { "Change status in post checkpointsMeta" }
                            if (statusChanged) {
                                logger.error { "mode already changed! $cursor" }
                            }
                            statusChanged = true
                            cursor.mode = Cursor.ReadMode.META
                            cursor.saveStreamToMeta()
                        }
                    }


            when (cursor.mode) {
                Cursor.ReadMode.STREAM -> {
                    checkpointsMeta
                            .filter { it.state == Checkpoint.State.USE }
                            .map { it.writeAndClose(cursor, xmlWriter) }
                    checkpointsStream
                            .filter { it.state == Checkpoint.State.USE || it.state == Checkpoint.State.NEW }
                            .map {
                                it.run(cursor, xmlWriter)
                                if( it.state != Checkpoint.State.CLOSE) {
                                    logger.info { "STREAM ${cursor.streamData}" }
                                }
                            }
                }
                Cursor.ReadMode.META -> {
                    checkpointsMeta
                            .filter { it.state == Checkpoint.State.USE || it.state == Checkpoint.State.NEW }
                            .map { it.run(cursor, xmlWriter) }
                }
            }
        }
    }
}