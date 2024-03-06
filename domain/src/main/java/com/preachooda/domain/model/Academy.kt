package com.preachooda.domain.model

data class Academy(
    val id: Int,
    val label: String,
    val address: String,
    val motto: String = "",
)
