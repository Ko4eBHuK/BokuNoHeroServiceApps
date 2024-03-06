package com.preachooda.adminapp.section.helpTicket.network

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Ticket
import com.preachooda.domain.model.TicketMediaCodes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HelpTicketService {

    @GET("tickets/{ticketId}")
    suspend fun getTicket(@Path("ticketId") ticketId: Long): Response<Ticket>

    @GET("download-media/ticket/{ticketId}")
    suspend fun getTicketMedia(@Path("ticketId") ticketId: Long): Response<TicketMediaCodes>

    @GET("heroes/free")
    suspend fun getAvailableHeroes(): Response<List<Hero>>

    @POST("heroes/auto")
    suspend fun getSuitableHeroes(@Body ticket: Ticket): Response<List<Hero>>

    @PUT("tickets/{ticketId}")
    suspend fun handleTicket(
        @Path("ticketId") ticketId: Long,
        @Body ticket: Ticket
    ): Response<Ticket>
}
