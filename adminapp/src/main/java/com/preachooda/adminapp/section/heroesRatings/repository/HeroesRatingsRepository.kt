package com.preachooda.adminapp.section.heroesRatings.repository

import com.preachooda.adminapp.section.heroesRatings.network.HeroesRatingsService
import com.preachooda.assets.util.simpleNetworkCallFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroesRatingsRepository @Inject constructor(
    private val api: HeroesRatingsService
) {
    suspend fun loadHeroes() = simpleNetworkCallFlow(
        call = { api.getHeroes() },
        loadingMessage = "Получение списка героев...",
        errorMessage = "Ошибка при получении списка героев",
        exceptionMessage = "Ошибка запроса при получении списка героев"
    )
}
