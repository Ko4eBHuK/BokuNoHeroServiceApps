package com.preachooda.bokunoheroservice.section.academyApplication.network

import com.preachooda.domain.model.Academy
import com.preachooda.domain.model.AcademyApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AcademyApplicationService {

    @GET("academies")
    suspend fun getAcademies(): Response<List<Academy>>

    @POST("academy-tickets")
    suspend fun sendApplication(@Body body: AcademyApplication): Response<AcademyApplication>
}
