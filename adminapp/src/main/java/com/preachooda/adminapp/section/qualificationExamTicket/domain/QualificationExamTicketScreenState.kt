package com.preachooda.adminapp.section.qualificationExamTicket.domain

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.QualificationExamApplication

data class QualificationExamTicketScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val ticket: QualificationExamApplication? = null,
    val availableHeroes: List<Hero> = listOf()
)
