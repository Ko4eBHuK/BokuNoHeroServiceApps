package com.preachooda.adminapp.section.qualificationExamTickets.domain

import com.preachooda.domain.model.QualificationExamApplication

data class QualificationExamTicketsScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val examApplications: List<QualificationExamApplication> = listOf()
)
