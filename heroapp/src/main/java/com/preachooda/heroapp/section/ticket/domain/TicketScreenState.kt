package com.preachooda.heroapp.section.ticket.domain

import com.preachooda.domain.model.Ticket

data class TicketScreenState(
    val isLoading: Boolean = false,
    val isFilesLoading: Boolean = false,
    val isError: Boolean = false,
    val isShowMessage: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val closeScreen: Boolean = false,
    val message: String = "",
    val ticket: Ticket? = null
)
