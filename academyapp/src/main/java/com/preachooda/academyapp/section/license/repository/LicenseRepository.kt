package com.preachooda.academyapp.section.license.repository

import com.preachooda.academyapp.section.license.network.LicenseService
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.LicenseApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRepository @Inject constructor(
    private val api: LicenseService
) {
    suspend fun sendLicenseApplication(licenseApplication: LicenseApplication) =
        simpleNetworkCallFlow(
            call = { api.sendLicenseApplication(licenseApplication) },
            loadingMessage = "Отправка заявления на получение лицензии...",
            errorMessage = "Ошибка при внесении данных о заявлении на получение лицензии",
            exceptionMessage = "Ошибка при совершении запроса отправки заявления на получение лицензии"
        )
}
