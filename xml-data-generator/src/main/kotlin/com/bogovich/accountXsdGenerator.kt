package com.bogovich

import com.bogovich.model.Account
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileReader
import javax.xml.stream.XMLOutputFactory


enum class ArcRole(val url: String) {
    DOMAIN_MEMBER("http://xbrl.org/int/dim/arcrole/domain-member")
}

fun DslXMLStreamWriter.location(href: String, label: String) {
    "link:location" emptyElement {
        "xlink:type" attr "locator"
        "xlink:href" attr href
        "xlink:label" attr label
    }
}

fun DslXMLStreamWriter.definitionArc(arcrole: ArcRole, from: String, to: String, order: String = "", targetRole: String = "") {
    "link:definitionArc" emptyElement {
        "xlink:type" attr "arc"
        "xlink:arcrole" attr arcrole.url
        "xlink:from" attr from
        "xlink:to" attr to
        if (!order.isEmpty()) "order" attr order
        if (!targetRole.isEmpty()) "xbrldt:targetRole" attr targetRole
    }
}

fun DslXMLStreamWriter.definitionLink(role: String, id: String, lambda: DslXMLStreamWriter.() -> Unit) {
    "link:definitionLink" {
        "xlink:type" attr "extended"
        "xlink:role" attr role
        "id" attr id
        this.lambda()
    }
}

fun main(args: Array<String>) {
    getAccountsXsdElements()
}

fun getAccountsXsdElements() {

    val gson = GsonBuilder().setPrettyPrinting().create()
    var personList: List<Account> = listOf()

    FileReader("C:\\staff\\Accounts.json").use {
        personList = gson.fromJson(it, object : TypeToken<List<Account>>() {}.type)
    }

//    println(personList)

    val out = System.out
    val writer = DslXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8"))

    writer.document {
        "data" {
            namespace("xsd", "http://www.w3.org/2001/XMLSchema")
            namespace("link", "http://www.xbrl.org/2003/linkbase")
            namespace("xbrldt", "http://xbrl.org/2005/xbrldt")
            namespace("xbrli", "http://www.xbrl.org/2003/instance")
            namespace("xlink", "http://www.w3.org/1999/xlink")

            definitionLink(role = "http://www.cbr-prototype.com/xbrl/fin/list/account/balanceAccounts",
                    id = "balanceAccounts") {
                location(href = "account.xsd#account-list_BalanceAccountDomain", label = "BalanceAccountDomain")
                location(href = "account.xsd#account-list_ActiveBalanceAccountDomain", label = "ActiveBalanceAccountDomain")
                location(href = "account.xsd#account-list_PassiveBalanceAccountDomain", label = "PassiveBalanceAccountDomain")

                definitionArc(arcrole = ArcRole.DOMAIN_MEMBER,
                        from = "BalanceAccountDomain",
                        to = "ActiveBalanceAccountDomain",
                        order = "1.0",
                        targetRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts")
                definitionArc(arcrole = ArcRole.DOMAIN_MEMBER,
                        from = "BalanceAccountDomain",
                        to = "PassiveBalanceAccountDomain",
                        order = "2.0",
                        targetRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts")
            }

            personList.forEach {
                "xsd:element" {
                    "name" attr "Account${it.number}"
                    "id" attr "account-list_Account${it.number}"
                    "type" attr "xbrli:stringItemType"
                    "substitutionGroup" attr "xbrli:item"
                    "nillable" attr "true"
                    "xbrli:periodType" attr "instant"
                }
            }
        }
    }
}