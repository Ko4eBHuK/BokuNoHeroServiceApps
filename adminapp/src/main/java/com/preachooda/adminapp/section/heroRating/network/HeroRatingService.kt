package com.preachooda.adminapp.section.heroRating.network

import com.preachooda.domain.model.Hero
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface HeroRatingService {
    @GET("heroes/{heroId}")
    suspend fun getHero(@Path("heroId") heroId: Long): Response<Hero>

    @PUT("heroes/{heroId}")
    suspend fun editHero(
        @Path("heroId") heroId: Long,
        @Body hero: Hero
    ): Response<Hero>
}
