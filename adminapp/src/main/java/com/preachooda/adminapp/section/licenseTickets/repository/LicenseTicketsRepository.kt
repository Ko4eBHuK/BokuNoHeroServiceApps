package com.preachooda.adminapp.section.licenseTickets.repository

import com.preachooda.adminapp.section.licenseTickets.network.LicenseTicketsService
import com.preachooda.assets.util.simpleNetworkCallFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseTicketsRepository @Inject constructor(
    private val api: LicenseTicketsService
) {
    suspend fun getLicenseTickets() = simpleNetworkCallFlow(
        call = { api.getLicenseApplications() },
        loadingMessage = "Получение списка заявок на лицензию...",
        errorMessage = "Ошибка при получении списка заявок на лицензию",
        exceptionMessage = "Ошибка запроса при получении заявок на лицензию"
    )
}
