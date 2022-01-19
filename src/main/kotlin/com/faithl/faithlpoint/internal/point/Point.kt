package com.faithl.faithlpoint.internal.point

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.FaithlPoint.attributes
import com.faithl.faithlpoint.api.FaithlPointAPI
import com.faithl.faithlpoint.util.getPoints
import com.faithl.faithlpoint.util.getTotalPoints
import com.faithl.faithlpoint.util.setPoints
import com.faithl.faithlpoint.util.setTotalPoints
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

class Point(val player: Player) {

    var totalPoints: Int
        get() = player.getTotalPoints()
        set(value) = player.setTotalPoints(value)
    var points = mutableMapOf<String, Int>()

    init {
        FaithlPoint.playerPoints.add(this)
        if (attributes.isEmpty()) {
            FaithlPoint.playerPoints.remove(this)
        }
        attributes.forEach {
            points[it.key] = player.getPoints(it.key)
        }
    }

    fun getAvailablePoints(): Int {
        var availablePoints: Int = totalPoints
        FaithlPoint.attributes.forEach {
            if (points[it.key] == null) {
                points[it.key] = 0
            }
            availablePoints -= points[it.key]!!
        }
        return availablePoints
    }

    fun getPoints(tag: String): Int? {
        return if (points[tag] != null) {
            points[tag]
        } else {
            points[tag] = 0
            points[tag]
        }
    }

    fun addAttribute(attribute: String, add: Int): Boolean {
        if (getAvailablePoints() >= add) {
            points[attribute] = points.getOrPut(attribute) {
                0
            } + add
        } else {
            player.sendLang("Point-Not-Enough")
            return false
        }
        save()
        FaithlPointAPI.updateAttribute(player)
        return true
    }

    fun takeAttribute(attribute: String, value: Int): Boolean {
        if ((points[attribute] ?: 0) - value >= 0) {
            points[attribute] = (points[attribute] ?: 0) - value
        } else {
            player.sendLang("Point-Not-Return")
            return false
        }
        save()
        FaithlPointAPI.updateAttribute(player)
        return true
    }

    private fun save() {
        points.forEach {
            player.setPoints(it.key, it.value)
        }
        player.setTotalPoints(totalPoints)
    }
}