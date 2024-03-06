package com.preachooda.adminapp.section.qualificationExamTickets.domain

sealed class QualificationExamTicketsIntent {
    data object CloseError : QualificationExamTicketsIntent()

    data object CloseMessage : QualificationExamTicketsIntent()

    data object Refresh : QualificationExamTicketsIntent()
}
