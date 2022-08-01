package com.bismark.searchevents.ui

import androidx.lifecycle.ViewModel
import com.bismark.searchevents.data.SearchEventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform

class SearchEventViewModel(
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val repository: SearchEventRepository
) : ViewModel() {

    val state: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.NoError)

    fun getAllEvents(): Flow<List<SearchEventState>> {
        return repository.getAllEventCategories()
            .transform { categories ->
                val searchStateList = mutableListOf<SearchEventState>()
                categories.forEach { category ->
                    if (category.events.isNotEmpty())
                        searchStateList += SearchEventState.SearchHeader(id = category.id, name = category.name)
                    category.events.forEach { event ->
                        searchStateList += SearchEventState.SearchItem(
                            id = event.id,
                            city = event.city,
                            price = event.price,
                            distance = event.distance,
                            venue = event.venueName,
                            date = event.date
                        )
                    }
                }
                emit(searchStateList)

            }.flowOn(dispatcher)
            .catch { error ->
                state.emit(SearchUIState.Error(error))
            }

    }

    fun searchButtonClick(search: String, mode: SearchMode): Flow<List<SearchEventState>> {
        return repository.searchEvents(search, mode)
            .transform { categories ->
                val searchStateList = mutableListOf<SearchEventState>()
                categories.forEach { category ->
                    if (category.events.isNotEmpty())
                        searchStateList += SearchEventState.SearchHeader(id = category.id, name = category.name)
                    category.events.forEach { event ->
                        searchStateList += SearchEventState.SearchItem(
                            id = event.id,
                            city = event.city,
                            price = event.price,
                            distance = event.distance,
                            venue = event.venueName,
                            date = event.date
                        )
                    }
                }
                emit(searchStateList)

            }.flowOn(dispatcher)
            .catch { error ->
                state.emit(SearchUIState.Error(error))
            }

    }

}
