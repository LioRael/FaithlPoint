package com.faithl.faithlpoint.util

import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.expansion.getDataContainer
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer


fun Player.getTotalPoints(): Int {
    releaseDataContainer()
    setupDataContainer()
    return Coerce.toInteger(getDataContainer()["Points_Total"])
}

fun Player.getPoints(type: String): Int {
    releaseDataContainer()
    setupDataContainer()
    if (getDataContainer()[type] == null) {
        getDataContainer()[type] = 0
    }
    return Coerce.toInteger(getDataContainer()[type])
}

fun Player.setTotalPoints(totalPoints: Int) {
    releaseDataContainer()
    setupDataContainer()
    getDataContainer()["Points_Total"] = totalPoints
}

fun Player.setPoints(type: String, value: Int) {
    releaseDataContainer()
    setupDataContainer()
    getDataContainer()[type] = value
}

