package com.preachooda.adminapp.section.helpTickets.domain

import com.preachooda.domain.model.Ticket

data class HelpTicketsScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val tickets: List<Ticket> = listOf()
)
