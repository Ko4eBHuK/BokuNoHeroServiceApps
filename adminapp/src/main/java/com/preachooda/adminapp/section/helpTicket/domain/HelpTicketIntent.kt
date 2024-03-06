package com.preachooda.adminapp.section.helpTicket.domain

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.TicketComplexity
import com.preachooda.domain.model.TicketPriority

sealed class HelpTicketIntent {
    data object CloseError : HelpTicketIntent()
    class ShowErrorIntent(val message: String) : HelpTicketIntent()
    data object CloseMessage : HelpTicketIntent()
    class Refresh(val ticketId: Long) : HelpTicketIntent()
    class ConfirmTicket(
        val heroes: List<Hero>,
        val complexity: TicketComplexity,
        val priority: TicketPriority
    ) : HelpTicketIntent()
    data object RejectTicket : HelpTicketIntent()
    data object StopPlayingAudioIntent : HelpTicketIntent()
    data object PlayAudioIntent : HelpTicketIntent()
    class AutoHeroes(
        val complexity: TicketComplexity,
        val priority: TicketPriority
    ) : HelpTicketIntent()
    data object CloseAutoHeroes : HelpTicketIntent()
}
