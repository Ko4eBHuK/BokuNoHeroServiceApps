package com.preachooda.adminapp.section.helpTickets.domain

sealed class HelpTicketsIntent {
    data object CloseError : HelpTicketsIntent()

    data object CloseMessage : HelpTicketsIntent()

    data object Refresh : HelpTicketsIntent()
}
