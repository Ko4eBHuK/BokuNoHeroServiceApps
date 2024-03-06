package com.preachooda.adminapp.section.qualificationExamTicket.domain

import com.preachooda.domain.model.Hero

sealed class QualificationExamTicketIntent {
    data object CloseError : QualificationExamTicketIntent()

    data object CloseMessage : QualificationExamTicketIntent()

    class Refresh(val ticketId: Long) : QualificationExamTicketIntent()

    class FormTicket(
        val opponent: Hero?,
        val instructor: Hero?,
        val startDateTime: String
    ) : QualificationExamTicketIntent()
}
