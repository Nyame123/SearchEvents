package com.bismark.searchevents.ui

enum class SearchMode(val mode: String) {
    PRICE("Price"),
    CITY("City"),
    AND("And"),
    OR("Or")
    ;
    companion object{
        fun from(item: String): SearchMode {
            return values().first { it.mode.equals(item, ignoreCase = true) }
        }
    }

}
