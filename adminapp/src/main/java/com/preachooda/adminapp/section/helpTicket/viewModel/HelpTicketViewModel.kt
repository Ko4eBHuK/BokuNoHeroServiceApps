package com.preachooda.adminapp.section.helpTicket.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.helpTicket.domain.HelpTicketIntent
import com.preachooda.adminapp.section.helpTicket.domain.HelpTicketScreenState
import com.preachooda.adminapp.section.helpTicket.repository.HelpTicketRepository
import com.preachooda.adminapp.utils.AudioHelper
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Ticket
import com.preachooda.domain.model.TicketComplexity
import com.preachooda.domain.model.TicketPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpTicketViewModel @Inject constructor(
    private val repository: HelpTicketRepository
) : BaseViewModel<HelpTicketIntent, HelpTicketScreenState>() {
    private val audioHelper = AudioHelper()

    private val _screenState: MutableStateFlow<HelpTicketScreenState> = MutableStateFlow(HelpTicketScreenState())
    override val screenState: StateFlow<HelpTicketScreenState> = _screenState

    override fun processIntent(intent: HelpTicketIntent) {
        when (intent) {
            HelpTicketIntent.CloseError -> closeError()
            is HelpTicketIntent.ShowErrorIntent -> showError(intent.message)
            HelpTicketIntent.CloseMessage -> closeMessage()
            is HelpTicketIntent.Refresh -> loadData(intent.ticketId)
            HelpTicketIntent.PlayAudioIntent -> playAudio()
            HelpTicketIntent.StopPlayingAudioIntent -> stopPlayingAudio()
            is HelpTicketIntent.ConfirmTicket -> confirmTicket(
                intent.heroes,
                intent.complexity,
                intent.priority
            )
            HelpTicketIntent.RejectTicket -> rejectTicket()
            is HelpTicketIntent.AutoHeroes -> findSuitableHeroes(intent.complexity, intent.priority)
            HelpTicketIntent.CloseAutoHeroes -> _screenState.value = _screenState.value.copy(useAutoHeroes = false)
        }
    }

    private fun loadData(ticketId: Long) {
        // get ticket
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTicket(ticketId).collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            ticket = networkCall.data,
                            isFilesLoading = false
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                            isFilesLoading = false,
                            message = networkCall.message
                        )
                    }
                    Status.LOADING -> {
                        if (networkCall.data != null) {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                ticket = networkCall.data,
                                isFilesLoading = true,
                                message = networkCall.message
                            )
                        } else {
                            _screenState.value = _screenState.value.copy(
                                isLoading = true,
                                message = networkCall.message
                            )
                        }
                    }
                }
            }
        }
        // get heroes
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAvailableHeroes().collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            availableHeroes = networkCall.data ?: emptyList()
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                            message = networkCall.message
                        )
                    }
                    Status.LOADING -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = true,
                            message = networkCall.message
                        )
                    }
                }
            }
        }
    }

    private fun findSuitableHeroes(complexity: TicketComplexity, priority: TicketPriority) {
        viewModelScope.launch(Dispatchers.IO) {
            val ticketInfo = Ticket(
                ticketComplexity = complexity,
                priority = priority,
                categories = _screenState.value.ticket?.categories?: emptySet()
            )
            repository.getSuitableHeroes(ticketInfo).collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            suitableHeroes = networkCall.data ?: emptyList(),
                            useAutoHeroes = true
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                            message = networkCall.message
                        )
                    }
                    Status.LOADING -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = true,
                            message = networkCall.message
                        )
                    }
                }
            }
        }
    }

    private fun closeError() {
        _screenState.value = _screenState.value.copy(
            isError = false
        )
    }

    private fun showError(errorText: String) {
        _screenState.value = _screenState.value.copy(
            isError = true,
            message = errorText
        )
    }

    private fun closeMessage() {
        _screenState.value = _screenState.value.copy(
            isMessage = false
        )
    }

    private fun playAudio() {
        try {
            _screenState.value.ticket?.let { ticket ->
                if (ticket.audioPath.isNullOrBlank()) {
                    showError("Нет пути аудиофайла для воспроизведения")
                } else {
                    if (_screenState.value.isAudioPlaying) {
                        stopPlayingAudio()
                    } else {
                        _screenState.value = _screenState.value.copy(
                            isAudioPlaying = true
                        )
                        audioHelper.startPlaying(ticket.audioPath!!) {
                            _screenState.value = _screenState.value.copy(
                                isAudioPlaying = false
                            )
                        }
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

    private fun rejectTicket() {
        viewModelScope.launch(Dispatchers.IO) {
            val rejectedTicket = _screenState.value.ticket?.copy(
                status = ActivityStatus.REJECTED
            )
            rejectedTicket?.let {
                repository.handleTicket(it).collect { networkCall ->
                    when (networkCall.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                ticket = networkCall.data
                            )
                        }
                        Status.ERROR -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                                message = networkCall.message
                            )
                        }
                        Status.LOADING -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = true,
                                message = networkCall.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun confirmTicket(
        heroes: List<Hero>,
        complexity: TicketComplexity,
        priority: TicketPriority
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val confirmedTicket = _screenState.value.ticket?.copy(
                status = ActivityStatus.ASSIGNED,
                heroes = heroes,
                ticketComplexity = complexity,
                priority = priority
            )
            confirmedTicket?.let {
                repository.handleTicket(it).collect { networkCall ->
                    when (networkCall.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                ticket = networkCall.data
                            )
                        }
                        Status.ERROR -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                                message = networkCall.message
                            )
                        }
                        Status.LOADING -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = true,
                                message = networkCall.message
                            )
                        }
                    }
                }
            }
        }
    }
}
