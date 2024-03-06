package com.preachooda.academyapp.section.application.network

import com.preachooda.domain.model.AcademyApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApplicationService {
    @GET("academy-tickets/{applicationId}")
    suspend fun getApplication(@Path("applicationId") applicationId: Long): Response<AcademyApplication>

    @POST("academy-tickets")
    suspend fun postApplication(@Body body: AcademyApplication): Response<AcademyApplication>
}
