package com.bogovich.excel

data class Cursor(
        var sheetNumber: Int = 0,
        var sheetName: String = "",
        var rowNumber: Int = 0,
        var reference: String = "",
        var value: String = "",
        var mode: ReadMode = ReadMode.META,
        var metaData: MutableMap<String, String> = mutableMapOf(),
        var streamData: MutableMap<String, String> = mutableMapOf()
) {
    enum class ReadMode {
        META,
        STREAM
    }
}
