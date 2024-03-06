package com.preachooda.academyapp.section.applications.network

import com.preachooda.domain.model.AcademyApplication
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApplicationsService {
    @GET("academy-tickets/academy/{academyId}")
    suspend fun getApplications(
        @Path("academyId") academyId: Long
    ): Response<List<AcademyApplication>>
}
