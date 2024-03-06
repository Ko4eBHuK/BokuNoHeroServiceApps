package com.preachooda.academyapp.section.qualificationExam.network

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.QualificationExamApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QualificationExamService {
    @GET("heroes")
    suspend fun getHeroes(): Response<List<Hero>>

    @POST("qualification-exams")
    suspend fun sendQualificationExamApplication(
        @Body body: QualificationExamApplication
    ): Response<QualificationExamApplication>
}
