package com.preachooda.bokunoheroservice.section.ticket.repository

import com.preachooda.assets.util.NetworkCallState
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.ticket.network.TicketService
import com.preachooda.bokunoheroservice.utils.SystemRepository
import com.preachooda.bokunoheroservice.utils.getAudioFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getVideoFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.saveBase64ToAudioFile
import com.preachooda.bokunoheroservice.utils.saveBase64ToImageFile
import com.preachooda.bokunoheroservice.utils.saveBase64ToVideoFile
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(
    val api: TicketService,
    val systemRepository: SystemRepository
) {
    suspend fun loadTicket(ticketId: Long) = flow {
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

    suspend fun deleteTicket(ticketId: Long) = flow {
        emit(NetworkCallState(status = Status.LOADING, message = "Удаление заявки..."))
        try {
            val apiResponse = api.deleteTicket(ticketId)
            if (apiResponse.isSuccessful) {
                emit(
                    NetworkCallState(
                        status = Status.SUCCESS,
                        data = "Заявка удалена."
                    )
                )
            } else {
                emit(
                    NetworkCallState(
                        status = Status.ERROR,
                        message = "Удаление заявки произошло с ошибкой: код ${apiResponse.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                NetworkCallState(
                    status = Status.ERROR,
                    message = "Ошибка при удалении заявки.\n${e.message}"
                )
            )
        }
    }

    suspend fun saveTicket(ticket: Ticket) = flow {
        emit(NetworkCallState(status = Status.LOADING, message = "Сохранение заявки..."))
        try {
            val apiResponse = api.saveTicket(ticket.id, ticket)
            if (apiResponse.isSuccessful) {
                if (apiResponse.body() != null) {
                    emit(
                        NetworkCallState(
                            status = Status.SUCCESS,
                            data = apiResponse.body(),
                            message = "Заявка сохранена."
                        )
                    )
                } else {
                    emit(
                        NetworkCallState(
                            status = Status.ERROR,
                            message = "Не удалось сохранить заявку. Код ${apiResponse.code()}."
                        )
                    )
                }
            } else {
                emit(
                    NetworkCallState(
                        status = Status.ERROR,
                        message = "Сохранение заявки произошло с ошибкой: код ${apiResponse.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                NetworkCallState(
                    status = Status.ERROR,
                    message = "Ошибка при сохранении заявки.\n${e.message}"
                )
            )
        }
    }
}
