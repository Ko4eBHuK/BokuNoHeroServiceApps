package com.preachooda.adminapp.section.licenseTickets.domain

sealed class LicenseTicketsIntent {
    data object CloseError : LicenseTicketsIntent()

    data object CloseMessage : LicenseTicketsIntent()

    data object Refresh : LicenseTicketsIntent()
}
