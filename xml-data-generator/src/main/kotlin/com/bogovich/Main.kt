package com.bogovich

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
//    println("abc")
    generateAccountXsd2()
//    generateDefinitionForAccountXsd()
}

fun generateAccountXsd2() {
    val accountXsdPath: Path = Paths.get("C:/staff/accountV2.xsd")
    println("create ${accountXsdPath.toAbsolutePath()}")
    if (!accountXsdPath.toFile().exists()) {
        accountXsdPath.toFile().createNewFile()
    }
    val accountXsdWriter = DslXMLStreamWriter(accountXsdPath)

    accountXsdWriter.document {
        "schema" {
            println("start schema")
            namespace(listOf(NamespaceEnum.XSI, NamespaceEnum.XLINK, NamespaceEnum.LINK, NamespaceEnum.XBRLI, NamespaceEnum.MODEL, NamespaceEnum.NONNUM))
            defaultNamespace(NamespaceEnum.XSD)
            targetNamespace(NamespaceEnum.XSD)
            import(listOf(NamespaceEnum.XBRLI, NamespaceEnum.MODEL, NamespaceEnum.NONNUM))

            val incomingBalance = xsdElement("IncomingBalance") {
                periodType(XbrlPeriodAttr.INSTANT); type(XbrlPeriodType.DOMAIN_ITEM_TYPE); substitutionGroup(XbrlSubstitutionGroup.ITEM); isNillable(); isAbstract()
            }

            val outgoingBalance = xsdElement("OutgoingBalance") {
                periodType(XbrlPeriodAttr.INSTANT); type(XbrlPeriodType.DOMAIN_ITEM_TYPE); substitutionGroup(XbrlSubstitutionGroup.ITEM); isNillable(); isAbstract()
            }

            val revenueDebit = xsdElement("RevenueDebit") {
                periodType(XbrlPeriodAttr.INSTANT); type(XbrlPeriodType.DOMAIN_ITEM_TYPE); substitutionGroup(XbrlSubstitutionGroup.ITEM); isNillable(); isAbstract()
            }

            val revenueCredit = xsdElement("RevenueCredit") {
                periodType(XbrlPeriodAttr.INSTANT); type(XbrlPeriodType.DOMAIN_ITEM_TYPE); substitutionGroup(XbrlSubstitutionGroup.ITEM); isNillable(); isAbstract()
            }
        }
    }
}