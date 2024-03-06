package com.preachooda.bokunoheroservice.section.ticket.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Rate
import com.preachooda.bokunoheroservice.section.ticket.domain.CloseErrorIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.CloseMessageIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.DeleteTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.PlayAudioIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.RefreshTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.SaveTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.ShowErrorIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.ShowMessageIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.StopPlayingAudioIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.TicketScreenIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.TicketScreenState
import com.preachooda.bokunoheroservice.section.ticket.repository.TicketRepository
import com.preachooda.bokunoheroservice.utils.AudioHelper
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
) : BaseViewModel<TicketScreenIntent, TicketScreenState>() {
    private val audioHelper = AudioHelper()

    private val _screenState: MutableStateFlow<TicketScreenState> = MutableStateFlow(
        TicketScreenState()
    )
    override val screenState: StateFlow<TicketScreenState> = _screenState

    fun initState(ticketId: Long) {
        loadTicket(ticketId)
    }

    override fun processIntent(intent: TicketScreenIntent) {
        when (intent) {
            RefreshTicketIntent -> loadTicket(_screenState.value.ticket.id)
            DeleteTicketIntent -> deleteTicket(_screenState.value.ticket.id)
            is SaveTicketIntent -> saveTicket(
                description = intent.description,
                comment = intent.comment,
                heroRates = intent.heroRates,
                ticketRate = intent.ticketRate,
            )

            is ShowErrorIntent -> showError(intent.errorText)
            CloseErrorIntent -> _screenState.value = _screenState.value.copy(isError = false)
            CloseMessageIntent -> _screenState.value =
                _screenState.value.copy(isShowMessage = false)

            is ShowMessageIntent -> _screenState.value = _screenState.value.copy(
                isShowMessage = true,
                message = intent.message
            )

            PlayAudioIntent -> playAudio()
            StopPlayingAudioIntent -> stopPlayingAudio()
        }
    }

    private fun loadTicket(ticketId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadTicket(ticketId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data != null && it.data is Ticket) {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isFilesLoading = false,
                                ticket = it.data as Ticket
                            )
                        } else {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isFilesLoading = false,
                                isError = true,
                                message = "Не удалось получить информацию о заявке."
                            )
                        }
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
                                ticket = it.data!!,
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
        try {
            if (_screenState.value.ticket.audioPath.isNullOrBlank()) {
                showError("Нет пути аудиофайла для воспроизведения")
            } else {
                if (_screenState.value.isAudioPlaying) {
                    stopPlayingAudio()
                } else {
                    _screenState.value = _screenState.value.copy(
                        isAudioPlaying = true
                    )
                    audioHelper.startPlaying(_screenState.value.ticket.audioPath!!) {
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

    private fun deleteTicket(ticketId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTicket(ticketId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isShowMessage = true,
                            closeScreen = true,
                            message = it.data ?: "Заявка удалена"
                        )
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
        }
    }

    private fun saveTicket(
        description: String,
        comment: String,
        heroRates: Map<Long, Rate>,
        ticketRate: Rate,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = if (_screenState.value.ticket.status == ActivityStatus.EVALUATION)
                ActivityStatus.COMPLETED else _screenState.value.ticket.status
            val ticketToSave = _screenState.value.ticket.copy(
                description = description,
                comment = comment,
                heroRates = heroRates,
                rate = ticketRate,
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
                                message = "Неизвестно, сохранилась ли заявка, повторите действие.",
                                ticket = ticketToSave
                            )
                        }
                    }

                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                            message = it.message,
                            ticket = ticketToSave
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
        }
    }
}
