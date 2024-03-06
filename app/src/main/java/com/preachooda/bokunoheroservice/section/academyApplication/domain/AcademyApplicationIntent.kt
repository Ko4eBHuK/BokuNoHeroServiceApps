package com.preachooda.bokunoheroservice.section.academyApplication.domain

import com.preachooda.domain.model.Academy

sealed class AcademyApplicationIntent {
    data object CloseError : AcademyApplicationIntent()

    data object CloseMessage : AcademyApplicationIntent()

    class SendApplication(
        val printedName: String = "",
        val age: Int = -1,
        val quirk: String = "",
        val educationDocumentNumber: String = "",
        val message: String = "",
        val firstAcademy: Academy?,
        val secondAcademy: Academy?,
        val thirdAcademy: Academy?
    ) : AcademyApplicationIntent()

    data object Refresh : AcademyApplicationIntent()
}
