package com.preachooda.bokunoheroservice.section.home.repository

import android.location.Location
import com.preachooda.bokunoheroservice.BuildConfig
import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.NetworkCallState
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.home.network.HomeService
import com.preachooda.bokunoheroservice.utils.SystemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val api: HomeService,
    private val systemRepository: SystemRepository
) {
    fun logout() {
        systemRepository.clearUserToken()
    }

    suspend fun sendSos() = channelFlow {
        send(NetworkCallState(status = Status.LOADING))
        try {
            val sosTicket = Ticket(
                userId = systemRepository.getUserId(),
                creationDate = SimpleDateFormat(BuildConfig.DATE_COMMON_FORMAT).format(Date())
            )

            val sendTicket: (Ticket) -> Unit = {
                runBlocking(Dispatchers.IO) {
                    try {
                        val response = api.sendSosTicket(it)
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                send(
                                    NetworkCallState(
                                        status = Status.SUCCESS,
                                        data = "Создана заявка с номером = ${response.body()?.id}"
                                    )
                                )
                            } else {
                                send(
                                    NetworkCallState(
                                        status = Status.ERROR,
                                        data = "Тело ответа пусто при успешном получении ответа."
                                    )
                                )
                            }
                        } else {
                            send(
                                NetworkCallState(
                                    status = Status.ERROR,
                                    data = response.message()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        send(
                            NetworkCallState(
                                status = Status.ERROR,
                                data = "Ошибка при отправке заявки. ${e.message}"
                            )
                        )
                    } finally {
                        close()
                    }
                }
            }

            val onLocationSuccess: (Location) -> Unit = {
                runBlocking(Dispatchers.IO) {
                    val location = LocationSimple(
                        latitude = it.latitude.toFloat(),
                        longitude = it.longitude.toFloat()
                    )

                    sendTicket(
                        sosTicket.copy(
                            description = "SOS заявка",
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
            }

            val onLocationFailure: (String) -> Unit = {
                runBlocking(Dispatchers.IO) {
                    sendTicket(
                        sosTicket.copy(
                            description = "SOS заявка. $it"
                        )
                    )
                }
            }

            systemRepository.getLocationFused(onLocationSuccess, onLocationFailure)
        } catch (e: Exception) {
            send(
                NetworkCallState(
                    status = Status.ERROR,
                    data = "Ошибка при совершении запроса.\n${e.message}"
                )
            )
            close()
        } finally {
            awaitClose()
        }
    }.flowOn(Dispatchers.IO)
}