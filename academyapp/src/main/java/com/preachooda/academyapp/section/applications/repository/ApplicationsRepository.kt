package com.preachooda.academyapp.section.applications.repository

import com.preachooda.academyapp.section.applications.network.ApplicationsService
import com.preachooda.academyapp.utils.SystemRepository
import com.preachooda.assets.util.simpleNetworkCallFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationsRepository @Inject constructor(
    private val api: ApplicationsService,
    private val systemRepository: SystemRepository
) {
    suspend fun getApplications() = simpleNetworkCallFlow(
        call = { api.getApplications(systemRepository.getUserId()) },
        loadingMessage = "Получение списка заявлений на поступление...",
        errorMessage = "Ошибка при получении списка заявлений",
        exceptionMessage = "Ошибка при совершении запроса получения списка заявлений"
    )
}
