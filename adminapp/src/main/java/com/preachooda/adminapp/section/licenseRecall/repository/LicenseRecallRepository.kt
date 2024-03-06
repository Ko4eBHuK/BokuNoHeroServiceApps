package com.preachooda.adminapp.section.licenseRecall.repository

import com.preachooda.adminapp.section.licenseRecall.network.LicenseRecallService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.QualificationExamApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRecallRepository @Inject constructor(
    private val api: LicenseRecallService
) {
    suspend fun loadHeroes() = simpleNetworkCallFlow(
        call = { api.getHeroes() },
        loadingMessage = "Загрузка списка героев...",
        errorMessage = "Ошбика при загрузке списка героев",
        exceptionMessage = "Ошибка запроса загрузки списка героев"
    )

    suspend fun recallHeroLicense(
        hero: Hero
    ) = simpleNetworkCallFlow(
        call = { api.recallLicense(hero) },
        loadingMessage = "Отзыв лицензии...",
        errorMessage = "Ошбика при отзыве лицензии",
        exceptionMessage = "Ошибка запроса отзыва лицензии"
    )
}
