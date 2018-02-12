package com.bogovich.excel

data class CellData (val ref: String, val data: String)
data class RowData (val sheetNum: Int, val rowNum: Int, val data:List<CellData>)
data class SheetData (val sheetNum: Int)