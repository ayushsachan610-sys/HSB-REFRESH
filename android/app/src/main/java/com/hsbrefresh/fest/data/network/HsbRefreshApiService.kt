package com.hsbrefresh.fest.data.network

import com.hsbrefresh.fest.data.model.CreateEventRequest
import com.hsbrefresh.fest.data.model.Event
import com.hsbrefresh.fest.data.model.Registration
import com.hsbrefresh.fest.data.model.RegistrationRequest
import com.hsbrefresh.fest.data.model.RegistrationResponse
import com.hsbrefresh.fest.data.model.StatsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HsbRefreshApiService {

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Event>>

    @GET("api/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: Int
    ): Response<Event>

    @POST("api/events/{event_id}/register")
    suspend fun registerForEvent(
        @Path("event_id") eventId: Int,
        @Body body: RegistrationRequest
    ): Response<RegistrationResponse>

    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): Response<List<Registration>>

    @GET("api/stats")
    suspend fun getStats(): Response<StatsResponse>

    // Stretch Goals
    @POST("api/events")
    suspend fun createEvent(
        @Body body: CreateEventRequest
    ): Response<Event>

    @DELETE("api/registrations/{registration_id}")
    suspend fun cancelRegistration(
        @Path("registration_id") registrationId: Int
    ): Response<Map<String, Any>>
}
