package com.preachooda.heroapp.section.login.network

data class LoginResponse(
    val accessToken: String,
    val userId: Long,
    val entityId: Long
)
