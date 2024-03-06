package com.preachooda.bokunoheroservice.section.ticket.domain

import com.preachooda.domain.model.Rate

sealed class TicketScreenIntent

class ShowErrorIntent(
    val errorText: String
) : TicketScreenIntent()

data object CloseErrorIntent : TicketScreenIntent()

class ShowMessageIntent(
    val message: String
) : TicketScreenIntent()

data object CloseMessageIntent : TicketScreenIntent()

data object RefreshTicketIntent : TicketScreenIntent()

data object PlayAudioIntent : TicketScreenIntent()

data object StopPlayingAudioIntent : TicketScreenIntent()

data object DeleteTicketIntent : TicketScreenIntent()

class SaveTicketIntent(
    val description: String = "",
    val comment: String = "",
    val heroRates: Map<Long, Rate> = mapOf(),
    val ticketRate: Rate = Rate.NOT_RATED
) : TicketScreenIntent()
