package com.bismark.searchevents.data

data class EventCategory(
    val id: Int,
    val name: String,
    val events: List<Event>
)
