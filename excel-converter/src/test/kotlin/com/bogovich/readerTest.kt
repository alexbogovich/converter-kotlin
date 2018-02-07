package com.bogovich

import com.bogovich.excel.ExcelReader
import com.bogovich.excel.ExcelWorkSheetHandler
import org.junit.Test

class TestSource {
    @Test
    fun f() {

        val map = mutableListOf<Pair<String, String>>()

        val file = TestSource::class.java.getResource("/Sample-Person-Data.xlsx").file
        val workSheetHandler = ExcelWorkSheetHandler()
        workSheetHandler.cellCallback = { cellReference, formattedValue ->
            map.add(cellReference to formattedValue)
        }
        val reader = ExcelReader(file, workSheetHandler)
        reader.read()

        println(map)
    }
}