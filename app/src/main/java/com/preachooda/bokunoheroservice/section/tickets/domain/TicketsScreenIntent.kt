package com.preachooda.bokunoheroservice.section.tickets.domain

sealed class TicketsScreenIntent

class AddFilterItemIntent(
    val itemValue: String
) : TicketsScreenIntent()

class RemoveFilterItemIntent(
    val itemValue: String
) : TicketsScreenIntent()

data object RefreshTicketsIntent : TicketsScreenIntent()

data object CloseErrorIntent : TicketsScreenIntent()

data object CloseMessageIntent : TicketsScreenIntent()
