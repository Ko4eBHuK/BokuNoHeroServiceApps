package com.preachooda.adminapp.section.licenseTicket.domain

sealed class LicenseTicketIntent {
    data object CloseError : LicenseTicketIntent()

    data object CloseMessage : LicenseTicketIntent()

    class Refresh(val ticketId: Long) : LicenseTicketIntent()

    class HandleApplication(val approve: Boolean) : LicenseTicketIntent()
}
