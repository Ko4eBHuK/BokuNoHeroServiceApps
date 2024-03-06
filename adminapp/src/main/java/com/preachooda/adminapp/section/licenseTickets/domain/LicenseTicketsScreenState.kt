package com.preachooda.adminapp.section.licenseTickets.domain

import com.preachooda.domain.model.LicenseApplication

data class LicenseTicketsScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val tickets: List<LicenseApplication> = listOf()
)
