package com.preachooda.adminapp.section.licenseTickets.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.licenseTickets.domain.LicenseTicketsIntent
import com.preachooda.adminapp.section.licenseTickets.domain.LicenseTicketsScreenState
import com.preachooda.adminapp.section.licenseTickets.repository.LicenseTicketsRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseTicketsViewModel @Inject constructor(
    private val repository: LicenseTicketsRepository
) : BaseViewModel<LicenseTicketsIntent, LicenseTicketsScreenState>() {
    private val _screenState: MutableStateFlow<LicenseTicketsScreenState> = MutableStateFlow(LicenseTicketsScreenState())
    override val screenState: StateFlow<LicenseTicketsScreenState> = _screenState

    override fun processIntent(intent: LicenseTicketsIntent) {
        when (intent) {
            LicenseTicketsIntent.CloseError -> closeError()
            LicenseTicketsIntent.CloseMessage -> closeMessage()
            LicenseTicketsIntent.Refresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLicenseTickets().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            tickets = it.data ?: emptyList()
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
                            isLoading = false,
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
}
