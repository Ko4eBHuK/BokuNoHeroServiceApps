package com.preachooda.adminapp.section.licenseTicket.repository

import com.preachooda.adminapp.section.licenseTicket.network.LicenseTicketService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.LicenseApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseTicketRepository @Inject constructor(
    private val api: LicenseTicketService
) {
    suspend fun getLicenseApplication(id: Long) = simpleNetworkCallFlow(
        call = { api.getLicenseApplication(id) },
        loadingMessage = "Получение данных по заявке на лицензию...",
        errorMessage = "Ошибка при получении данных по заявке на лицензию",
        exceptionMessage = "Ошибка запроса при получении данных по заявке на лицензию"
    )

    suspend fun handleTicket(ticket: LicenseApplication) = simpleNetworkCallFlow(
        call = { api.handleLicenseApplication(ticket.id ?: -1, ticket) },
        loadingMessage = "Обработка заявки...",
        errorMessage = "Ошибка обработки заявки",
        exceptionMessage = "Ошибка запроса обработки заявки"
    )
}
