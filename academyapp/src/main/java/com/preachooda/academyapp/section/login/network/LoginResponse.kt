package com.preachooda.academyapp.section.login.network

data class LoginResponse(
    val accessToken: String,
    val userId: Long,
    val entityId: Long
)
