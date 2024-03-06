package com.preachooda.bokunoheroservice.section.login.network

data class LoginResponse(
    val accessToken: String,
    val userId: Long,
    val entityId: Long
)
