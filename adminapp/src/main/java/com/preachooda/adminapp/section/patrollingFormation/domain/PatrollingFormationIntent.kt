package com.preachooda.adminapp.section.patrollingFormation.domain

import com.preachooda.domain.model.Hero

sealed class PatrollingFormationIntent {
    data object CloseError : PatrollingFormationIntent()

    data object CloseMessage : PatrollingFormationIntent()

    class ConfirmPatrol(
        val district: String,
        val startTime: String,
        val endTime: String,
        val heroes: List<Hero>,
    ) : PatrollingFormationIntent()

    data object Refresh : PatrollingFormationIntent()
}
