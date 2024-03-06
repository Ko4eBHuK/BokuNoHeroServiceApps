package com.preachooda.adminapp.section.qualificationExamTicket.network

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.QualificationExamApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface QualificationExamTicketService {
    @GET("qualification-exams/{ticketId}")
    suspend fun getQualificationExamApplication(
        @Path("ticketId") ticketId: Long
    ): Response<QualificationExamApplication>

    @GET("heroes")
    suspend fun getHeroes(): Response<List<Hero>>

    @PUT("qualification-exams/{ticketId}")
    suspend fun sendQualificationExamApplication(
        @Path("ticketId") ticketId: Long,
        @Body body: QualificationExamApplication
    ): Response<QualificationExamApplication>
}
