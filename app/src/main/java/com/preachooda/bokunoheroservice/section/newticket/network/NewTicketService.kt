package com.preachooda.bokunoheroservice.section.newticket.network

import com.preachooda.domain.model.Ticket
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface NewTicketService {
    @POST("tickets")
    suspend fun sendNewTicket(@Body ticket: Ticket): Response<Ticket>

    @Multipart
    @POST("upload-media/ticket/{ticketId}")
    suspend fun uploadFiles(
        @Path("ticketId") ticketId: Long,
        @Part files: MutableList<MultipartBody.Part>
    ): Response<Unit>
}
