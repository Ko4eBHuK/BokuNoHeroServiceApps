package com.preachooda.bokunoheroservice.section.tickets.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.tickets.domain.AddFilterItemIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.CloseErrorIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.CloseMessageIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.RefreshTicketsIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.RemoveFilterItemIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.TicketsScreenIntent
import com.preachooda.bokunoheroservice.section.tickets.domain.TicketsScreenState
import com.preachooda.bokunoheroservice.section.tickets.repository.TicketsRepository
import com.preachooda.assets.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val repository: TicketsRepository
) : BaseViewModel<TicketsScreenIntent, TicketsScreenState>() {
    private val _screenStateFlow: MutableStateFlow<TicketsScreenState> = MutableStateFlow(
        TicketsScreenState()
    )
    override val screenState: StateFlow<TicketsScreenState> = _screenStateFlow

    init {
        buildTicketsShowList()
    }

    override fun processIntent(intent: TicketsScreenIntent) {
        when (intent) {
            is AddFilterItemIntent -> buildTicketsShowList(
                _screenStateFlow.value.filterItems.toMutableList().apply {
                    add(intent.itemValue)
                }
            )
            is RemoveFilterItemIntent -> buildTicketsShowList(
                _screenStateFlow.value.filterItems.toMutableList().apply {
                    remove(intent.itemValue)
                }
            )
            RefreshTicketsIntent -> { loadTickets() }
            CloseErrorIntent -> closeError()
            CloseMessageIntent -> closeMessage()
        }
    }

    private fun closeError() {
        _screenStateFlow.value = _screenStateFlow.value.copy(
            isError = false
        )
    }

    private fun closeMessage() {
        _screenStateFlow.value = _screenStateFlow.value.copy(
            isShowMessage = false
        )
    }

    private fun loadTickets() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getTicketsRemote().collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenStateFlow.value = _screenStateFlow.value.copy(
                                allTickets = it.data ?: emptyList(),
                                isLoading = false
                            )
                            buildTicketsShowList()
                        }
                        Status.ERROR -> {
                            _screenStateFlow.value = _screenStateFlow.value.copy(
                                isLoading = false,
                                isError = true,
                                message = it.message
                            )
                        }
                        Status.LOADING -> {
                            _screenStateFlow.value = _screenStateFlow.value.copy(
                                isLoading = true,
                                message = "Загрузка списка заявок..."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _screenStateFlow.value = _screenStateFlow.value.copy(
                    isLoading = false,
                    isError = true,
                    message = "Ошибка при получении списка заявок: ${e.message}"
                )
            }
        }
    }

    private fun buildTicketsShowList(newFilters: List<String> = _screenStateFlow.value.filterItems) {
        // TODO: ticket's status does not change after ticket send in ticket screen, but after filtering it switches
        var newTicketsShow = mutableSetOf<Ticket>()

        if (newFilters.isEmpty()) {
            newTicketsShow = _screenStateFlow.value.allTickets.toMutableSet()
        } else {
            _screenStateFlow.value.allTickets.forEach { ticket ->
                if (!newTicketsShow.contains(ticket)) run keysSearch@{
                    newFilters.forEach { filterItem ->
                        if (ticket.toKeywordsString().contains(filterItem, true)) {
                            newTicketsShow.add(ticket)
                            return@keysSearch
                        }
                    }
                }
            }
        }

        _screenStateFlow.value = _screenStateFlow.value.copy(
            filterItems = newFilters,
            ticketsShowList = newTicketsShow.toList()
        )
    }
}
