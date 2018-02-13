package com.bogovich.excel

import com.bogovich.utils.CellUtils
import org.apache.poi.ss.usermodel.Cell

data class CellData (val ref: String, val data: String) {
    companion object {
        fun of(cell: Cell): CellData {
            return CellData(CellUtils.getCellRef(cell.columnIndex), cell.stringCellValue)
        }
    }
}
data class RowData (val sheetNum: Int, val rowNum: Int, val data:Map<String, CellData>)
data class SheetData (val sheetNum: Int)