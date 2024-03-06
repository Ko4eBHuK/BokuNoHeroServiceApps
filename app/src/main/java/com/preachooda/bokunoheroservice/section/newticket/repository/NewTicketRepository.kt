package com.preachooda.bokunoheroservice.section.newticket.repository

import android.location.Location
import com.preachooda.assets.util.NetworkCallState
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.newticket.network.NewTicketService
import com.preachooda.bokunoheroservice.utils.SystemRepository
import com.preachooda.bokunoheroservice.utils.getAudioFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getVideoFileByNameFromPublicDirectory
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewTicketRepository @Inject constructor(
    private val api: NewTicketService,
    private val systemRepository: SystemRepository
) {
    suspend fun sendNewTicketForCreation(ticket: Ticket) = flow {
        emit(NetworkCallState.loading("Создание заявки..."))

        try {
            val response = api.sendNewTicket(ticket)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    emit(NetworkCallState.loading("Создана заявка с номером ${response.body()!!.id}"))

                    if (response.body()!!.photosPaths.isNotEmpty() ||
                        !response.body()!!.videoPath.isNullOrBlank() ||
                        !response.body()!!.audioPath.isNullOrBlank()
                    ) {
                        emit(NetworkCallState.loading("Создана заявка с номером ${response.body()!!.id}\nОтправка файлов..."))

                        val filesList = mutableListOf<File>()
                        response.body()!!.photosPaths.forEach { fileName ->
                            getImageFileByNameFromPublicDirectory(fileName)?.let { file ->
                                filesList.add(file)
                            }
                        }
                        response.body()!!.videoPath?.let { fileName ->
                            getVideoFileByNameFromPublicDirectory(fileName)?.let { file ->
                                filesList.add(file)
                            }
                        }
                        response.body()!!.audioPath?.let { fileName ->
                            getAudioFileByNameFromPublicDirectory(fileName)?.let { file ->
                                filesList.add(file)
                            }
                        }
                        val filesMultipartBodies = mutableListOf<MultipartBody.Part>()

                        filesList.forEach { file ->
                            val mediaType = when (file.extension) {
                                "jpg", "jpeg" -> "image/jpeg".toMediaType()
                                "mp4" -> "video/mp4".toMediaType()
                                "mp3" -> "audio/mp3".toMediaType()
                                else -> null
                            }
                            mediaType?.let {
                                val fileRequestBody = file.asRequestBody(mediaType)
                                val multipartBody = MultipartBody.Part.createFormData(
                                    "files",
                                    file.name,
                                    fileRequestBody
                                )
                                filesMultipartBodies.add(multipartBody)
                            }
                        }

                        val responseFilesSending = api.uploadFiles(response.body()!!.id, filesMultipartBodies)
                        if (responseFilesSending.isSuccessful) {
                            emit(NetworkCallState.success("Файлы отправлены."))
                        } else {
                            emit(NetworkCallState.error<String>("Файлы не отправлены."))
                        }
                    } else {
                        emit(NetworkCallState.success("Создана заявка с номером ${response.body()!!.id}"))
                    }
                } else {
                    emit(NetworkCallState.error<String>("Тело ответа пусто при успешном получении ответа."))
                }
            } else {
                emit(NetworkCallState.error<String>(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error("Ошибка при совершении запроса.\n${e.message}"))
        }
    }

    fun getLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (String) -> Unit
    ) = systemRepository.getLocationFused(onSuccess, onFailure)
}
