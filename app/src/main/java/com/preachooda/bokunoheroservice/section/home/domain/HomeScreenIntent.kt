package com.preachooda.bokunoheroservice.section.home.domain

sealed class HomeScreenIntent {
    data object Sos : HomeScreenIntent()

    data object Logout : HomeScreenIntent()

    data object CloseErrorDialog : HomeScreenIntent()

    data object CloseMessageDialog : HomeScreenIntent()
}
