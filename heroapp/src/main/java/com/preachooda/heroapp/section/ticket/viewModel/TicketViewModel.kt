package com.preachooda.heroapp.section.ticket.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.assets.util.Status
import com.preachooda.heroapp.section.ticket.domain.CloseErrorIntent
import com.preachooda.heroapp.section.ticket.domain.CloseMessageIntent
import com.preachooda.heroapp.section.ticket.domain.PlayAudioIntent
import com.preachooda.heroapp.section.ticket.domain.RefreshTicketIntent
import com.preachooda.heroapp.section.ticket.domain.SaveTicketIntent
import com.preachooda.heroapp.section.ticket.domain.ShowErrorIntent
import com.preachooda.heroapp.section.ticket.domain.ShowMessageIntent
import com.preachooda.heroapp.section.ticket.domain.StopPlayingAudioIntent
import com.preachooda.heroapp.section.ticket.domain.TicketScreenIntent
import com.preachooda.heroapp.section.ticket.domain.TicketScreenState
import com.preachooda.heroapp.section.ticket.repository.TicketRepository
import com.preachooda.heroapp.utils.AudioHelper
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    val repository: TicketRepository
): BaseViewModel<TicketScreenIntent, TicketScreenState>() {
    private val audioHelper = AudioHelper()

    private val _screenState: MutableStateFlow<TicketScreenState> = MutableStateFlow(
        TicketScreenState()
    )
    override val screenState: StateFlow<TicketScreenState> = _screenState

    override fun processIntent(intent: TicketScreenIntent) {
        when (intent) {
            RefreshTicketIntent -> {
                loadTicket()
            }
            is SaveTicketIntent -> {
                saveTicket()
            }
            is ShowErrorIntent -> showError(intent.errorText)
            CloseErrorIntent -> {
                _screenState.value = _screenState.value.copy(
                    isError = false
                )
            }
            CloseMessageIntent -> {
                _screenState.value = _screenState.value.copy(
                    isShowMessage = false
                )
            }
            is ShowMessageIntent -> {
                _screenState.value = _screenState.value.copy(
                    isShowMessage = true,
                    message = intent.message
                )
            }
            PlayAudioIntent -> playAudio()
            StopPlayingAudioIntent -> stopPlayingAudio()
        }
    }

    private fun loadTicket() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadTicket().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            ticket = it.data,
                            isFilesLoading = false
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isFilesLoading = false,
                            isError = true,
                            message = "Не удалось получить информацию о заявке. ${it.message}"
                        )
                    }
                    Status.LOADING -> {
                        if (it.data != null) {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                ticket = it.data,
                                isFilesLoading = true,
                                message = it.message
                            )
                        } else {
                            _screenState.value = _screenState.value.copy(
                                isLoading = true,
                                message = it.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun playAudio() {
        _screenState.value.ticket?.let {
            try {
                if (it.audioPath.isNullOrBlank()) {
                    showError("Нет пути аудиофайла для воспроизведения")
                } else {
                    if (_screenState.value.isAudioPlaying) {
                        stopPlayingAudio()
                    } else {
                        _screenState.value = _screenState.value.copy(
                            isAudioPlaying = true
                        )
                        audioHelper.startPlaying(it.audioPath!!) {
                            _screenState.value = _screenState.value.copy(
                                isAudioPlaying = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Не удалось воспроизвести запись: ${e.message}")
            }
        }
    }

    private fun stopPlayingAudio() {
        try {
            audioHelper.stopPlaying()
            _screenState.value = _screenState.value.copy(
                isAudioPlaying = false
            )
        } catch (e: Exception) {
            showError("Не удалось остановить воспроизвдение звука: ${e.message}")
        }
    }

    private fun showError(errorText: String) {
        _screenState.value = _screenState.value.copy(
            isError = true,
            message = errorText
        )
    }

    private fun saveTicket() {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.value.ticket?.let { ticket ->
                val newStatus = when(ticket.status) {
                    ActivityStatus.ASSIGNED -> ActivityStatus.IN_WORK
                    ActivityStatus.IN_WORK -> ActivityStatus.EVALUATION
                    else -> null
                }
                if (newStatus!= null) {
                    val ticketToSave = ticket.copy(
                        status = newStatus
                    )
                    repository.saveTicket(ticketToSave).collect {
                        when (it.status) {
                            Status.SUCCESS -> {
                                if (it.data != null && it.data is Ticket) {
                                    _screenState.value = _screenState.value.copy(
                                        isLoading = false,
                                        isShowMessage = true,
                                        message = it.message,
                                        ticket = it.data as Ticket
                                    )
                                } else {
                                    _screenState.value = _screenState.value.copy(
                                        isLoading = false,
                                        isError = true,
                                        message = "Неизвестно сохранилась ли заявка, повторите действие."
                                    )
                                }
                            }
                            Status.ERROR -> {
                                _screenState.value = _screenState.value.copy(
                                    isLoading = false,
                                    isError = true,
                                    message = it.message
                                )
                            }
                            Status.LOADING -> {
                                _screenState.value = _screenState.value.copy(
                                    isLoading = true,
                                    message = it.message
                                )
                            }
                        }
                    }
                } else {
                    _screenState.value = _screenState.value.copy(
                        isError = true,
                        message = "Не удалось сохранить заявку. Несоответствие статуса."
                    )
                }
            }
        }
    }
}
