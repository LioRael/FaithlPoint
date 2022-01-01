package com.faithl.faithlpoint.api

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.internal.display.PointMenu
import com.faithl.faithlpoint.internal.point.Point
import com.faithl.milim.MilimAPI
import org.bukkit.entity.Player
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce

object FaithlPointAPI {
    fun getMenu(tag: String): PointMenu? {
        return FaithlPoint.menus.find {
            it.tag == tag
        }
    }

    fun getPoint(player: Player): Point {
        return if (FaithlPoint.playerPoints.find {
                it.player == player
            } != null) {
            FaithlPoint.playerPoints.find {
                it.player == player
            }!!
        } else {
            Point(player)
        }
    }

    fun updateAttribute(player: Player) {
        submit {
            val map = HashMap<String, Array<Number>>()
            val points = getPoint(player).points
            FaithlPoint.attributes.forEach { (key, value) ->
                value.getKeys(false).forEach {
                    val args = value.getString(it)!!.split("-")
                    map[it] = arrayOf(
                        Coerce.toDouble(args[0]) * points[key]!!,
                        Coerce.toDouble(args.getOrElse(1) { args[0] }) * points[key]!!
                    )
                }
            }
            MilimAPI.setAttribute("faithl_point", player, map)
        }
    }
}