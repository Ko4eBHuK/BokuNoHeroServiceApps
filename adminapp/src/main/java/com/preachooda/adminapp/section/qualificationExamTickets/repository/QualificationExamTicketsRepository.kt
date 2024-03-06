package com.preachooda.adminapp.section.qualificationExamTickets.repository

import com.preachooda.adminapp.section.qualificationExamTickets.network.QualificationExamTicketsService
import com.preachooda.assets.util.simpleNetworkCallFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QualificationExamTicketsRepository @Inject constructor(
    private val api: QualificationExamTicketsService
) {
    suspend fun getExamTickets() = simpleNetworkCallFlow(
        call = { api.getQualificationExamTickets() },
        loadingMessage = "Получение списка заявок на экзамен...",
        errorMessage = "Ошибка при получении списка заявок на экзамен",
        exceptionMessage = "Ошибка запроса при получении заявок на экзамен"
    )
}
