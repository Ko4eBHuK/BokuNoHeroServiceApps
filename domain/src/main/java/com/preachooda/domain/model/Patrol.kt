package com.preachooda.domain.model

data class Patrol(
    val id: Int? = null,
    val district: String,
    val heroes: List<Hero>,
    val status: Status = Status.PENDING,
    val scheduledStart: String,
    val scheduledEnd: String,
    val actualStart: String? = null,
    val actualEnd: String? = null
) {
    enum class Status {
        PENDING,
        STARTED,
        COMPLETED
    }
}
