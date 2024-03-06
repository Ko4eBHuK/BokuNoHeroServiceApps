package com.preachooda.heroapp.section.home.domain

sealed class HomeScreenIntent {
    data object Logout : HomeScreenIntent()

    data object CloseErrorDialog : HomeScreenIntent()

    data object CloseMessageDialog : HomeScreenIntent()
}
