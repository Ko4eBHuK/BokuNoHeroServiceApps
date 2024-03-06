package com.preachooda.adminapp.section.heroRating.domain

import com.preachooda.domain.model.Hero

data class HeroRatingScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val hero: Hero? = null
)
