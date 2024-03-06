package com.preachooda.adminapp.section.qualificationExamTickets.network

import com.preachooda.domain.model.QualificationExamApplication
import retrofit2.Response
import retrofit2.http.GET

interface QualificationExamTicketsService {
    @GET("qualification-exams")
    suspend fun getQualificationExamTickets(): Response<List<QualificationExamApplication>>
}
