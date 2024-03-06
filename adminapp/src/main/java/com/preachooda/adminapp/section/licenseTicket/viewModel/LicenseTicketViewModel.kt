package com.preachooda.adminapp.section.licenseTicket.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.licenseTicket.domain.LicenseTicketScreenState
import com.preachooda.adminapp.section.licenseTicket.domain.LicenseTicketIntent
import com.preachooda.adminapp.section.licenseTicket.repository.LicenseTicketRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseTicketViewModel @Inject constructor(
    private val repository: LicenseTicketRepository
) : BaseViewModel<LicenseTicketIntent, LicenseTicketScreenState>() {
    private val _screenState: MutableStateFlow<LicenseTicketScreenState> =
        MutableStateFlow(LicenseTicketScreenState())
    override val screenState: StateFlow<LicenseTicketScreenState> = _screenState

    override fun processIntent(intent: LicenseTicketIntent) {
        when (intent) {
            LicenseTicketIntent.CloseError -> closeError()
            LicenseTicketIntent.CloseMessage -> closeMessage()
            is LicenseTicketIntent.Refresh -> loadData(intent.ticketId)
            is LicenseTicketIntent.HandleApplication -> if (intent.approve) submitApplication() else declineApplication()
        }
    }

    private fun loadData(ticketId: Long) {
        // get ticket
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLicenseApplication(ticketId).collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            licenseApplication = networkCall.data
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

    private fun closeMessage() {
        _screenState.value = _screenState.value.copy(
            isMessage = false
        )
    }

    private fun submitApplication() {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.value.licenseApplication?.let {
                val handledTicket = it.copy(status = ActivityStatus.COMPLETED)
                repository.handleTicket(handledTicket).collect { networkCall ->
                    when (networkCall.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                licenseApplication = networkCall.data
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

    private fun declineApplication() {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.value.licenseApplication?.let {
                val handledTicket = it.copy(status = ActivityStatus.REJECTED)
                repository.handleTicket(handledTicket).collect { networkCall ->
                    when (networkCall.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                licenseApplication = networkCall.data
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
