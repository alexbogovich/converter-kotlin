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

        val out = System.out
        val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))
        writer.document {
            "ЭДПФР" tag {
                "РНПФ" tag {
                    meta(converter, 0, 1, 16, 1) {cursor ->

                    }
                    stream(converter, 16, 1, Int.MAX_VALUE, 1) {cursor ->

                    }
                    reader.read()
                }
            }

        }
        println(cursor)
    }

}

fun DslXMLStreamWriter.stream(converter: Converter,
                              startRow: Int,
                              startSheet: Int,
                              endRow: Int,
                              endSheet: Int,
                              init: DslXMLStreamWriter.(cursor: Cursor) -> Unit) {
    converter.stream(startRow, startSheet, endRow, endSheet, init)
}

fun DslXMLStreamWriter.meta(converter: Converter,
                            startRow:Int = 0,
                            startSheet: Int = 1,
                            endRow: Int = Int.MAX_VALUE,
                            endSheet:Int = 1,
                            init: DslXMLStreamWriter.(cursor: Cursor) -> Unit) {
    converter.meta(startRow, startSheet, endRow, endSheet, init)
}
