package com.preachooda.bokunoheroservice.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Rate
import com.preachooda.domain.model.Ticket

class Converters {
    @TypeConverter
    fun fromListStringToStringJson(stringList: List<String>): String =
        Gson().toJson(stringList)

    @TypeConverter
    fun fromStringJsonToListString(stringJson: String): List<String> =
        Gson().fromJson(stringJson, Array<String>::class.java).toList()

    @TypeConverter
    fun fromCollectionStringToStringJson(categorySet: Collection<String>): String =
        Gson().toJson(categorySet)

    @TypeConverter
    fun fromStringJsonToSetString(stringJson: String): Set<String> =
        Gson().fromJson(stringJson, Array<String>::class.java).toSet()

    @TypeConverter
    fun fromListHeroesToStringJson(heroesList: List<Hero>): String =
        Gson().toJson(heroesList)

    @TypeConverter
    fun fromStringJsonToListHeroes(stringJson: String): List<Hero> =
        Gson().fromJson(stringJson, Array<Hero>::class.java).toList()

    @TypeConverter
    fun fromListCategoryToStringJson(categorySet: Set<Ticket.Category>): String =
        Gson().toJson(categorySet)

    @TypeConverter
    fun fromStringJsonToSetCategory(stringJson: String): Set<Ticket.Category> =
        Gson().fromJson(stringJson, Array<Ticket.Category>::class.java).toSet()

    @TypeConverter
    fun fromMapLongRateToJsonString(map: Map<Long, Rate>): String = Gson().toJson(map)

    @TypeConverter
    fun fromStringJsonToMapLongRate(stringJson: String): Map<Long, Rate> {
        val mapType = object : TypeToken<Map<Long, Rate>>() {}.type
        return Gson().fromJson(stringJson, mapType)
    }
}
