package com.preachooda.academyapp.section.license.network

import com.preachooda.domain.model.LicenseApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LicenseService {
    @POST("license-tickets")
    suspend fun sendLicenseApplication(@Body application: LicenseApplication): Response<LicenseApplication>
}
