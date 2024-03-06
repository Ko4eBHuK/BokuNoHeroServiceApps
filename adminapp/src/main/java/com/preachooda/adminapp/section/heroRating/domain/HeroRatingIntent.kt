package com.preachooda.adminapp.section.heroRating.domain

sealed class HeroRatingIntent {
    data object CloseError : HeroRatingIntent()

    data object CloseMessage : HeroRatingIntent()

    class Refresh(val heroId: Long) : HeroRatingIntent()

    class Submit(val newRating: Float) : HeroRatingIntent()
}
