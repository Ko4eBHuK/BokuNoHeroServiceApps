package com.preachooda.adminapp.section.helpTicket.domain

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Ticket

data class HelpTicketScreenState(
    val isLoading: Boolean = false,
    val isFilesLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val ticket: Ticket? = null,
    val availableHeroes: List<Hero> = listOf(),
    val suitableHeroes: List<Hero> = listOf(),
    val useAutoHeroes: Boolean = false,
    val isAudioPlaying: Boolean = false
)
