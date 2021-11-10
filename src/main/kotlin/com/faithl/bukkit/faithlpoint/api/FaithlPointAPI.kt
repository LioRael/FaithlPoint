package com.faithl.bukkit.faithlpoint.api

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.internal.display.PointMenu
import com.faithl.bukkit.faithlpoint.internal.point.Point
import org.bukkit.entity.Player
import taboolib.common.platform.Awake

@Awake
object FaithlPointAPI {
    fun getMenu(tag:String):PointMenu?{
        return FaithlPoint.menus.find {
            it.tag == tag
        }
    }

    fun getPoint(player:Player):Point{
        return if (FaithlPoint.playerPoints.find {
                it.player == player
            } !=null){
            FaithlPoint.playerPoints.find {
                it.player == player
            }!!
        }else {
            Point(player)
        }
    }
}