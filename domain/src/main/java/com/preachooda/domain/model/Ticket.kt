package com.preachooda.domain.model

data class Ticket(
    val id: Long = 0,
    val userId: Long = 0,
    val priority: TicketPriority = TicketPriority.ONE,
    val ticketComplexity: TicketComplexity = TicketComplexity.VERY_EASY,
    val creationDate: String = "",
    val description: String = "",
    val categories: Set<Category> = setOf(),
    val latitude: Float = .0f,
    val longitude: Float = .0f,
    val photosPaths: List<String> = listOf(),
    val videoPath: String? = "",
    val audioPath: String? = "",
    val heroes: List<Hero> = listOf(),
    val heroRates: Map<Long, Rate> = mapOf(),
    val rate: Rate = Rate.NOT_RATED,
    val status: ActivityStatus = ActivityStatus.CREATED,
    val comment: String = ""
) {
    enum class Category(
        val value: String
    ) {
        FIRE("Пожар"),
        FLOODING("Наводнение"),
        EARTHQUAKE("Землетрясение"),
        ROBBERY("Грабеж"),
        QUIRK("Причуда"),
        OTHER("Другое")
    }

    fun toKeywordsString(): String {
        return "$id\n$creationDate\n$description\n${status.value}"
    }

    override fun equals(other: Any?): Boolean {
        return other is Ticket && id == other.id
    }
}
