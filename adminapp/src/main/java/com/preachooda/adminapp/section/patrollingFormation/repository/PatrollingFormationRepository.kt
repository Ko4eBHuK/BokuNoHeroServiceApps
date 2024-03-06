package com.preachooda.adminapp.section.patrollingFormation.repository

import com.preachooda.adminapp.section.patrollingFormation.network.PatrollingFormationService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.Patrol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatrollingFormationRepository @Inject constructor(
    private val api: PatrollingFormationService
) {

    suspend fun getAvailableHeroes() = simpleNetworkCallFlow(
        call = { api.getAvailableHeroes() },
        loadingMessage = "Получение списка доступных героев...",
        errorMessage = "Ошибка при получении списка доступных героев",
        exceptionMessage = "Ошибка запроса при получении списка доступных героев"
    )

    suspend fun sendPatrol(patrol: Patrol) = simpleNetworkCallFlow(
        call = { api.postPatrol(patrol) },
        loadingMessage = "Отправка сформированного задания патруля...",
        errorMessage = "Ошибка отправки задания",
        exceptionMessage = "Ошибка при совершении запроса отправки задания"
    )
}
