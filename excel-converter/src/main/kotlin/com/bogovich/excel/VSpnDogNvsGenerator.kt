package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.stream.XMLOutputFactory

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}


class Reader {


    fun read() {
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

        val out = System.out
        val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))
        writer.document {
            "ЭДПФР" tag {
                "РНПФ" tag {
                    meta(converter, 0, 1, 16, 1) { cursor ->

                    }
                    //{cursor -> cursor.streamData["A"].isNullOrBlank()}
                    stream(converter, 16, 1, Int.MAX_VALUE, 1) { cursor ->

                    }
                    reader.read()
                }
            }

        }
        println(cursor)
    }

}

//fun DslXMLStreamWriter.stream(converter: Converter,
//                              startRow: Int,
//                              startSheet: Int,
//                              endRow: Int,
//                              endSheet: Int,
//                              init: writerLambdaWithCursor) {
//    converter.stream(startRow, startSheet, endRow, endSheet, init)
//}
//
//fun DslXMLStreamWriter.meta(converter: Converter,
//                            startRow:Int = 0,
//                            startSheet: Int = 1,
//                            endRow: Int = Int.MAX_VALUE,
//                            endSheet:Int = 1,
//                            init: writerLambdaWithCursor) {
//    converter.meta(startRow, startSheet, endRow, endSheet, init)
//}

fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
    converter.meta({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
            metaCallback)
}

fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, metaCallback: WriterWithCursor) {
    converter.meta({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, metaCallback)
}

fun DslXMLStreamWriter.meta(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
    converter.meta(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, metaCallback)
}


fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, streamCallback:
WriterWithCursor) {
    converter.stream({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
            streamCallback)
}

fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, streamCallback: WriterWithCursor) {
    converter.stream({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, streamCallback)
}

fun DslXMLStreamWriter.stream(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, streamCallback: WriterWithCursor) {
    converter.stream(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, streamCallback)
}