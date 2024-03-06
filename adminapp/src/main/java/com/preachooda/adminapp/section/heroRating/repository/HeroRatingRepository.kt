package com.preachooda.adminapp.section.heroRating.repository

import com.preachooda.adminapp.section.heroRating.network.HeroRatingService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.Hero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroRatingRepository @Inject constructor(
    private val api: HeroRatingService
) {
    suspend fun loadHero(heroId: Long) = simpleNetworkCallFlow(
        call = { api.getHero(heroId) },
        loadingMessage = "Получение информации о герое...",
        errorMessage = "Ошибка при получении информации о герое",
        exceptionMessage = "Ошибка запроса при получении информации о герое"
    )

    suspend fun sendHero(hero: Hero) = simpleNetworkCallFlow(
        call = { api.editHero(hero.id, hero) },
        loadingMessage = "Отправка информации о герое...",
        errorMessage = "Ошибка отправки информации о герое",
        exceptionMessage = "Ошибка запроса отправки информации о герое"
    )
}
