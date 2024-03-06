package com.preachooda.adminapp.section.licenseRecall.domain

import com.preachooda.domain.model.Hero

sealed class LicenseRecallIntent {
    data object CloseError : LicenseRecallIntent()

    data object CloseMessage : LicenseRecallIntent()

    data object Refresh : LicenseRecallIntent()

    class RecallIntent(val hero: Hero?) : LicenseRecallIntent()
}
