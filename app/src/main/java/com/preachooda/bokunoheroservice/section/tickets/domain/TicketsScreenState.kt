package com.preachooda.bokunoheroservice.section.tickets.domain

import com.preachooda.domain.model.Ticket

data class TicketsScreenState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isShowMessage: Boolean = false,
    val message: String = "",
    val allTickets: List<Ticket> = listOf(),
    val ticketsShowList: List<Ticket> = listOf(),
    val filterItems: List<String> = listOf(),
)
