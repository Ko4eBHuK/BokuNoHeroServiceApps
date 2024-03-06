package com.preachooda.domain.model

import com.google.gson.annotations.SerializedName

enum class Rate(
    val value: Int
) {
    @SerializedName("0")
    NOT_RATED(0),
    @SerializedName("1")
    ONE(1),
    @SerializedName("2")
    TWO(2),
    @SerializedName("3")
    THREE(3),
    @SerializedName("4")
    FOUR(4),
    @SerializedName("5")
    FIVE(5)
}
