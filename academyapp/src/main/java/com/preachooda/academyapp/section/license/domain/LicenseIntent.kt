package com.preachooda.academyapp.section.license.domain

sealed class LicenseIntent {
    data object CloseError : LicenseIntent()

    data object CloseMessage : LicenseIntent()

    data class RegisterLicenseApplication(
        val printedName: String,
        val heroName: String,
        val quirkName: String,
        val birthDate: String,
        val educationDocumentNumber: String,
    ) : LicenseIntent()
}
