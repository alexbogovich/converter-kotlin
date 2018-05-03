package com.bogovich.excel

import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.util.zip.GZIPOutputStream

const val BALANCE_START  = 6
const val BALANCE_END = 1393
const val TRUST_ACTIVE_START  = 1403
const val TRUST_ACTIVE_END = 1422
const val TRUST_PASSIVE_START  = 1422
const val TRUST_PASSIVE_END = 1430
const val UNBALANCE_START  = 1440
const val UNBALANCE_END = 1539
const val OTHER_START  = 1552
const val OTHER_END = 1706




data class Account(val group: Group, val number: String, val type: Type, val title: String) {
    enum class Type {
        ACTIVE,
        PASSIVE,
        UNKNOWN;
        companion object {
            fun of(shortName: String): Type {
                if (shortName.toLowerCase() == "п") return PASSIVE
//            check cyrillic and english
                if (shortName.toLowerCase() == "a" || shortName.toLowerCase() == "а") return ACTIVE
                return UNKNOWN
            }
        }
    }

    enum class Group {
        BALANCE,
        TRUST,
        OFFBALANCE,
        OTHER
    }
}

fun main(args: Array<String>) = runBlocking {

    val converter = Converter()

    val job = launch {
        converter.reader.readMainDoc("C:\\Users\\Bogovich\\Downloads\\accouns.xlsx")
        converter.closeChannels()
    }

    try {
        parseAccounts(converter)
    } catch (e: Exception) {

    } finally {
        job.cancelAndJoin()
    }


}

suspend fun parseAccounts(converter: Converter) {

    val accounts: MutableList<Account> = mutableListOf()

//    println("===== A ======")

    converter.streamWithountMeta(
            { row -> row.rowNum >= BALANCE_START && row.sheetNum == 1 },
            { row -> row.rowNum >= BALANCE_END && row.sheetNum == 1 }) {
        if (cell("A").isEmpty() && !cell("B").isEmpty()) {
            accounts.add(Account(group = Account.Group.BALANCE,
                    number = cell("B"),
                    type = Account.Type.of(cell("D")),
                    title = cell("C")))
        }
    }


//    println("===== Б active ======")

    converter.streamWithountMeta(
            { row -> row.rowNum >= TRUST_ACTIVE_START && row.sheetNum == 1 },
            { row -> row.rowNum >= TRUST_ACTIVE_END && row.sheetNum == 1 }) {
        if (!cell("A").isEmpty() && cell("A").length == 5) {
            accounts.add(Account(group = Account.Group.TRUST,
                    number = cell("A"),
                    type = Account.Type.of("А"),
                    title = cell("B")))
        }
    }

//    println("===== Б passive ======")

    converter.streamWithountMeta(
            { row -> row.rowNum >= TRUST_PASSIVE_START && row.sheetNum == 1 },
            { row -> row.rowNum >= TRUST_PASSIVE_END && row.sheetNum == 1 }) {
        if (!cell("A").isEmpty() && cell("A").length == 5) {
            accounts.add(Account(group = Account.Group.TRUST,
                    number = cell("A"),
                    type = Account.Type.of("П"),
                    title = cell("B")))
        }
    }


//    println("===== В ======")

    converter.streamWithountMeta(
            { row -> row.rowNum >= UNBALANCE_START && row.sheetNum == 1 },
            { row -> row.rowNum >= UNBALANCE_END && row.sheetNum == 1 }) {
        if (!cell("A").isEmpty() && cell("A").length == 5) {
            accounts.add(Account(group = Account.Group.OFFBALANCE,
                    number = cell("A"),
                    type = Account.Type.of(cell("C")),
                    title = cell("B")))
        }
    }

//    println("===== Г ======")

    converter.streamWithountMeta(
            { row -> row.rowNum >= OTHER_START && row.sheetNum == 1 },
            { row -> row.rowNum >= OTHER_END && row.sheetNum == 1 }) {
        if (!cell("A").isEmpty() && cell("A").length == 5) {
            accounts.add(Account(group = Account.Group.OTHER,
                    number = cell("A"),
                    type = Account.Type.of(cell("C")),
                    title = cell("B")))
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter("C:\\staff\\Accounts.json").use {
            gson.toJson(accounts, it)
        }
    }
}
