package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}


class Reader {


    fun read() {
        val map = mutableListOf<Pair<String, String>>()
        val inputStream = Files.newInputStream(Paths.get("C:/Users/aleksandr.bogovich/Desktop/my staff/practice/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))

        val workSheetHandler = ExcelWorkSheetHandler()
//        workSheetHandler.cellCallback = { cursor ->
//            map.add(cursor.reference to cursor.value)
//        }
//        workSheetHandler.rowStartCallback = { cursor ->
//            if (cursor.rowNumber == 16) {
//                cursor.mode = Cursor.ReadMode.STREAM
//            }
//        }
//        workSheetHandler.rowEndCallback = { cursor ->
//            if (cursor.streamData["A"].isNullOrEmpty()) {
//                cursor.mode = Cursor.ReadMode.META
//                cursor.saveStreamToMeta()
//            }
//            if (cursor.mode == Cursor.ReadMode.STREAM) {
//                println("STREAM ${cursor.streamData}")
//            }
//        }
        val cursor = Cursor()
        val converter = Converter(DslXMLStreamWriter(null))
        converter.setUp(workSheetHandler)
        val reader = ExcelReader(inputStream, workSheetHandler, cursor)

        reader.read()
        println(cursor)
    }

}

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

class Converter(val xmlWriter: DslXMLStreamWriter) {

    companion object : KLogging()

    val checkpointsMeta = mutableListOf<Checkpoint>()
    val checkpointsStream = mutableListOf<Checkpoint>()

    fun meta(startRow: Int = 0, startSheet: Int = 0, endRow: Int, endSheet: Int = 0, meta: DslXMLStreamWriter.
    (cursor: Cursor) -> Unit) {
        meta({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
                { cursor -> cursor.isCheckpoint(endRow, endSheet) },
                meta)
    }

    fun stream(startRow: Int = 0, startSheet: Int = 0, endRow: Int, endSheet: Int = 0, stream: DslXMLStreamWriter.
    (cursor: Cursor) -> Unit) {
        stream({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
                { cursor -> cursor.isCheckpoint(endRow, endSheet) },
                stream)
    }

    fun meta(startCheck: (cursor: Cursor) -> Boolean, endCheck: (cursor: Cursor) -> Boolean, meta: DslXMLStreamWriter.
    (cursor: Cursor) -> Unit) {
        checkpointsMeta.add(Checkpoint(startCheck, endCheck, meta))
    }

    fun stream(startCheck: (cursor: Cursor) -> Boolean, endCheck: (cursor: Cursor) -> Boolean, stream: DslXMLStreamWriter.
    (cursor: Cursor) -> Unit) {
        checkpointsStream.add(Checkpoint(startCheck, endCheck, stream))
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