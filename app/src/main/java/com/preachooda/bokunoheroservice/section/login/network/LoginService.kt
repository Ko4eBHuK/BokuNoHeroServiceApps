package com.preachooda.bokunoheroservice.section.login.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("auth/login")
    suspend fun loginRequest(@Body body: AuthBody): Response<LoginResponse>
}
