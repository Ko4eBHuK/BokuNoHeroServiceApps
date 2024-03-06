package com.preachooda.bokunoheroservice.section.academyApplication.repository

import com.preachooda.assets.util.NetworkCallState
import com.preachooda.bokunoheroservice.section.academyApplication.network.AcademyApplicationService
import com.preachooda.domain.model.AcademyApplication
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademyApplicationRepository @Inject constructor(
    private val api: AcademyApplicationService
) {
    suspend fun sendApplication(application: AcademyApplication) = flow {
        emit(NetworkCallState.loading("Отправка заявления..."))
        try {
            val response = api.sendApplication(application)
            if (response.isSuccessful) {
                emit(NetworkCallState.success(response.body()))
            } else {
                emit(NetworkCallState.error(message = "Ошибка при создании заявления: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error(message = "Ошибка при отправке заявления: ${e.message}"))
        }
    }

    suspend fun getAcademies() = flow {
        emit(NetworkCallState.loading("Получение списка академий..."))
        try {
            val response = api.getAcademies()
            if (response.isSuccessful) {
                emit(NetworkCallState.success(response.body()))
            } else {
                emit(NetworkCallState.error(message = "Ошибка при получении списка академий: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error(message = "Ошибка запроса списка академий: ${e.message}"))
        }
    }
}
