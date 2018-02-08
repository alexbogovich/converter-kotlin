package com.bogovich.excel

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}


class Reader {


    fun read() {
        val map = mutableListOf<Pair<String, String>>()

        val file = this::class.java.getResource("/РНПФ-01.xlsx").file
        val workSheetHandler = ExcelWorkSheetHandler()
        workSheetHandler.cellCallback = { cellReference, formattedValue ->
            map.add(cellReference to formattedValue)
        }
        val reader = ExcelReader(file, workSheetHandler)
        reader.read()

        println(map)
    }

}