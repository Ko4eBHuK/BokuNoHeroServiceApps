package com.preachooda.heroapp.section.login.repository

import com.preachooda.assets.util.NetworkCallState
import com.preachooda.heroapp.section.login.domain.LoginStatus
import com.preachooda.heroapp.section.login.network.AuthBody
import com.preachooda.heroapp.section.login.network.LoginService
import com.preachooda.heroapp.utils.SystemRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val api: LoginService,
    private val systemRepository: SystemRepository
) {
    fun getLoginStatus(): LoginStatus {
        return if ((systemRepository.getHeroToken() ?: "").isNotBlank()) {
            LoginStatus.LOGGED
        } else {
            LoginStatus.NOT_LOGGED
        }
    }

    suspend fun authenticate(
        username: String,
        password: String
    ) = flow {
        emit(NetworkCallState.loading(message = "Выполняется вход..."))
        try {
            val response = api.loginRequest(AuthBody(username, password))
            if (response.isSuccessful) {
                if (response.body() != null) {
                    systemRepository.setHeroNetworkToken(response.body()!!.accessToken)
                    systemRepository.setHeroId(response.body()!!.entityId)
                    emit(NetworkCallState.success(data = "Выполнен вход в систему."))
                } else {
                    emit(NetworkCallState.error(message = "Ответ системы на вход не определён, повторите попытку."))
                }
            } else {
                emit(NetworkCallState.error(message = "Запрос входа в систему произошёл с ошибкой. Код ${response.code()}."))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error(message = "Ошибка выполнения запроса входа: ${e.message}"))
        }
    }
}
