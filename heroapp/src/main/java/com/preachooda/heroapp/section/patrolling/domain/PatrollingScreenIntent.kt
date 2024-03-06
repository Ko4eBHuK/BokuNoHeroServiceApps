package com.preachooda.heroapp.section.patrolling.domain

sealed class PatrollingScreenIntent {
    data object CloseError : PatrollingScreenIntent()

    class ShowError(val message: String) : PatrollingScreenIntent()

    data object CloseMessage : PatrollingScreenIntent()

    data object Refresh : PatrollingScreenIntent()

    data object StartPatrolling : PatrollingScreenIntent()

    data object FinishPatrolling : PatrollingScreenIntent()
}
