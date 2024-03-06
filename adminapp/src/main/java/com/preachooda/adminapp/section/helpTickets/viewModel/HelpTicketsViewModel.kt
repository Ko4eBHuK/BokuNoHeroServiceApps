package com.preachooda.adminapp.section.helpTickets.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.helpTickets.domain.HelpTicketsIntent
import com.preachooda.adminapp.section.helpTickets.domain.HelpTicketsScreenState
import com.preachooda.adminapp.section.helpTickets.repository.HelpTicketsRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpTicketsViewModel @Inject constructor(
    private val repository: HelpTicketsRepository
) : BaseViewModel<HelpTicketsIntent, HelpTicketsScreenState>() {
    private val _screenState: MutableStateFlow<HelpTicketsScreenState> = MutableStateFlow(HelpTicketsScreenState())
    override val screenState: StateFlow<HelpTicketsScreenState> = _screenState

    override fun processIntent(intent: HelpTicketsIntent) {
        when (intent) {
            HelpTicketsIntent.CloseError -> closeError()
            HelpTicketsIntent.CloseMessage -> closeMessage()
            HelpTicketsIntent.Refresh -> loadData()
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

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHelpTickets().collect {
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
}
