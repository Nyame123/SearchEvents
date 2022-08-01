package com.bismark.searchevents.data

import com.bismark.searchevents.ui.SearchMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

interface SearchEventRepository {

    fun getAllEventCategories(): Flow<List<EventCategory>>

    fun searchEvents(search: String, mode: SearchMode): Flow<List<EventCategory>>
}

class SearchEventRepositoryImpl(
    private val eventJsonDataSource: EventJsonDataSource
) : SearchEventRepository {

    override fun getAllEventCategories(): Flow<List<EventCategory>> {
        return eventJsonDataSource.getAllEventsFromJson()
    }

    override fun searchEvents(search: String, mode: SearchMode): Flow<List<EventCategory>> {
        return eventJsonDataSource.getAllEventsFromJson()
            .transform { categories ->
                val category = mutableListOf<EventCategory>()
                categories.forEach { eventCategory ->
                    val filter = eventCategory.events.filter { filterPredicate(it, search, mode) }
                    category += eventCategory.copy(events = filter)
                }
                emit(category)
            }
    }

    private fun filterPredicate(event: Event, search: String, mode: SearchMode): Boolean {
        return when (mode) {
            SearchMode.CITY -> {
                val regex: Regex = search.lowercase().trim().toRegex()
                event.city.lowercase().contains(regex)
            }

            SearchMode.PRICE -> {
                event.price >= search.toDouble()
            }

            SearchMode.AND -> {
                val separateSearch = search.split(" ")
                combineSearch(separateSearch, event) == separateSearch.size
            }

            SearchMode.OR -> {
                val separateSearch = search.split(" ")
                combineSearch(separateSearch, event) > 0
            }
        }
    }

    private fun combineSearch(separateSearch: List<String>, event: Event): Int {
        val numberRegex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
        var matches = 0;
        separateSearch.forEach { word ->
            if (word.matches(numberRegex) && event.price <= word.toDouble()) {
                matches++
            } else {
                val regex: Regex = word.lowercase().trim().toRegex()
                if (event.city.lowercase().contains(regex)) {
                    matches++;
                }
            }
        }
        return matches
    }
}
