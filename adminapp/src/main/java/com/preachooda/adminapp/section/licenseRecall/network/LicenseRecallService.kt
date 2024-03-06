package com.preachooda.adminapp.section.licenseRecall.network

import com.preachooda.domain.model.Hero
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LicenseRecallService {
    @GET("heroes")
    suspend fun getHeroes(): Response<List<Hero>>

    @POST("license-recall")
    suspend fun recallLicense(@Body hero: Hero): Response<Unit>
}
