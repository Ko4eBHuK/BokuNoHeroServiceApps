package com.preachooda.academyapp.section.applications.domain

sealed class ApplicationsIntent {
    data object CloseError : ApplicationsIntent()

    data object CloseMessage : ApplicationsIntent()

    data object Refresh : ApplicationsIntent()
}
