package com.preachooda.adminapp.section.licenseTicket.domain

import com.preachooda.domain.model.LicenseApplication

data class LicenseTicketScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val licenseApplication: LicenseApplication? = null
)
