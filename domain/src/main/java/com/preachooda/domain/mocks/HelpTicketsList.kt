package com.preachooda.domain.mocks

import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Rate
import com.preachooda.domain.model.Ticket
import com.preachooda.domain.model.Tier

val ticketTestPool = listOf(
    Ticket(
        id = 450,
        description = "hackintosh Питер",
        creationDate = "created now",
        categories = setOf(Ticket.Category.QUIRK, Ticket.Category.ROBBERY),
        latitude = 59.96672f,
        longitude = 30.31001f,
        status = ActivityStatus.CREATED
    ),
    Ticket(
        id = 67,
        description = "kali linux Мацумото",
        creationDate = "created yesterday",
        categories = setOf(Ticket.Category.FLOODING),
        latitude = 36.22401f,
        longitude = 137.95030f,
        status = ActivityStatus.IN_WORK,
        heroes = listOf(
            Hero(
                id = 1042,
                label = "Лучший Джинист",
                quirk = "Управление нитями",
                skillByQuirk = Tier.SS,
                rankingPosition = 15
            ),
            Hero(
                id = 3,
                label = "Гран Торино",
                quirk = "Реактивный",
                skillByQuirk = Tier.A,
                rankingPosition = 172
            ),
        )
    ),
    Ticket(
        id = 13,
        description = "btw i use arch Huron USA",
        creationDate = "created hour ago",
        categories = setOf(Ticket.Category.FIRE),
        latitude = 44.37071f,
        longitude = -98.29526f,
        status = ActivityStatus.EVALUATION,
        heroes = listOf(
            Hero(
                id = 1042,
                label = "Лучший Джинист",
                quirk = "Управление нитями",
                skillByQuirk = Tier.SS
            ),
            Hero(
                id = 3,
                label = "Гран Торино",
                quirk = "Реактивный",
                skillByQuirk = Tier.A
            ),
        ),
        comment = "valuation comment"
    ),
    Ticket(
        id = 4788,
        description = "btw i use astra Irkutsk",
        creationDate = "created hour ago",
        categories = Ticket.Category.entries.toSet(),
        latitude = 52.26708f,
        longitude = 104.25876f,
        status = ActivityStatus.COMPLETED,
        rate = Rate.FIVE,
        heroes = listOf(
            Hero(
                id = 1042,
                label = "Лучший Джинист",
                quirk = "Управление нитями",
                skillByQuirk = Tier.SS
            ),
            Hero(
                id = 3,
                label = "Гран Торино",
                quirk = "Реактивный",
                skillByQuirk = Tier.A
            ),
        ),
        heroRates = mapOf(
            1042L to Rate.FOUR,
            3L to Rate.FIVE
        ),
        comment = "COMPLETED comment"
    ),
    Ticket(
        id = 4,
        description = "btw i use gentoo1",
        creationDate = "created now",
        status = ActivityStatus.REJECTED,
        comment = "Заявка невалидна, попробуйте создать новую."
    ),
    Ticket(
        id = 5,
        description = "btw i use gentoo2",
        creationDate = "created now",
        status = ActivityStatus.REJECTED,
        comment = "Заявка невалидна, попробуйте создать новую."
    ),
    Ticket(
        id = 6,
        description = "btw i use gentoo3",
        creationDate = "created now",
        status = ActivityStatus.REJECTED,
        comment = "Заявка невалидна, попробуйте создать новую."
    )
)
