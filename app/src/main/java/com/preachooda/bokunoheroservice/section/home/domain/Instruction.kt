package com.preachooda.bokunoheroservice.section.home.domain

data class Instruction(
    val id: Int,
    val label: String,
    val contentText: String = "",
    val filesPaths: List<String> = listOf()
)
