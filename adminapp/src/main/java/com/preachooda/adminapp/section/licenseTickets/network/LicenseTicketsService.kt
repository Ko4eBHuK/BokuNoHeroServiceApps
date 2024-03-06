package com.preachooda.adminapp.section.licenseTickets.network

import com.preachooda.domain.model.LicenseApplication
import retrofit2.Response
import retrofit2.http.GET

interface LicenseTicketsService {
    @GET("license-tickets")
    suspend fun getLicenseApplications(): Response<List<LicenseApplication>>
}
