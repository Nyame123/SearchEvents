package com.bismark.searchevents.data

data class Event(
    val id: Int,
    val name: String,
    val venueName: String,
    val city: String,
    val price: Double,
    val distance: Double,
    val date: String
)
