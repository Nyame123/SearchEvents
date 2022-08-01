package com.bismark.searchevents.ui

sealed interface SearchEventState {
    data class SearchHeader(val id: Int,val name: String) : SearchEventState
    data class SearchItem(
        val id: Int,
        val city: String,
        val venue: String,
        val price: Double,
        val distance: Double,
        val date: String
    ) : SearchEventState
}

sealed interface SearchUIState {
    object NoError : SearchUIState
    data class Error(val error: Throwable): SearchUIState
}
