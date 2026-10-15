package com.hsbrefresh.fest.data.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("event_date")
    val eventDate: String,

    @SerializedName("event_time")
    val eventTime: String,

    @SerializedName("venue")
    val venue: String,

    @SerializedName("capacity")
    val capacity: Int,

    @SerializedName("fee")
    val fee: Int = 0,

    @SerializedName("icon")
    val icon: String?,

    @SerializedName("available_seats")
    val availableSeats: Int
) {
    val registered: Int
        get() = (capacity - availableSeats).coerceAtLeast(0)

    val utilizationPct: Float
        get() = if (capacity > 0) {
            Math.round((registered.toFloat() / capacity.toFloat() * 100f) * 10f) / 10f
        } else {
            0f
        }

    val isFull: Boolean
        get() = availableSeats <= 0

    val isNearCapacity: Boolean
        get() = utilizationPct >= 90f
}
