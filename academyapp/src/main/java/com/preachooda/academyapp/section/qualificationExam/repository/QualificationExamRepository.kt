package com.preachooda.academyapp.section.qualificationExam.repository

import com.preachooda.academyapp.section.qualificationExam.network.QualificationExamService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.QualificationExamApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QualificationExamRepository @Inject constructor(
    private val api: QualificationExamService
) {
    suspend fun getHeroes() = simpleNetworkCallFlow(
        call = { api.getHeroes() },
        loadingMessage = "Получение списка героев...",
        errorMessage = "Ошибка при получении списка героев",
        exceptionMessage = "Ошибка при совершении запроса получения списка героев"
    )

    suspend fun sendQualificationExamApplication(application: QualificationExamApplication) =
        simpleNetworkCallFlow(
            call = { api.sendQualificationExamApplication(application) },
            loadingMessage = "Отправка заявления на квалификационный экзамен...",
            errorMessage = "Ошибка при отправке заявления",
            exceptionMessage = "Ошибка при совершении запроса отправки заявления"
        )
}
