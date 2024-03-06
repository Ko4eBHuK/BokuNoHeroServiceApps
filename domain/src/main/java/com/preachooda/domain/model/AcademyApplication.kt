package com.preachooda.domain.model

data class AcademyApplication(
    val id : Long? = null,
    val userId : Long,
    val printedName: String = "",
    val age: Int = -1,
    val quirk: String = "",
    val educationDocumentNumber: String = "",
    val message: String = "",
    val firstAcademy: Academy,
    val secondAcademy: Academy,
    val thirdAcademy: Academy,
    val status: ActivityStatus = ActivityStatus.CREATED,
)
