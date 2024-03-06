package com.preachooda.academyapp.section.application.repository

import com.preachooda.academyapp.section.application.network.ApplicationService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.AcademyApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationRepository @Inject constructor(
    private val api: ApplicationService
) {
    suspend fun getApplication(applicationId: Long) = simpleNetworkCallFlow(
        call = { api.getApplication(applicationId) },
        loadingMessage = "Получение информации заявления на поступление...",
        errorMessage = "Ошибка при получении информации заявления на поступление",
        exceptionMessage = "Ошибка при совершении запроса получения информации заявления на поступление"
    )

    suspend fun sendApplication(application: AcademyApplication) = simpleNetworkCallFlow(
        call = { api.postApplication(application) },
        loadingMessage = "Отправка заявления на поступление...",
        errorMessage = "Ошибка при отправке заявления на поступление",
        exceptionMessage = "Ошибка при совершении запроса отправки заявления на поступление"
    )
}
