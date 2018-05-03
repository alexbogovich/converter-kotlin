package com.bogovich.model

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