package com.preachooda.academyapp.section.qualificationExam.domain

import com.preachooda.domain.model.Hero

data class QualificationExamScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isMessage: Boolean = false,
    val message: String = "",
    val heroes: List<Hero> = listOf(
        Hero(
            id = 1,
            label = "Лучший Джинист",
            quirk = "Управление нитями",
        ),
        Hero(
            id = 2,
            label = "Гран Торино",
            quirk = "Реактивный",
        ),
        Hero(
            id = 3,
            label = "Старатель",
            quirk = "Адское пламя",
        ),
        Hero(
            id = 4,
            label = "Деку",
            quirk = "Один за всех",
        ),
        Hero(
            id = 5,
            label = "Уравити",
            quirk = "Невесомость",
        ),
        Hero(
            id = 6,
            label = "Сорвиголова",
            quirk = "Стирание причуд",
        )
    )
)
