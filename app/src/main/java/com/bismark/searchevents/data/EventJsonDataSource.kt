package com.bismark.searchevents.data

import android.content.Context
import com.bismark.searchevents.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject

interface EventJsonDataSource {

    fun getAllEventsFromJson(): Flow<List<EventCategory>>
}

class EventJsonDataSourceImp(private val context: Context) : EventJsonDataSource {

    override fun getAllEventsFromJson(): Flow<List<EventCategory>> = flow {
        val events = context.resources.openRawResource(R.raw.events)
            .bufferedReader().use {
                it.readText()
            }

        val jsonArray = JSONArray(events)
        val len = jsonArray.length()
        val eventCategories = mutableListOf<EventCategory>()
        for (jsonIndex in 0 until len) {
            val jsonObject = jsonArray.getJSONObject(jsonIndex)
            parseJsonRecursively(jsonObject,eventCategories)
        }

        emit(eventCategories)
    }

    private fun parseJsonRecursively(jsonObject: JSONObject, eventCategoryList: MutableList<EventCategory>) {
        val eventJsonArray = jsonObject.getJSONArray("events")
        val events = mutableListOf<Event>()
        for (eventIndex in 0 until eventJsonArray.length()) {
            val eventJsonObject = eventJsonArray.getJSONObject(eventIndex)
            val event = Event(
                id = eventJsonObject.optInt("id"),
                name = eventJsonObject.optString("name"),
                venueName = eventJsonObject.optString("venueName"),
                city = eventJsonObject.optString("city"),
                price = eventJsonObject.optDouble("price"),
                distance = eventJsonObject.optDouble("distanceFromVenue"),
                date = eventJsonObject.optString("date")
            )

            events += event
        }
        eventCategoryList += EventCategory(
            id = jsonObject.optInt("id"),
            name = jsonObject.optString("name"),
            events = events
        )

        for (child in 0 until jsonObject.getJSONArray("children").length()) {
            parseJsonRecursively(
                jsonObject.getJSONArray("children").getJSONObject(child), eventCategoryList
            )
        }
    }

}
