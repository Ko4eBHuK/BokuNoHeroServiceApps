package com.preachooda.domain.model

data class Hero(
    val id: Long = 0,
    val userId: Long = 0,
    val label: String = "",
    val quirk: String = "",
    val quirkType: QuirkType = QuirkType.REINFORCEMENT,
    val skillByQuirk: Tier = Tier.F,
    val rankingPosition: Long = -1,
    val rating: Float = 0.0f,
    val strength: Int = 0,
    val speed: Int = 0,
    val technique: Int = 0,
    val intelligence: Int = 0,
    val cooperation: Int = 0
)
