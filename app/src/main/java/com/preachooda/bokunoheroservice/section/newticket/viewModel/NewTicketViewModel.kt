package com.preachooda.bokunoheroservice.section.newticket.viewModel

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.preachooda.bokunoheroservice.BuildConfig
import com.preachooda.domain.FileNotDeletedException
import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.newticket.MESSAGE_NEW_TICKET_CREATED
import com.preachooda.bokunoheroservice.section.newticket.MESSAGE_NEW_TICKET_CREATION_FAILED
import com.preachooda.bokunoheroservice.section.newticket.domain.*
import com.preachooda.bokunoheroservice.section.newticket.repository.NewTicketRepository
import com.preachooda.bokunoheroservice.utils.AudioHelper
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.bokunoheroservice.utils.SystemRepository
import com.preachooda.bokunoheroservice.utils.getAudioFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getVideoFileByNameFromPublicDirectory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTicketViewModel @Inject constructor(
    private val repository: NewTicketRepository,
    private val systemRepository: SystemRepository
) : BaseViewModel<NewTicketScreenIntent, NewTicketScreenState>() {
    private val audioHelper = AudioHelper()

    private val _newTicketScreenState: MutableStateFlow<NewTicketScreenState> = MutableStateFlow(
        NewTicketScreenState()
    )
    override val screenState: StateFlow<NewTicketScreenState> = _newTicketScreenState

    override fun processIntent(intent: NewTicketScreenIntent) {
        when (intent) {
            Init -> initState()
            is CloseCreation -> {
                closeCreation(intent.save, intent.ticketDescription)
            }

            is CreateTicketIntent -> createNewTicket(intent.ticketDescription)
            is SelectCategoryIntent -> selectCategory(intent.category)
            is ShowErrorIntent -> showError(intent.errorText)
            CloseErrorIntent -> closeError()
            CloseMessageIntent -> closeMessage()
            is AddPhotoPathIntent -> addPhotoPath(intent.absolutePath)
            is AddVideoPathIntent -> addVideoPath(intent.absolutePath)
            GetLocationIntent -> getLocation()
            RecordAudioIntent -> recordAudio()
            StopAudioRecordingIntent -> stopAudioRecording()
            PlayAudioIntent -> playAudio()
            StopPlayingAudioIntent -> stopPlayingAudio()
            DeleteAudioIntent -> deleteAudio()
            is DeletePhotoIntent -> deletePhoto(intent.position)
            DeleteVideoIntent -> deleteVideo()
        }
    }

    private fun initState() {
        if (!_newTicketScreenState.value.isInit) {
            val initialTicket = systemRepository.getTicketTemplate() ?: Ticket(
                userId = systemRepository.getUserId()
            )

            val availableCategories = Ticket.Category.entries.toSet()

            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                ticket = initialTicket,
                availableCategories = availableCategories,
                selectedCategories = initialTicket.categories,
                photosPaths = initialTicket.photosPaths,
                videoPath = initialTicket.videoPath,
                audioPath = initialTicket.audioPath,
                isInit = true
            )

            getLocation()
        }
    }

    private fun closeCreation(shouldSave: Boolean, ticketDescription: String) {
        if (shouldSave) storeTicketTemplate(ticketDescription)
        else systemRepository.deleteTicketTemplate()
        _newTicketScreenState.value = NewTicketScreenState()
    }

    private fun closeError() {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            isError = false
        )
    }

    private fun closeMessage() {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            isShowMessage = false
        )
    }

    private fun getLocation() = viewModelScope.launch(Dispatchers.IO) {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            locationStatus = Status.LOADING
        )

        val onLocationSuccess: (Location) -> Unit = {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                locationStatus = Status.SUCCESS,
                locationInfo = LocationSimple(
                    it.latitude.toFloat(),
                    it.longitude.toFloat(),
                    "Локация получена."
                ),
                ticket = _newTicketScreenState.value.ticket.copy(
                    latitude = it.latitude.toFloat(),
                    longitude = it.longitude.toFloat()
                )
            )
        }
        val onLocationFailure: (String) -> Unit = {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                locationStatus = Status.ERROR,
                locationInfo = LocationSimple(
                    latitude = .0f,
                    longitude = .0f,
                    description = it
                ),
                ticket = _newTicketScreenState.value.ticket.copy(
                    latitude = .0f,
                    longitude = .0f
                )
            )
        }

        repository.getLocation(onLocationSuccess, onLocationFailure)
    }


    private fun addPhotoPath(fileName: String) {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            photosPaths = _newTicketScreenState.value.photosPaths + fileName
        )
    }

    private fun addVideoPath(fileName: String) {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            videoPath = fileName
        )
    }

    private fun recordAudio() {
        try {
            if (!_newTicketScreenState.value.isAudioRecording) {
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    isAudioRecording = true
                )
                audioHelper.startRecording(_newTicketScreenState.value.ticket.userId.toString())
            }
        } catch (e: Exception) {
            showError("Не удалось записать звук: ${e.message}")
        }
    }

    private fun stopAudioRecording() {
        try {
            val audioFileName = audioHelper.stopRecording()
            if (audioFileName.isNotBlank()) {
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    audioPath = audioFileName,
                    isAudioRecording = false
                )
            } else showError("Аудиофайл не записан.")
        } catch (e: Exception) {
            showError("Не удалось остановить запись звука: ${e.message}")
        }
    }

    private fun playAudio() {
        try {
            if (_newTicketScreenState.value.audioPath.isNullOrBlank()) {
                showError("Нет пути аудиофайла для воспроизведения")
            } else {
                if (_newTicketScreenState.value.isAudioPlaying) {
                    stopPlayingAudio()
                } else {
                    _newTicketScreenState.value = _newTicketScreenState.value.copy(
                        isAudioPlaying = true
                    )
                    audioHelper.startPlaying(_newTicketScreenState.value.audioPath!!) {
                        _newTicketScreenState.value = _newTicketScreenState.value.copy(
                            isAudioPlaying = false
                        )
                    }
                }
            }
        } catch (e: Exception) {
            showError("Не удалось воспроизвести запись: ${e.message}")
        }
    }

    private fun stopPlayingAudio() {
        try {
            audioHelper.stopPlaying()
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isAudioPlaying = false
            )
        } catch (e: Exception) {
            showError("Не удалось остановить воспроизвдение звука: ${e.message}")
        }
    }

    private fun showError(errorText: String) {
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            isError = true,
            message = errorText
        )
    }

    private fun createNewTicket(description: String) {
        viewModelScope.launch {
            val ticketCreationTime = SimpleDateFormat(BuildConfig.DATE_COMMON_FORMAT).format(Date())
            val ticketToSend = _newTicketScreenState.value.ticket.copy(
                userId = systemRepository.getUserId(),
                creationDate = ticketCreationTime,
                description = description,
                categories = _newTicketScreenState.value.selectedCategories,
                photosPaths = _newTicketScreenState.value.photosPaths,
                videoPath = _newTicketScreenState.value.videoPath,
                audioPath = _newTicketScreenState.value.audioPath,
            )

            repository.sendNewTicketForCreation(ticketToSend).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _newTicketScreenState.value = _newTicketScreenState.value.copy(
                            isShowMessage = true,
                            isLoading = false,
                            isError = false,
                            shouldCloseScreen = true,
                            message = it.data ?: MESSAGE_NEW_TICKET_CREATED
                        )
                        systemRepository.deleteTicketTemplate()
                    }

                    Status.ERROR -> {
                        _newTicketScreenState.value = _newTicketScreenState.value.copy(
                            isShowMessage = false,
                            isLoading = false,
                            isError = true,
                            message = MESSAGE_NEW_TICKET_CREATION_FAILED + it.message
                        )
                    }

                    Status.LOADING -> {
                        _newTicketScreenState.value = _newTicketScreenState.value.copy(
                            isShowMessage = false,
                            isLoading = true,
                            isError = false,
                            message = it.message
                        )
                    }
                }
            }
        }
    }

    private fun selectCategory(category: Ticket.Category) {
        val newCategories = if (_newTicketScreenState.value.selectedCategories.contains(category)) {
            _newTicketScreenState.value.selectedCategories - category
        } else {
            _newTicketScreenState.value.selectedCategories + category
        }
        _newTicketScreenState.value = _newTicketScreenState.value.copy(
            selectedCategories = newCategories
        )
    }

    private fun storeTicketTemplate(ticketDescription: String) {
        val ticketToSave = _newTicketScreenState.value.ticket.copy(
            userId = systemRepository.getUserId(),
            description = ticketDescription,
            categories = _newTicketScreenState.value.selectedCategories,
            latitude = .0f,
            longitude = .0f,
            photosPaths = _newTicketScreenState.value.photosPaths,
            videoPath = _newTicketScreenState.value.videoPath,
            audioPath = _newTicketScreenState.value.audioPath,
        )
        systemRepository.saveTicketTemplate(ticketToSave)
    }

    private fun deleteAudio() {
        try {
            val audioPath = getAudioFileByNameFromPublicDirectory(
                _newTicketScreenState.value.audioPath ?: ""
            )?.absolutePath
            if (audioPath != null) {
                systemRepository.deleteFile(audioPath)
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    audioPath = null,
                    isShowMessage = true,
                    message = "Файл аудиозаписи удалён."
                )
            } else {
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    isError = true,
                    message = "Файл аудиозаписи не задан."
                )
            }
        } catch (e: FileNotDeletedException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Не удалось удалить файл: ${e.message}"
            )
        } catch (e: FileNotFoundException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Файл для удаления не найден: ${e.message}"
            )
        }
    }

    private fun deletePhoto(position: Int) {
        try {
            val photoPath = getImageFileByNameFromPublicDirectory(
                _newTicketScreenState.value.photosPaths[position]
            )?.absolutePath ?: ""
            systemRepository.deleteFile(photoPath)
            val newPhotoPaths = _newTicketScreenState.value.photosPaths
                .filterIndexed { index, _ -> index != position }
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                photosPaths = newPhotoPaths,
                isShowMessage = true,
                message = "Файл фотографии удалён."
            )
        } catch (e: FileNotDeletedException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Не удалось удалить файл: ${e.message}"
            )
        } catch (e: FileNotFoundException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Файл для удаления не найден: ${e.message}"
            )
        } catch (e: IndexOutOfBoundsException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Определение файла фотографии по позиции не удалось: ${e.message}"
            )
        }
    }

    private fun deleteVideo() {
        try {
            val videoPath = getVideoFileByNameFromPublicDirectory(
                _newTicketScreenState.value.videoPath ?: ""
            )?.absolutePath
            if (videoPath != null) {
                systemRepository.deleteFile(videoPath)
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    videoPath = null,
                    isShowMessage = true,
                    message = "Файл видеозаписи удалён."
                )
            } else {
                _newTicketScreenState.value = _newTicketScreenState.value.copy(
                    isError = true,
                    message = "Файл видеозаписи не задан."
                )
            }
        } catch (e: FileNotDeletedException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Не удалось удалить файл: ${e.message}"
            )
        } catch (e: FileNotFoundException) {
            _newTicketScreenState.value = _newTicketScreenState.value.copy(
                isError = true,
                message = "Файл для удаления не найден: ${e.message}"
            )
        }
    }
}
