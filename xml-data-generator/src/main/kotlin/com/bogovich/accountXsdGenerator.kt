package com.bogovich

import com.bogovich.ArcRole.*
import com.bogovich.model.Account
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileReader
import javax.xml.stream.XMLOutputFactory


enum class ArcRole(val url: String) {
    DOMAIN_MEMBER("http://xbrl.org/int/dim/arcrole/domain-member"),
    DIMENSION_DOMAIN("http://xbrl.org/int/dim/arcrole/dimension-domain")
}

data class Location(val href: String, val label: String)
data class DefinitionArc(val arcrole: ArcRole, val from: String, val to: String, val order: String = "", val targetRole: String = "")
data class DefinitionLink(val role: String, val id: String)

fun DslXMLStreamWriter.location(href: String, label: String): Location {
    "link:location" emptyElement {
        "xlink:type" attr "locator"
        "xlink:href" attr href
        "xlink:label" attr label
    }
    return Location(href, label)
}

fun DslXMLStreamWriter.definitionArc(arcrole: ArcRole, from: String, to: String, order: String = "", targetRole:
String = ""): DefinitionArc {
    "link:definitionArc" emptyElement {
        "xlink:type" attr "arc"
        "xlink:arcrole" attr arcrole.url
        "xlink:from" attr from
        "xlink:to" attr to
        if (!order.isEmpty()) "order" attr order
        if (!targetRole.isEmpty()) "xbrldt:targetRole" attr targetRole
    }
    return DefinitionArc(arcrole, from, to, order, targetRole)
}

fun DslXMLStreamWriter.definitionArc(arcrole: ArcRole, from: Location, to: Location, order: String = "", targetRole:
String = ""): DefinitionArc {
    return this.definitionArc(arcrole, from.label, to.label, order, targetRole)
}

fun DslXMLStreamWriter.definitionLink(role: String, id: String, lambda: DslXMLStreamWriter.() -> Unit): DefinitionLink {
    "link:definitionLink" {
        "xlink:type" attr "extended"
        "xlink:role" attr role
        "id" attr id
        this.lambda()
    }
    return DefinitionLink(role, id)
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

    val allAccountsRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/allAccounts"

    val balanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/balanceAccounts"
    val activeBalanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts"
    val passiveBalanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveBalanceAccounts"

    val trustAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/trustAccounts"
    val activeTrustAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeTrustAccounts"
    val passiveTrustAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveTrustAccounts"

    val offBalanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/offBalanceAccounts"
    val activeOffBalanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeOffBalanceAccounts"
    val passiveOffBalanceAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveOffBalanceAccounts"

    val otherAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/otherAccounts"
    val activeOtherAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/activeOtherAccounts"
    val passiveOtherAccountRole = "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveOtherAccounts"

    writer.document {
        "data" {
            namespace("xsd", "http://www.w3.org/2001/XMLSchema")
            namespace("link", "http://www.xbrl.org/2003/linkbase")
            namespace("xbrldt", "http://xbrl.org/2005/xbrldt")
            namespace("xbrli", "http://www.xbrl.org/2003/instance")
            namespace("xlink", "http://www.w3.org/1999/xlink")

            definitionLink(allAccountsRole, "allAccountsLink") {
                val accountDimension = location("account.xsd#account-list_AccountDimension","AccountDimension")
                val balance = location("account.xsd#account-list_BalanceAccountDomain","BalanceAccountDomain")
                val trust = location("account.xsd#account-list_TrustAccountDomain", "TrustAccountDomain")
                val offBalance = location("account.xsd#account-list_OffBalanceAccountDomain",
                        "OffBalanceAccountDomain")
                val other = location("account.xsd#account-list_OtherAccountDomain", "OtherAccountDomain")

                definitionArc(DIMENSION_DOMAIN, accountDimension, balance, "1.0", balanceAccountRole)
                definitionArc(DIMENSION_DOMAIN, accountDimension, trust, "2.0", trustAccountRole)
                definitionArc(DIMENSION_DOMAIN, accountDimension, offBalance, "3.0", offBalanceAccountRole)
                definitionArc(DIMENSION_DOMAIN, accountDimension, other, "4.0", otherAccountRole)
            }

            definitionLink(balanceAccountRole, "balanceAccounts") {
                val balance = location("account.xsd#account-list_BalanceAccountDomain","BalanceAccountDomain")
                val active = location("account.xsd#account-list_ActiveBalanceAccountDomain","ActiveBalanceAccountDomain")
                val passive = location("account.xsd#account-list_PassiveBalanceAccountDomain", "PassiveBalanceAccountDomain")

                definitionArc(DOMAIN_MEMBER, balance, active, "1.0", activeBalanceAccountRole)
                definitionArc(DOMAIN_MEMBER, balance, passive, "2.0", passiveBalanceAccountRole)
            }

            definitionLink(trustAccountRole, "trustAccount") {
                val trust = location("account.xsd#account-list_TrustAccountDomain","TrustAccountDomain")
                val active = location("account.xsd#account-list_ActiveTrustAccountDomain","ActiveTrustAccountDomain")
                val passive = location("account.xsd#account-list_PassiveTrustAccountDomain", "PassiveTrustAccountDomain")

                definitionArc(DOMAIN_MEMBER, trust, active, "1.0", activeTrustAccountRole)
                definitionArc(DOMAIN_MEMBER, trust, passive, "2.0", passiveTrustAccountRole)
            }

            definitionLink(offBalanceAccountRole, "offBalanceAccounts") {
                val offBalance = location("account.xsd#account-list_OffBalanceAccountDomain","OffBalanceAccountDomain")
                val active = location("account.xsd#account-list_ActiveOffBalanceAccountDomain","ActiveOffBalanceAccountDomain")
                val passive = location("account.xsd#account-list_PassiveOffBalanceAccountDomain", "PassiveOffBalanceAccountDomain")

                definitionArc(DOMAIN_MEMBER, offBalance, active, "1.0", activeOffBalanceAccountRole)
                definitionArc(DOMAIN_MEMBER, offBalance, passive, "2.0", passiveOffBalanceAccountRole)
            }

            definitionLink(otherAccountRole, "otherAccounts") {
                val other = location("account.xsd#account-list_OtherAccountDomain","OtherAccountDomain")
                val active = location("account.xsd#account-list_ActiveOtherAccountDomain","ActiveOtherAccountDomain")
                val passive = location("account.xsd#account-list_PassiveOtherAccountDomain", "PassiveOtherAccountDomain")

                definitionArc(DOMAIN_MEMBER, other, active, "1.0", activeOtherAccountRole)
                definitionArc(DOMAIN_MEMBER, other, passive, "2.0", passiveOtherAccountRole)
            }

//            personList.forEach {
//                "xsd:element" {
//                    "name" attr "Account${it.number}"
//                    "id" attr "account-list_Account${it.number}"
//                    "type" attr "xbrli:stringItemType"
//                    "substitutionGroup" attr "xbrli:item"
//                    "nillable" attr "true"
//                    "xbrli:periodType" attr "instant"
//                }
//            }
        }
    }
}