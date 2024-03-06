package com.preachooda.academyapp.section.qualificationExam.domain

import com.preachooda.domain.model.Hero

sealed class QualificationExamIntent {
    data object CloseError : QualificationExamIntent()

    data object CloseMessage : QualificationExamIntent()

    data object Refresh : QualificationExamIntent()

    data class SendApplication(val hero: Hero?) : QualificationExamIntent()
}
