package com.preachooda.bokunoheroservice.section.login.domain

data class LoginScreenState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isShowMessage: Boolean = false,
    val message: String = "",
    val authComplete: Boolean = false
)
