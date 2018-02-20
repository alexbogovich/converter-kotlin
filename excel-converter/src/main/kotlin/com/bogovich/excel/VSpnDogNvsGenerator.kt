package com.bogovich.excel

import com.bogovich.utils.InsuredPersonUtils
import com.bogovich.xml.writer.dsl.CoroutineXMLStreamWriter
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.xml.stream.XMLOutputFactory


data class Sum(var sum: BigDecimal = BigDecimal.ZERO, var id: BigDecimal = BigDecimal.ZERO)

fun main(args: Array<String>) = runBlocking {
    val converter = Converter()

    val job = launch {
        args.forEachIndexed { index, s ->
            when (index) {
                0 -> {
                    converter.reader.readMainDoc(s)
                }
                else -> {
                    converter.reader.readRestDocs(s, index + 1, args.size)
                }
            }
        }
        converter.closeChannels()
    }

    val out = System.out
    converter.writer = CoroutineXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))

    val total = object {
        var zlCount: Long = 0
        val transferSums = object {
            val sv = Sum()
            val dsv = Sum()
            val sofn = Sum()
            val msk = Sum()
            var total = BigDecimal.ZERO
        }
        var garanty = BigDecimal.ZERO
        var compensation = BigDecimal.ZERO
        var totalTransferred = BigDecimal.ZERO
    }

    converter.document {
        "ЭДПФР" {
            "РНПФ" {
                "Реквизиты" {
                    "Дата" tag converter.cellDate("B3")
                    "Номер" tag converter.cell("D3")
                }
                "НПФ" {
                    "НаименованиеФормализованное" tag converter.cell("D11")
                    "ИНН" tag converter.cell("D9")
                }
                "СписокСведений" {
                    converter.stream({ row -> row.rowNum >= 16 && row.sheetNum == 1 },
                            { row -> row.data["A"]?.stringCellValue.isNullOrEmpty() }) {
                        var rowTotalSpn = BigDecimal.ZERO
                        "Запись" {
                            "НомерПП" tag ++total.zlCount
                            "ЗЛ" {
                                "ФИО" {
                                    "Фамилия" tag cell("B")
                                    "Имя" tag cell("C")
                                    "Отчество" tag cell("D")
                                }
                                "Пол" tag cell("G")
                                "ДатаРождения" tag cellDate("E")
                                "МестоРождения" {
                                    "ТипМестаРождения" tag "СТАНДАРТНОЕ"
                                    "ГородРождения" tag cell("F")
                                    "СтранаРождения" tag "РФ"
                                }
                                "СтраховойНомер" tag InsuredPersonUtils.formattedNumber(cell("H").toLong())
                            }
                            "СуммыПереданные" {
                                "СВ" {
                                    "Сумма" tag cellMoney("J").also {
                                        total.transferSums.sv.sum += it
                                        rowTotalSpn += it
                                    }
                                    "ИД" tag cellMoney("K").also {
                                        total.transferSums.sv.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "ДСВ" {
                                    "Сумма" tag cellMoney("L").also {
                                        total.transferSums.dsv.sum += it
                                        rowTotalSpn += it
                                    }
                                    "ИД" tag cellMoney("M").also {
                                        total.transferSums.dsv.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "СОФН" {
                                    "Сумма" tag cellMoney("N").also {
                                        total.transferSums.sofn.sum += it
                                        rowTotalSpn += it
                                    }
                                    "ИД" tag cellMoney("O").also {
                                        total.transferSums.sofn.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "МСК" {
                                    "Сумма" tag cellMoney("P").also {
                                        total.transferSums.msk.sum += it
                                        rowTotalSpn += it
                                    }
                                    "ИД" tag cellMoney("Q").also {
                                        total.transferSums.msk.id += it
                                        rowTotalSpn += it
                                    }
                                }
                            }
                            "ВсегоСПН" tag rowTotalSpn.also { total.transferSums.total += it }
                        }
                        "ГарантийноеВосполнение" tag cellMoney("S").also {
                            total.garanty += it
                            rowTotalSpn += it
                        }
                        "Компенсация" tag cellMoney("T").also {
                            total.compensation += it
                            rowTotalSpn += it
                        }
                        "ВсегоПередано" tag rowTotalSpn.also { total.totalTransferred += it }
                    }
                }
                "Итого" {
                    "КоличествоЗЛ" tag total.zlCount
                    "СуммыПереданные" tag {
                        "СуммыПереданные" tag {
                            "СВ" {
                                "Сумма" tag total.transferSums.sv.sum
                                "ИД" tag total.transferSums.sv.id
                            }
                            "ДСВ" {
                                "Сумма" tag total.transferSums.dsv.sum
                                "ИД" tag total.transferSums.dsv.id
                            }
                            "СОФН" {
                                "Сумма" tag total.transferSums.sofn.sum
                                "ИД" tag total.transferSums.sofn.id
                            }
                            "МСК" {
                                "Сумма" tag total.transferSums.msk.sum
                                "ИД" tag total.transferSums.msk.id
                            }
                            "ВсегоСПН" tag total.transferSums.total
                        }
                    }
                    "ГарантийноеВосполнение" tag total.garanty
                    "Компенсация" tag total.compensation
                    "ВсегоПередано" tag total.totalTransferred
                }
                "СлужебнаяИнформация" {
                    "GUID" tag UUID.randomUUID()
                    "ДатаВремя" tag LocalDateTime.now()
                    "ЗаГод" tag LocalDateTime.now().year
                }
            }
        }
    }
    job.cancelAndJoin()
}