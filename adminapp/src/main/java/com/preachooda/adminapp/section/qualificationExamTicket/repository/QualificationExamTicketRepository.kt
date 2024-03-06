package com.preachooda.adminapp.section.qualificationExamTicket.repository

import com.preachooda.adminapp.section.qualificationExamTicket.network.QualificationExamTicketService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.QualificationExamApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QualificationExamTicketRepository @Inject constructor(
    private val api: QualificationExamTicketService
) {
    suspend fun loadQualificationExamApplication(ticketId: Long) = simpleNetworkCallFlow(
        call = { api.getQualificationExamApplication(ticketId) },
        loadingMessage = "Загрузка заявки на экзамен...",
        errorMessage = "Ошбика при загрузке заявки на экзамен",
        exceptionMessage = "Ошибка запроса загрузки заявки на экзамен"
    )

    suspend fun loadHeroes() = simpleNetworkCallFlow(
        call = { api.getHeroes() },
        loadingMessage = "Загрузка списка героев...",
        errorMessage = "Ошбика при загрузке списка героев",
        exceptionMessage = "Ошибка запроса загрузки списка героев"
    )

    suspend fun sendQualificationExamApplication(
        application: QualificationExamApplication
    ) = simpleNetworkCallFlow(
        call = { api.sendQualificationExamApplication(application.id?.toLong() ?: -1, application) },
        loadingMessage = "Сохранение заявки на экзамен...",
        errorMessage = "Ошбика при сохранении заявки на экзамен",
        exceptionMessage = "Ошибка запроса сохранения заявки на экзамен"
    )
}
