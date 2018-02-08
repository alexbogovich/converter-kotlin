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
        workSheetHandler.cellCallback = { cellReference, formattedValue ->
            map.add(cellReference to formattedValue)
        }
        val reader = ExcelReader(inputStream, workSheetHandler)
        reader.read()

        println(map)
    }

}