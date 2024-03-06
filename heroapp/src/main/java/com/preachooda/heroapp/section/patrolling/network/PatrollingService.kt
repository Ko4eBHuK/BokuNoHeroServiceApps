package com.preachooda.heroapp.section.patrolling.network

import com.preachooda.domain.model.Patrol
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PatrollingService {
    @GET("district-patrollings/active/{heroId}")
    suspend fun getActivePatrol(@Path("heroId") heroId: Long): Response<Patrol?>

    @PUT("district-patrollings/{patrolId}")
    suspend fun updatePatrol(
        @Path("patrolId") patrolId: Long,
        @Body body: Patrol
    ): Response<Patrol>
}
