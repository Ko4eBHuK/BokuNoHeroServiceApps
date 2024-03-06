package com.preachooda.heroapp.section.patrolling.domain

import com.preachooda.domain.model.Patrol

data class PatrollingScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val patrol: Patrol? = null
)
