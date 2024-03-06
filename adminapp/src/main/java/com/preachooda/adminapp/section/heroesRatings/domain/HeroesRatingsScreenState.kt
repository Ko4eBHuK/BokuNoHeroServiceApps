package com.preachooda.adminapp.section.heroesRatings.domain

import com.preachooda.domain.model.Hero

data class HeroesRatingsScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val heroes: List<Hero> = listOf()
)
