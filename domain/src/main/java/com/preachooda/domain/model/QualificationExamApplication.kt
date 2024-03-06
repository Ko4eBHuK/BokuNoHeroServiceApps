package com.preachooda.domain.model

data class QualificationExamApplication(
    val id: Int? = null,
    val academyId: Long,
    val hero: Hero,
    val status: ActivityStatus = ActivityStatus.CREATED,
    val opponent: Hero? = null,
    val instructor: Hero? = null,
    val startDateTime: String? = null
)
