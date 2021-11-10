package com.faithl.bukkit.faithlpoint.internal.point

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.FaithlPoint.attributes
import com.faithl.bukkit.faithlpoint.util.getPoints
import com.faithl.bukkit.faithlpoint.util.getTotalPoints
import com.faithl.bukkit.faithlpoint.util.setPoints
import com.faithl.bukkit.faithlpoint.util.setTotalPoints
import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.util.sendLang

class Point(val player:Player) {
    var totalPoints:Int
        get() = player.getTotalPoints()
        set(value) = player.setTotalPoints(value)
    var points = mutableMapOf<String,Int>()

    init {
        FaithlPoint.playerPoints.add(this)
        if (attributes.isEmpty()){
            FaithlPoint.playerPoints.remove(this)
        }
        FaithlPoint.attributes.forEach {
            points[it.key] = player.getPoints(it.key)
        }
    }

    fun getAvailablePoints():Int {
        var availablePoints:Int = totalPoints
        FaithlPoint.attributes.forEach {
            if (points[it.key] == null){
                points[it.key] = 0
            }
            availablePoints -= points[it.key]!!
        }
        return availablePoints
    }

    fun getPoints(tag:String):Int?{
        return if (points[tag] != null){
            points[tag]
        } else {
            points[tag] = 0
            points[tag]
        }
    }

    fun addAttribute(attribute:String,add:Int){
        if (getAvailablePoints() >= add){
            points[attribute] = points.getOrPut(attribute) {
                0
            } + add
        }else
            player.sendLang("Point-Not-Enough")
        save()
    }

    fun takeAttribute(attribute:String,value:Int){
        if ((points[attribute] ?: 0) - value >= 0)
            points[attribute] = (points[attribute]?: 0) - value
        else
            player.sendLang("Point-Not-Return")
        save()
    }

    private fun save(){
        points.forEach {
            player.setPoints(it.key,it.value)
        }
        player.setTotalPoints(totalPoints)
    }
}