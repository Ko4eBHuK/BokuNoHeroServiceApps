package com.preachooda.bokunoheroservice.section.tickets.repository

import com.preachooda.assets.util.NetworkCallState
import com.preachooda.assets.util.Status
import com.preachooda.bokunoheroservice.section.tickets.network.TicketsService
import com.preachooda.bokunoheroservice.utils.SystemRepository
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketsRepository @Inject constructor(
    private val api: TicketsService,
    private val systemRepository: SystemRepository
) {
    suspend fun getTicketsRemote() = channelFlow {
        send(NetworkCallState(status = Status.LOADING))

        try {
            val apiResponse = api.getTicketsList(/*systemRepository.getUserId()*/) // TODO: userId
            if (apiResponse.isSuccessful) {
                if (apiResponse.body() != null) {
                    send(
                        NetworkCallState(
                            status = Status.SUCCESS,
                            data = apiResponse.body()
                        )
                    )
                } else {
                    send(
                        NetworkCallState(
                            status = Status.ERROR,
                            message = "Список заявок не опрделён в ответе от сервера."
                        )
                    )
                }
            } else {
                send(
                    NetworkCallState(
                        status = Status.ERROR,
                        message = "Запрос заявок произошёл с ошибкой: код ${apiResponse.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            send(
                NetworkCallState(
                    status = Status.ERROR,
                    message = "Ошибка при совершении запроса списка заявок.\n${e.message}"
                )
            )
        }
    }
}