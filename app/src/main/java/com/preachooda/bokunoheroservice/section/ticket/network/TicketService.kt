package com.preachooda.bokunoheroservice.section.ticket.network

import com.preachooda.domain.model.Ticket
import com.preachooda.domain.model.TicketMediaCodes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TicketService {
    @GET("tickets/{ticketId}")
    suspend fun getTicket(@Path("ticketId") ticketId: Long): Response<Ticket>

    @DELETE("tickets/{ticketId}")
    suspend fun deleteTicket(@Path("ticketId") ticketId: Long): Response<Any?>

    @GET("download-media/ticket/{ticketId}")
    suspend fun getTicketMedia(@Path("ticketId") ticketId: Long): Response<TicketMediaCodes>

    @PUT("tickets/{ticketId}")
    suspend fun saveTicket(
        @Path("ticketId") ticketId: Long,
        @Body ticket: Ticket
    ): Response<Ticket>
}
