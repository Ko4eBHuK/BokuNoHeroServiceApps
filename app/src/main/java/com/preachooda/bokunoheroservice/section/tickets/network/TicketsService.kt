package com.preachooda.bokunoheroservice.section.tickets.network

import com.preachooda.domain.model.Ticket
import retrofit2.Response
import retrofit2.http.GET

interface TicketsService {
    @GET("tickets") // TODO: /user/{userId}
    suspend fun getTicketsList(/*@Path("userId") userId: Long*/): Response<List<Ticket>>
}