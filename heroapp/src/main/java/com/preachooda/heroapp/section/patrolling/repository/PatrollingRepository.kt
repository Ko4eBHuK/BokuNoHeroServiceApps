package com.preachooda.heroapp.section.patrolling.repository

import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.Patrol
import com.preachooda.heroapp.section.patrolling.network.PatrollingService
import com.preachooda.heroapp.utils.SystemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatrollingRepository @Inject constructor(
    private val api: PatrollingService,
    private val systemRepository: SystemRepository
) {
    suspend fun getPatrol() = simpleNetworkCallFlow(
        call = { api.getActivePatrol(systemRepository.getHeroId()) },
        loadingMessage = "Получение данных о патрулировании...",
        errorMessage = "Ошибка при получении данных о патрулировании",
        exceptionMessage = "Ошибка при запросе данных о патрулировании"
    )

    suspend fun updatePatrol(patrol: Patrol) = simpleNetworkCallFlow(
        call = { api.updatePatrol(patrol.id?.toLong() ?: -1, patrol) },
        loadingMessage = "Внесение данных о патрулировании...",
        errorMessage = "Ошибка при изменении данных о патрулировании",
        exceptionMessage = "Ошибка при запросе изменения патрулирования"
    )
}
