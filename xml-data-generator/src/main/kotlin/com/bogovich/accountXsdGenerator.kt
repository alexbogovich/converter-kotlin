package com.bogovich

import com.bogovich.ArcRole.DIMENSION_DOMAIN
import com.bogovich.ArcRole.DOMAIN_MEMBER
import com.bogovich.model.Account
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileOutputStream
import java.io.FileReader
import javax.xml.stream.XMLOutputFactory


enum class ArcRole(val url: String) {
    DOMAIN_MEMBER("http://xbrl.org/int/dim/arcrole/domain-member"),
    DIMENSION_DOMAIN("http://xbrl.org/int/dim/arcrole/dimension-domain")
}

enum class ArcroleRef(val arcroleURI: String, val href: String) {
    ALL("http://xbrl.org/int/dim/arcrole/all",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#all"),
    NOT_ALL("http://xbrl.org/int/dim/arcrole/notAll",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#notAll"),
    DIMENSION_DEFAULT("http://xbrl.org/int/dim/arcrole/dimension-default",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#dimension-default"),
    DIMENSION_DOMAIN("http://xbrl.org/int/dim/arcrole/dimension-domain",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#dimension-domain"),
    DOMAIN_MEMBER("http://xbrl.org/int/dim/arcrole/domain-member",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#domain-member"),
    HYPERCUBE_DIMENSION("http://xbrl.org/int/dim/arcrole/hypercube-dimension",
            "http://www.xbrl.org/2005/xbrldt-2005.xsd#hypercube-dimension")
}

data class Location(val href: String, val label: String)
data class DefinitionArc(val arcrole: ArcRole, val from: String, val to: String, val order: String = "", val targetRole: String = "")
data class DefinitionLink(val role: String, val id: String)
data class RoleRef(val href: String, val roleURI: String) // use URI?

fun DslXMLStreamWriter.arcroleRef(arcroleURI: String, href: String): Unit {
    "link:arcroleRef" emptyElement  {
        "arcroleURI" attr arcroleURI
        "xlink:type" attr "simple"
        "xlink:href" attr href
    }
}

fun DslXMLStreamWriter.writeAllArcroleRef(): Unit {
    ArcroleRef.values().forEach { arcroleRef(it.arcroleURI, it.href) }
}

fun DslXMLStreamWriter.location(href: String, label: String): Location {
    "link:loc" emptyElement {
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
RoleRef = RoleRef("", "")): DefinitionArc {
    return this.definitionArc(arcrole, from.label, to.label, order, targetRole.roleURI)
}

fun DslXMLStreamWriter.definitionLink(role: RoleRef, id: String, lambda: DslXMLStreamWriter.() -> Unit): DefinitionLink {
    "link:definitionLink" {
        "xlink:type" attr "extended"
        "xlink:role" attr role.roleURI
        "id" attr id
        this.lambda()
    }
    return DefinitionLink(role.roleURI, id)
}

fun DslXMLStreamWriter.roleRef(href: String, roleURI: String): RoleRef {
    "link:roleRef" emptyElement  {
        "xlink:type" attr "simple"
        "xlink:href" attr href
        "roleURI" attr roleURI
    }
    return RoleRef(href, roleURI)
}

fun DslXMLStreamWriter.writeArrayOfAccounts(domain: Location, accounts: List<Account>, group: Account.Group, type: Account.Type) {
    accounts.asSequence()
            .filter { it.group == group && it.type == type }
            .map { location("account.xsd#account-list_Account${it.number}", "Account${it.number}") }
            .forEachIndexed { index, location ->
                definitionArc(DOMAIN_MEMBER, domain, location, "${index + 1}.0")
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

//    val out = System.out
    val out = FileOutputStream("C:\\staff\\account-definition.xml")
    val writer = DslXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8"))

    writer.document {
        "link:linkbase" {
            namespace("xsd", "http://www.w3.org/2001/XMLSchema")
            namespace("link", "http://www.xbrl.org/2003/linkbase")
            namespace("xbrldt", "http://xbrl.org/2005/xbrldt")
            namespace("xbrli", "http://www.xbrl.org/2003/instance")
            namespace("xlink", "http://www.w3.org/1999/xlink")

            writeAllArcroleRef()

            val allAccountsRole = roleRef("account.xsd#allAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/allAccounts")
            val balanceAccountRole = roleRef("account.xsd#balanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/balanceAccounts")
            val activeBalanceAccountRole = roleRef("account.xsd#activeBalanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/activeBalanceAccounts")
            val passiveBalanceAccountRole = roleRef("account.xsd#passiveBalanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveBalanceAccounts")

            val trustAccountRole = roleRef("account.xsd#trustAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/trustAccounts")
            val activeTrustAccountRole = roleRef("account.xsd#activeTrustAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/activeTrustAccounts")
            val passiveTrustAccountRole = roleRef("account.xsd#passiveTrustAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveTrustAccounts")

            val offBalanceAccountRole = roleRef("account.xsd#offBalanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/offBalanceAccounts")
            val activeOffBalanceAccountRole = roleRef("account.xsd#activeOffBalanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/activeOffBalanceAccounts")
            val passiveOffBalanceAccountRole = roleRef("account.xsd#passiveOffBalanceAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveOffBalanceAccounts")

            val otherAccountRole = roleRef("account.xsd#otherAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/otherAccounts")
            val activeOtherAccountRole = roleRef("account.xsd#activeOtherAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/activeOtherAccounts")
            val passiveOtherAccountRole = roleRef("account.xsd#passiveOtherAccounts",
                    "http://www.cbr-prototype.com/xbrl/fin/list/account/passiveOtherAccounts")

            definitionLink(allAccountsRole, "allAccountsLink") {
                val accountDimension = location("account.xsd#account-list_AccountDimension", "AccountDimension")
                val balance = location("account.xsd#account-list_BalanceAccountDomain", "BalanceAccountDomain")
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
                val balance = location("account.xsd#account-list_BalanceAccountDomain", "BalanceAccountDomain")
                val active = location("account.xsd#account-list_ActiveBalanceAccountDomain", "ActiveBalanceAccountDomain")
                val passive = location("account.xsd#account-list_PassiveBalanceAccountDomain", "PassiveBalanceAccountDomain")

                definitionArc(DOMAIN_MEMBER, balance, active, "1.0", activeBalanceAccountRole)
                definitionArc(DOMAIN_MEMBER, balance, passive, "2.0", passiveBalanceAccountRole)
            }

            definitionLink(trustAccountRole, "trustAccount") {
                val trust = location("account.xsd#account-list_TrustAccountDomain", "TrustAccountDomain")
                val active = location("account.xsd#account-list_ActiveTrustAccountDomain", "ActiveTrustAccountDomain")
                val passive = location("account.xsd#account-list_PassiveTrustAccountDomain", "PassiveTrustAccountDomain")

                definitionArc(DOMAIN_MEMBER, trust, active, "1.0", activeTrustAccountRole)
                definitionArc(DOMAIN_MEMBER, trust, passive, "2.0", passiveTrustAccountRole)
            }

            definitionLink(offBalanceAccountRole, "offBalanceAccounts") {
                val offBalance = location("account.xsd#account-list_OffBalanceAccountDomain", "OffBalanceAccountDomain")
                val active = location("account.xsd#account-list_ActiveOffBalanceAccountDomain", "ActiveOffBalanceAccountDomain")
                val passive = location("account.xsd#account-list_PassiveOffBalanceAccountDomain", "PassiveOffBalanceAccountDomain")

                definitionArc(DOMAIN_MEMBER, offBalance, active, "1.0", activeOffBalanceAccountRole)
                definitionArc(DOMAIN_MEMBER, offBalance, passive, "2.0", passiveOffBalanceAccountRole)
            }

            definitionLink(otherAccountRole, "otherAccounts") {
                val other = location("account.xsd#account-list_OtherAccountDomain", "OtherAccountDomain")
                val active = location("account.xsd#account-list_ActiveOtherAccountDomain", "ActiveOtherAccountDomain")
                val passive = location("account.xsd#account-list_PassiveOtherAccountDomain", "PassiveOtherAccountDomain")

                definitionArc(DOMAIN_MEMBER, other, active, "1.0", activeOtherAccountRole)
                definitionArc(DOMAIN_MEMBER, other, passive, "2.0", passiveOtherAccountRole)
            }

            definitionLink(activeBalanceAccountRole, "activeBalanceAccounts") {
                val domain = location("account.xsd#account-list_ActiveBalanceAccountDomain", "ActiveBalanceAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.BALANCE, Account.Type.ACTIVE)
            }

            definitionLink(passiveBalanceAccountRole, "passiveBalanceAccounts") {
                val domain = location("account.xsd#account-list_PassiveBalanceAccountDomain", "PassiveBalanceAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.BALANCE, Account.Type.PASSIVE)
            }

            definitionLink(activeTrustAccountRole, "activeTrustAccounts") {
                val domain = location("account.xsd#account-list_ActiveTrustAccountDomain", "ActiveTrustAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.TRUST, Account.Type.ACTIVE)
            }

            definitionLink(passiveTrustAccountRole, "passiveTrustAccounts") {
                val domain = location("account.xsd#account-list_PassiveTrustAccountDomain", "PassiveTrustAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.TRUST, Account.Type.PASSIVE)
            }

            definitionLink(activeOffBalanceAccountRole, "ActiveOffBalanceAccountDomain") {
                val domain = location("account.xsd#account-list_ActiveOffBalanceAccountDomain", "ActiveOffBalanceAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.OFFBALANCE, Account.Type.ACTIVE)
            }

            definitionLink(passiveOffBalanceAccountRole, "passiveOffBalanceAccounts") {
                val domain = location("account.xsd#account-list_PassiveOffBalanceAccountDomain", "PassiveOffBalanceAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.OFFBALANCE, Account.Type.PASSIVE)
            }

            definitionLink(activeOtherAccountRole, "activeOtherAccounts") {
                val domain = location("account.xsd#account-list_ActiveOtherAccountDomain", "ActiveOtherAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.OTHER, Account.Type.ACTIVE)
            }

            definitionLink(passiveOtherAccountRole, "passiveOtherAccounts") {
                val domain = location("account.xsd#account-list_PassiveOtherAccountDomain", "PassiveOtherAccountDomain")
                writeArrayOfAccounts(domain, personList, Account.Group.OTHER, Account.Type.PASSIVE)
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