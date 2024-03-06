package com.preachooda.heroapp.section.ticket.domain

import com.preachooda.domain.model.Rate
import com.preachooda.domain.model.Ticket

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

data object SaveTicketIntent : TicketScreenIntent()
