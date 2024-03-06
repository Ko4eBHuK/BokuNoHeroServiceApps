package com.preachooda.bokunoheroservice.section.login.domain

sealed class LoginScreenIntent

data object CloseErrorIntent : LoginScreenIntent()

data object CloseMessageIntent : LoginScreenIntent()

data class AuthenticateIntent(
    val username: String,
    val password: String
) : LoginScreenIntent()
