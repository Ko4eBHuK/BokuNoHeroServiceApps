package com.preachooda.academyapp.section.home.domain

sealed class HomeScreenIntent {
    data object Logout : HomeScreenIntent()

    data object CloseErrorDialog : HomeScreenIntent()

    data object CloseMessageDialog : HomeScreenIntent()
}
