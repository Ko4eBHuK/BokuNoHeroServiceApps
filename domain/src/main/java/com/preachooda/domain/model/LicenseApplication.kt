package com.preachooda.domain.model

data class LicenseApplication(
    val id : Long? = null,
    val printedName: String = "",
    val heroName: String = "",
    val quirk: String = "",
    val birthDate: String = "",
    val educationDocumentNumber: String,
    val status: ActivityStatus = ActivityStatus.CREATED
)
