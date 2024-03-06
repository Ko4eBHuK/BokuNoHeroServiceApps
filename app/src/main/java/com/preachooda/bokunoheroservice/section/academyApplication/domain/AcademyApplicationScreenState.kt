package com.preachooda.bokunoheroservice.section.academyApplication.domain

import com.preachooda.domain.model.Academy

data class AcademyApplicationScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val academies: List<Academy> = emptyList(),
    val closeScreen: Boolean = false,
)
