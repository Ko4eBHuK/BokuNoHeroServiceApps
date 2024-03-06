package com.preachooda.adminapp.section.licenseTicket.network

import com.preachooda.domain.model.LicenseApplication
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface LicenseTicketService {
    @GET("license-tickets/{ticketId}")
    suspend fun getLicenseApplication(@Path("ticketId") ticketId: Long): Response<LicenseApplication>

    @PUT("license-tickets/{ticketId}")
    suspend fun handleLicenseApplication(
        @Path("ticketId") ticketId: Long,
        @Body licenseApplication: LicenseApplication
    ): Response<LicenseApplication>
}
