package com.preachooda.bokunoheroservice.section.home.network

import com.preachooda.domain.model.Ticket
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeService {
    @POST("tickets")
    suspend fun sendSosTicket(@Body ticket: Ticket): Response<Ticket>
}