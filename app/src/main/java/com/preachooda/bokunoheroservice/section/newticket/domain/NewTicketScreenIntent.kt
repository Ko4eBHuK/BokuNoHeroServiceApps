package com.preachooda.bokunoheroservice.section.newticket.domain

import com.preachooda.domain.model.Ticket

sealed class NewTicketScreenIntent

data object Init : NewTicketScreenIntent()

class CreateTicketIntent(
    val ticketDescription: String
) : NewTicketScreenIntent()

class CloseCreation(
    val save: Boolean,
    val ticketDescription: String = ""
) : NewTicketScreenIntent()

class SelectCategoryIntent(
    val category: Ticket.Category
) : NewTicketScreenIntent()

class ShowErrorIntent(
    val errorText: String
) : NewTicketScreenIntent()

data object CloseErrorIntent : NewTicketScreenIntent()

data object CloseMessageIntent : NewTicketScreenIntent()

data object GetLocationIntent : NewTicketScreenIntent()

class AddPhotoPathIntent(
    val absolutePath: String
) : NewTicketScreenIntent()

class DeletePhotoIntent(
    val position: Int
) : NewTicketScreenIntent()

class AddVideoPathIntent(
    val absolutePath: String
) : NewTicketScreenIntent()

data object DeleteVideoIntent : NewTicketScreenIntent()

data object RecordAudioIntent : NewTicketScreenIntent()

data object StopAudioRecordingIntent : NewTicketScreenIntent()

data object PlayAudioIntent : NewTicketScreenIntent()

data object StopPlayingAudioIntent : NewTicketScreenIntent()

data object DeleteAudioIntent : NewTicketScreenIntent()
