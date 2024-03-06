package com.preachooda.adminapp.section.helpTickets.network

import com.preachooda.domain.model.Ticket
import retrofit2.Response
import retrofit2.http.GET

interface HelpTicketsService {
    @GET("tickets")
    suspend fun getTickets(): Response<List<Ticket>>
}
