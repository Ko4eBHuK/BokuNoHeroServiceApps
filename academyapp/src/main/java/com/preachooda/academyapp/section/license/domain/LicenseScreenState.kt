package com.preachooda.academyapp.section.license.domain

data class LicenseScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = ""
)
