package com.bogovich

import com.bogovich.utils.InsuredPersonUtils
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import java.time.LocalDate
import java.util.*
import javax.xml.stream.XMLOutputFactory


const val UZR = "http://пф.рф/ВсВО/НПФ/УЗР/2017-08-09"
const val I_NPF = "http://пф.рф/ВсВО/НПФ/типыВходящие/2017-08-09"
const val NPF = "http://пф.рф/ВсВО/НПФ/типы/2017-08-09"
const val UT = "http://пф.рф/унифицированныеТипы/2014-01-01"
const val AF = "http://пф.рф/АФ"

val guid = UUID.randomUUID().toString()
val localDate = LocalDate.now().toString()

fun main(args: Array<String>) {
    val out = System.out
    val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))
//    val file = File("h:\\ПФР_777000_УЗР_${localDate}_${guid}.xml.gz")
//    val fileOutputStream: OutputStream = GZIPOutputStream(FileOutputStream(file))
//    fileOutputStream.use {
//        val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(it, "UTF-8"))
    serialize04(writer)
//    }
}

private fun serialize04(writer: DslXMLStreamWriter) {
    writer.document {
        element("ЭДПФР") {
            defaultNamespace(UZR)
            namespace("ВНПФ", I_NPF)
            namespace("НПФ", NPF)
            namespace("УТ", UT)
            namespace("АФ", AF)

            element("УЗР") {
                element("НПФ") {
                    "Наименование" tag "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД ПЛЕС"
                    element("НаименованиеФормализованное", "НПФ_ПЛЕС")
                    element("ИНН", "7881020165")
                    element("ОГРН", "1033628801099")
                    "Лицензия" tag {
                        UT to "Дата" tag "2010-08-08"
                        UT to "Номер" tag 2308
                    }
                    element("Адрес") {
                        element(UT, "Индекс", "127001")
                        element(UT, "РоссийскийАдрес") {
                            element(UT, "Город") {
                                element(UT, "Название", "МОСКВА")
                                element(UT, "Сокращение", "г")
                            }
                            element(UT, "Улица") {
                                element(UT, "Название", "ЛЕСКОВА")
                                element(UT, "Сокращение", "ул")
                            }
                            element(UT, "Дом") {
                                element(UT, "Номер", "2")
                            }
                        }
                    }
                }
                element("Реорганизация") {
                    element("РешениеБанка") {
                        element(UT, "Дата", "2010-10-10")
                        element(UT, "Номер", "1010")
                    }
                    element("Форма", "1")
                    element("Результат") {
                        element("Созданные") {
                            element("НПФ") {
                                element("Наименование", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД ПЛЕС")
                                element("ИНН", "7881020165")
                                element("ОГРН", "1033628801099")
                                element("Адрес") {
                                    element(UT, "Индекс", "127001")
                                    element(UT, "РоссийскийАдрес") {
                                        element(UT, "Город") {
                                            element(UT, "Название", "МОСКВА")
                                            element(UT, "Сокращение", "г")
                                        }
                                        element(UT, "Улица") {
                                            element(UT, "Название", "ЛЕСКОВА")
                                            element(UT, "Сокращение", "ул")
                                        }
                                        element(UT, "Дом") {
                                            element(UT, "Номер", "2")
                                        }
                                    }
                                }
                            }
                        }
                        element("ПрекратившиеДеятельность") {
                            //1
                            element("НПФ") {
                                element("Наименование", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД ВОЛОГДА")
                                element("ИНН", "7886021717")
                                element("ОГРН", "1033628802299")
                                element("Адрес") {
                                    element(UT, "Индекс", "127001")
                                    element(UT, "РоссийскийАдрес") {
                                        element(UT, "Город") {
                                            element(UT, "Название", "КОСТРОМА")
                                            element(UT, "Сокращение", "г")
                                        }
                                        element(UT, "Улица") {
                                            element(UT, "Название", "ЛЕНИНА")
                                            element(UT, "Сокращение", "ул")
                                        }
                                        element(UT, "Дом") {
                                            element(UT, "Номер", "2")
                                        }
                                    }
                                }
                            }
                            //2
                            element("НПФ") {
                                element("Наименование", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД СУЗДАЛЬ")
                                element("ИНН", "7884038318")
                                element("ОГРН", "1033628803399")
                                element("Адрес") {
                                    element(UT, "Индекс", "")
                                    element(UT, "РоссийскийАдрес") {
                                        element(UT, "Город") {
                                            element(UT, "Название", "РОСТОВ")
                                            element(UT, "Сокращение", "г")
                                        }
                                        element(UT, "Улица") {
                                            element(UT, "Название", "ФРУНЗЕ")
                                            element(UT, "Сокращение", "ул")
                                        }
                                        element(UT, "Дом") {
                                            element(UT, "Номер", "2")
                                        }
                                    }
                                }
                            }
                            //3
                            element("НПФ") {
                                element("Наименование", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД КОЛОМНА")
                                element("ИНН", "7880032954")
                                element("ОГРН", "1033628803399")
                                element("Адрес") {
                                    element(UT, "Индекс", "")
                                    //writeEmptyElement(UT, "Индекс")
                                    element(UT, "РоссийскийАдрес") {
                                        element(UT, "Город") {
                                            element(UT, "Название", "РОСТОВ")
                                            element(UT, "Сокращение", "г")
                                        }
                                        element(UT, "Улица") {
                                            element(UT, "Название", "ФРУНЗЕ")
                                            element(UT, "Сокращение", "ул")
                                        }
                                        element(UT, "Дом") {
                                            element(UT, "Номер", "22")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                var totalZlCount: Long = 0
                element("СписокСведений") {
                    element("РеорганизованныйНПФ") {
                        var zlCount: Long = 0
                        for (i in 1..9) {
                            println("Write $i")
                            element("Запись") {
                                element("НомерПП", i.toString())
                                element("НПФ") {
                                    element("Наименование", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД ПЛЕС")
                                    element("ОГРН", "1033628801099")
                                    element("Лицензия") {
                                        element(UT, "Дата", "2010-08-08")
                                        element(UT, "Номер", "2308")
                                    }
                                }
                                element("ЗЛ") {
                                    element(UT, "ФИО") {
                                        element(UT, "Фамилия", "ГОЛЬФ")
                                        element(UT, "Имя", "ЕКАТЕРИНА")
                                        element(UT, "Отчество", "АЛЬФРЕДОВНА")
                                    }
                                    element(UT, "ДатаРождения", "1974-05-07")
                                    element(UT, "Пол", "Ж")
                                    val s: Long = 81_000_000L + i - 1
                                    zlCount++
                                    element(UT, "СтраховойНомер", InsuredPersonUtils.format(s) + " " + getControlNumber(s))
                                }
                                element("СПН") {
                                    element(NPF, "Сумма", "200222")
                                    element(NPF, "ИД", "1500")
                                }
                                element("Выплаты", "250000")
                            }
                        }
                        totalZlCount += zlCount
                        element("КоличествоЗЛ", zlCount.toString())
                    }
                }
                element("КоличествоЗЛ", totalZlCount.toString())
                element("ЕиоНПФ") {
                    element(UT, "ФИО") {
                        element(UT, "Фамилия", "Сидоров")
                        element(UT, "Имя", "Николай")
                        element(UT, "Отчество", "Владимирович")
                    }
                    element(UT, "Должность", "Генеральный директор")
                }
            }
            element("СлужебнаяИнформация") {
                //TODO: generate
                element(AF, "GUID", guid)
                element(AF, "ДатаВремя", "2017-08-09T12:00:00-05:00")
                element(NPF, "Составитель") {
                    element(UT, "НалоговыйНомер") {
                        element(UT, "ИНН", "7881020165")
                        element(UT, "КПП", "770201001")
                    }
                    element(UT, "КодЕГРИП", "0")
                    element(UT, "Форма", "normalizedString")
                    element(UT, "НаименованиеОрганизации", "НЕГОСУДАРСТВЕННЫЙ ПЕНСИОННЫЙ ФОНД ПЛЕС")
                    element(UT, "НаименованиеКраткое", "НПФ_ПЛЕС")
                    element(UT, "РегистрационныйНомер", "000-000-000000")
                    element(UT, "Адрес") {
                        element(UT, "Индекс", "127001")
                        element(UT, "РоссийскийАдрес") {
                            element(UT, "Город") {
                                element(UT, "Название", "МОСКВА")
                                element(UT, "Сокращение", "г")
                            }
                            element(UT, "Улица") {
                                element(UT, "Название", "ЛЕСКОВА")
                                element(UT, "Сокращение", "ул")
                            }
                            element(UT, "Дом") {
                                element(UT, "Номер", "2")
                            }
                        }
                    }
                    element(UT, "Подразделение") {
                        element(UT, "НаименованиеПодразделения", "НОВОЕ")
                        element(UT, "НомерПодразделения", "111")
                    }
                }
                element(NPF, "НомерДокументаОрганизации", "333")
                element(NPF, "ЗаГод", "2017")
                element(NPF, "ТипПериода", "String")
                element(NPF, "НомерПериода", "0")
            }
        }
    }
    writer.flush()
}

fun getControlNumber(s: Long): String {
    if (s <= 82_000_000L) return "71"
    if (s <= 85_000_000L) return "79"
    if (s < 90_000_000L) return "00"
    return "00"
}