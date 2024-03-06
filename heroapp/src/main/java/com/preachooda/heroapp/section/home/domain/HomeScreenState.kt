package com.preachooda.heroapp.section.home.domain

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isShowMessage: Boolean = false,
    val message: String = ""
)
