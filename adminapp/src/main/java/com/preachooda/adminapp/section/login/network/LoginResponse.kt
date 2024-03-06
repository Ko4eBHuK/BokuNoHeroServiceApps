package com.preachooda.adminapp.section.login.network

data class LoginResponse(
    val accessToken: String,
    val userId: Long,
    val entityId: Long
)
