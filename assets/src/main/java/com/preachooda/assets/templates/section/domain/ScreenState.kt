package com.preachooda.assets.templates.section.domain

data class ScreenState( // TODO: rename
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    // TODO: add fields
)
