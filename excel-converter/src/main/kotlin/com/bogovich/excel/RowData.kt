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
data class RowData (val sheetNum: Int, val rowNum: Int, val data:Map<String, CellData>) {
    fun cell(shortRef: String, removeDoubleQuotes: Boolean = true): String {
        val s = data[shortRef]?.data.orEmpty()
        if (removeDoubleQuotes && s.startsWith("\"") && s.endsWith("\"")) {
            return s.removeSurrounding("\"")
        }
        return s
    }
}
data class SheetData (val sheetNum: Int)



