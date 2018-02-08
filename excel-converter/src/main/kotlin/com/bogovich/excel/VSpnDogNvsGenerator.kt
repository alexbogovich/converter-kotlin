package com.bogovich.excel

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}


class Reader {


    fun read() {
        val map = mutableListOf<Pair<String, String>>()
        val inputStream = Files.newInputStream(Paths.get("/home/alex/IdeaProjects/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))

        val workSheetHandler = ExcelWorkSheetHandler()
        workSheetHandler.cellCallback = { cursor ->
            map.add(cursor.reference to cursor.value)
        }
        workSheetHandler.rowStartCallback = { cursor ->
            if (cursor.rowNumber == 16) {
                cursor.mode = Cursor.ReadMode.STREAM
            }
        }
        workSheetHandler.rowEndCallback = { cursor ->
            if (cursor.streamData["A"].isNullOrEmpty()) {
                cursor.mode = Cursor.ReadMode.META
                cursor.saveStreamToMeta()
            }
            if (cursor.mode == Cursor.ReadMode.STREAM) {
                println("STREAM ${cursor.streamData}")
            }
        }

        val cursor = Cursor()
        val reader = ExcelReader(inputStream, workSheetHandler, cursor)
        reader.read()

        println(cursor)
    }

}