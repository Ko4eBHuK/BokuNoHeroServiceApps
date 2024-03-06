package com.preachooda.domain.model

enum class ActivityStatus(
    val value: String
) {
    CREATED("Создана"),
    ASSIGNED("Назначена"),
    IN_WORK("В работе"),
    EVALUATION("На оценке"),
    COMPLETED("Завершена"),
    REJECTED("Отказ")
}
