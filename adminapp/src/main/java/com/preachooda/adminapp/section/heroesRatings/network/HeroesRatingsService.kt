package com.preachooda.adminapp.section.heroesRatings.network

import com.preachooda.domain.model.Hero
import retrofit2.Response
import retrofit2.http.GET

interface HeroesRatingsService {
    @GET("heroes")
    suspend fun getHeroes(): Response<List<Hero>>
}
