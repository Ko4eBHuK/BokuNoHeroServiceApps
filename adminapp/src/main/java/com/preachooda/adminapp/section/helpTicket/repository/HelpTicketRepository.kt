package com.preachooda.adminapp.section.helpTicket.repository

import com.preachooda.adminapp.section.helpTicket.network.HelpTicketService
import com.preachooda.adminapp.utils.getAudioFileByNameFromPublicDirectory
import com.preachooda.adminapp.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.adminapp.utils.getVideoFileByNameFromPublicDirectory
import com.preachooda.adminapp.utils.saveBase64ToAudioFile
import com.preachooda.adminapp.utils.saveBase64ToImageFile
import com.preachooda.adminapp.utils.saveBase64ToVideoFile
import com.preachooda.assets.util.NetworkCallState
import com.preachooda.assets.util.Status
import com.preachooda.assets.util.simpleNetworkCallFlow
import com.preachooda.domain.model.Ticket
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpTicketRepository @Inject constructor(
    private val api: HelpTicketService
) {

    suspend fun getTicket(ticketId: Long) = flow {
        emit(NetworkCallState.loading(message = "Получение данных о заявке..."))
        try {
            val apiResponse = api.getTicket(ticketId)
            if (apiResponse.isSuccessful) {
                if (apiResponse.body() != null) {
                    with(apiResponse.body()!!) {
                        if (photosPaths.isNotEmpty() || videoPath != null || audioPath != null) {
                            var shouldLoadFiles = false
                            photosPaths.forEach { photoFileName ->
                                shouldLoadFiles = getImageFileByNameFromPublicDirectory(photoFileName) == null
                                if (shouldLoadFiles) return@forEach
                            }
                            if (!shouldLoadFiles) shouldLoadFiles = getVideoFileByNameFromPublicDirectory(videoPath!!) == null
                            if (!shouldLoadFiles) shouldLoadFiles = getAudioFileByNameFromPublicDirectory(audioPath!!) == null

                            if (shouldLoadFiles) {
                                emit(NetworkCallState(status = Status.LOADING, data = this, message = "Загрузка файлов заявки $id..."))
                                val filesResponse = api.getTicketMedia(id)
                                if (filesResponse.isSuccessful) {
                                    try {
                                        if (filesResponse.body()?.photosCodes?.size == photosPaths.size) {
                                            photosPaths.forEachIndexed { index, photoFileName ->
                                                filesResponse.body()?.photosCodes?.get(index)?.let { code ->
                                                    saveBase64ToImageFile(code, photoFileName)
                                                }
                                            }
                                        }
                                        if (videoPath != null && filesResponse.body()?.videoCode != null) saveBase64ToVideoFile(filesResponse.body()!!.videoCode!!, videoPath!!)
                                        if (audioPath != null && filesResponse.body()?.audioCode != null) saveBase64ToAudioFile(filesResponse.body()!!.audioCode!!, audioPath!!)

                                        emit(NetworkCallState.success(data = this))
                                    } catch (e: Exception) {
                                        emit(NetworkCallState.error<Ticket?>("Ошибка сохранения файлов: ${e.message}"))
                                    }
                                } else {
                                    emit(NetworkCallState.error<Ticket?>("Ошибка загрузки файлов: ${filesResponse.code()}"))
                                }
                            } else {
                                emit(NetworkCallState.success(data = this))
                            }
                        } else {
                            emit(NetworkCallState.success(data = this))
                        }
                    }
                } else {
                    emit(NetworkCallState.success(null))
                }
            } else {
                emit(NetworkCallState.error(message = "Запрос загрузки заявки произошёл с ошибкой: код ${apiResponse.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error(message = "Ошибка при загрузке заявки.\n${e.message}"))
        }
    }

    suspend fun getAvailableHeroes() = simpleNetworkCallFlow(
        call = { api.getAvailableHeroes() },
        loadingMessage = "Получение списка доступных героев...",
        errorMessage = "Ошибка при получении списка доступных героев",
        exceptionMessage = "Ошибка запроса при получении списка доступных героев"
    )

    suspend fun getSuitableHeroes(ticket: Ticket) = simpleNetworkCallFlow(
        call = { api.getSuitableHeroes(ticket) },
        loadingMessage = "Автоподбор героев...",
        errorMessage = "Ошибка при автоподборе героев",
        exceptionMessage = "Ошибка запроса при автоподборе героев"
    )

    suspend fun handleTicket(ticket: Ticket) = simpleNetworkCallFlow(
        call = { api.handleTicket(ticket.id, ticket) },
        loadingMessage = "Отправка заявки на обработку...",
        errorMessage = "Ошибка отправки заявки на обработку",
        exceptionMessage = "Ошибка запроса отправки заявки на обработку"
    )
}
