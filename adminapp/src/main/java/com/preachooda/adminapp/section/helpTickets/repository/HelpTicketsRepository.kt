package com.preachooda.adminapp.section.helpTickets.repository

import com.preachooda.adminapp.section.helpTickets.network.HelpTicketsService
import com.preachooda.assets.util.simpleNetworkCallFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpTicketsRepository @Inject constructor(
    private val api: HelpTicketsService
) {
    suspend fun getHelpTickets() = simpleNetworkCallFlow(
        call = { api.getTickets() },
        loadingMessage = "Получение списка заявок на помощь...",
        errorMessage = "Ошибка при получении списка заявок на помощь",
        exceptionMessage = "Ошибка запроса при получении заявок на помощь"
    )
}
