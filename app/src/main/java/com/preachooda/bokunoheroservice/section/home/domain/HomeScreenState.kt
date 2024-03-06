package com.preachooda.bokunoheroservice.section.home.domain

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isShowMessage: Boolean = false,
    val message: String = "",
    val instructionsList: List<Instruction> = listOf()
)
