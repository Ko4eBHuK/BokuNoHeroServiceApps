package com.preachooda.adminapp.section.patrollingFormation.domain

import com.preachooda.domain.model.Hero

data class PatrollingFormationScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val closeScreen: Boolean = false,
    val message: String = "",
    val availableHeroes: List<Hero> = listOf()
)
