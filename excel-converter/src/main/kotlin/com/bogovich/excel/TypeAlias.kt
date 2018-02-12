package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter

typealias WriterWithCursor = DslXMLStreamWriter.(cursor: Cursor) -> Unit
typealias CheckStatement = (cursor: Cursor) -> Boolean
typealias CursorOperation = (cursor: Cursor) -> Unit
typealias RowDataCallback = suspend (rowData: RowData) -> Unit