package com.bogovich

import com.bogovich.model.Account
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileReader
import java.io.FileWriter
import javax.xml.stream.XMLOutputFactory


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


            "link:definitionLink" {
                "xlink:type" attr "extended"
                "xlink:role" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/balanceAccounts"
                "id" attr "balanceAccounts"
                "link:loc" emptyElement {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_BalanceAccountDomain"
                    "xlink:label" attr "BalanceAccountDomain"
                }

                "link:loc" emptyElement {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_ActiveBalanceAccountDomain"
                    "xlink:label" attr "ActiveBalanceAccountDomain"
                }
                "link:loc" emptyElement  {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_PassiveBalanceAccountDomain"
                    "xlink:label" attr "PassiveBalanceAccountDomain"
                }

                "link:definitionArc" emptyElement {
                    "xlink:type" attr "arc"
                    "xbrldt:targetRole" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts"
                    "xlink:arcrole" attr "http://xbrl.org/int/dim/arcrole/domain-member"
                    "xlink:from" attr "BalanceAccountDomain"
                    "xlink:to" attr "ActiveBalanceAccountDomain"
                    "order" attr "1.0"
                }

                "link:definitionArc" emptyElement {
                    "xlink:type" attr "arc"
                    "xbrldt:targetRole" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveBalanceAccounts"
                    "xlink:arcrole" attr "http://xbrl.org/int/dim/arcrole/domain-member"
                    "xlink:from" attr "BalanceAccountDomain"
                    "xlink:to" attr "PassiveBalanceAccountDomain"
                    "order" attr "2.0"
                }
            }

            "link:definitionLink" {
                "xlink:type" attr "extended"
                "xlink:role" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/balanceAccounts"
                "id" attr "balanceAccounts"
                "link:loc" emptyElement {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_BalanceAccountDomain"
                    "xlink:label" attr "BalanceAccountDomain"
                }

                "link:loc" emptyElement {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_ActiveBalanceAccountDomain"
                    "xlink:label" attr "ActiveBalanceAccountDomain"
                }
                "link:loc" emptyElement  {
                    "xlink:type" attr "locator"
                    "xlink:href" attr "account.xsd#account-list_PassiveBalanceAccountDomain"
                    "xlink:label" attr "PassiveBalanceAccountDomain"
                }

                "link:definitionArc" emptyElement {
                    "xlink:type" attr "arc"
                    "xbrldt:targetRole" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts"
                    "xlink:arcrole" attr "http://xbrl.org/int/dim/arcrole/domain-member"
                    "xlink:from" attr "BalanceAccountDomain"
                    "xlink:to" attr "ActiveBalanceAccountDomain"
                    "order" attr "1.0"
                }

                "link:definitionArc" emptyElement {
                    "xlink:type" attr "arc"
                    "xbrldt:targetRole" attr "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveBalanceAccounts"
                    "xlink:arcrole" attr "http://xbrl.org/int/dim/arcrole/domain-member"
                    "xlink:from" attr "BalanceAccountDomain"
                    "xlink:to" attr "PassiveBalanceAccountDomain"
                    "order" attr "2.0"
                }
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