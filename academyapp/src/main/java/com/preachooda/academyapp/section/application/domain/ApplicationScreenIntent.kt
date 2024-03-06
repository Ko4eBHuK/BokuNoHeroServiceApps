package com.preachooda.academyapp.section.application.domain

sealed class ApplicationScreenIntent {
    data object CloseError : ApplicationScreenIntent()

    data object CloseMessage : ApplicationScreenIntent()

    class Refresh(val applicationId: Long) : ApplicationScreenIntent()

    data object HandleApplication : ApplicationScreenIntent()
}
