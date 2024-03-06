package com.preachooda.adminapp.section.heroesRatings.domain

sealed class HeroesRatingsIntent {
    data object CloseError : HeroesRatingsIntent()

    data object CloseMessage : HeroesRatingsIntent()

    data object Refresh : HeroesRatingsIntent()
}
