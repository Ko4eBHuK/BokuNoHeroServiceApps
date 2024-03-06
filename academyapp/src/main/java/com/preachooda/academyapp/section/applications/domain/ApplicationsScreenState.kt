package com.preachooda.academyapp.section.applications.domain

import com.preachooda.domain.model.AcademyApplication

data class ApplicationsScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val applications: List<AcademyApplication> = listOf()
)
