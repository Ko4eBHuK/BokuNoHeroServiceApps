package com.preachooda.adminapp.section.qualificationExamTicket.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.qualificationExamTicket.domain.QualificationExamTicketIntent
import com.preachooda.adminapp.section.qualificationExamTicket.domain.QualificationExamTicketScreenState
import com.preachooda.adminapp.section.qualificationExamTicket.repository.QualificationExamTicketRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QualificationExamTicketViewModel @Inject constructor(
    private val repository: QualificationExamTicketRepository
) : BaseViewModel<QualificationExamTicketIntent, QualificationExamTicketScreenState>() {
    private val _screenState: MutableStateFlow<QualificationExamTicketScreenState> =
        MutableStateFlow(QualificationExamTicketScreenState())
    override val screenState: StateFlow<QualificationExamTicketScreenState> = _screenState

    override fun processIntent(intent: QualificationExamTicketIntent) {
        when (intent) {
            QualificationExamTicketIntent.CloseError -> closeError()
            QualificationExamTicketIntent.CloseMessage -> closeMessage()
            is QualificationExamTicketIntent.Refresh -> loadData(intent.ticketId)
            is QualificationExamTicketIntent.FormTicket -> sendTicket(
                intent.opponent,
                intent.instructor,
                intent.startDateTime
            )
        }
    }

    private fun loadData(ticketId: Long) {
        viewModelScope.launch(Dispatchers.IO) { // get application info
            repository.loadQualificationExamApplication(ticketId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            ticket = it.data,
                            isLoading = false
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
        viewModelScope.launch(Dispatchers.IO) { // get heroes
            repository.loadHeroes().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            availableHeroes = it.data ?: emptyList(),
                            isLoading = false
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

    private fun closeError() {
        _screenState.value = _screenState.value.copy(
            isError = false
        )
    }

    private fun closeMessage() {
        _screenState.value = _screenState.value.copy(
            isMessage = false
        )
    }

    private fun sendTicket(
        opponent: Hero?,
        instructor: Hero?,
        startDateTime: String
    ) {
        if (opponent != null && instructor != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _screenState.value.ticket?.let { application ->
                    val applicationToSend = application.copy(
                        opponent = opponent,
                        instructor = instructor,
                        startDateTime = startDateTime,
                        status = ActivityStatus.ASSIGNED
                    )
                    repository.sendQualificationExamApplication(applicationToSend).collect {
                        when (it.status) {
                            Status.SUCCESS -> {
                                _screenState.value = _screenState.value.copy(
                                    ticket = it.data,
                                    isMessage = true,
                                    isLoading = false,
                                    message = "Заявка на экзамен №${it.data?.id} сформирована"
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
        } else {
            _screenState.value = _screenState.value.copy(
                isError = true,
                message = "Должны быть выбраны тренер и противник"
            )
        }
    }
}
