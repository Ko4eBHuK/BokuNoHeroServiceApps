package com.preachooda.adminapp.section.patrollingFormation.network

import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Patrol
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PatrollingFormationService {
    @GET("heroes/free")
    suspend fun getAvailableHeroes(): Response<List<Hero>>

    @POST("district-patrollings")
    suspend fun postPatrol(@Body patrol: Patrol): Response<Patrol>
}
