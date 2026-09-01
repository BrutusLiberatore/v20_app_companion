package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MoralPath(
    val pathId: String = "humanity",
    val pathNameId: String = "path.humanity",
    val conscienceValue: Int = 1,
    val selfControlValue: Int = 1,
    val courageValue: Int = 1,
    val convictionValue: Int = 0,
    val instinctValue: Int = 0
) {
    val humanity: Int
        get() = conscienceValue + selfControlValue

    val willpower: Int
        get() = courageValue

    val pathTrait1: Int
        get() = if (pathId == "humanity" || pathId == "path.humanity") conscienceValue else convictionValue

    val pathTrait2: Int
        get() = if (pathId == "humanity" || pathId == "path.humanity") selfControlValue else instinctValue
}
