package com.preachooda.adminapp.section.licenseRecall.domain

import com.preachooda.domain.model.Hero

data class LicenseRecallScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val heroes: List<Hero> = listOf()
)
