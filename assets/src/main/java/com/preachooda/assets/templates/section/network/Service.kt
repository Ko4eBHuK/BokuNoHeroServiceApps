package com.preachooda.assets.templates.section.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Service { // TODO: rename and provide it in NetworkModule
    @GET("TODO/{pathPart}") // TODO: setup
    suspend fun getMethod(@Path("pathPart") pathPart: String): Response<Any> // TODO: setup

    @POST("TODO") // TODO: setup
    suspend fun postMethod(@Body body: Any): Response<Any> // TODO: setup
}
