package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter

typealias RowDataCallback = suspend (rowData: RowData) -> Unit