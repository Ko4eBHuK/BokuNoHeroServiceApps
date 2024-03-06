package com.preachooda.academyapp.section.application.domain

import com.preachooda.domain.model.AcademyApplication

data class ApplicationScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val application: AcademyApplication? = null
)
