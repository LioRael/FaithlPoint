package com.faithl.faithlpoint.api

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.internal.display.PointMenu
import com.faithl.faithlpoint.internal.point.Point
import com.faithl.milim.MilimAPI
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

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
            //val map = HashMap<String, Array<Number>>()
            val list = arrayListOf<String>()
            val points = getPoint(player).points
            FaithlPoint.attributes.forEach { (key, value) ->
                value.getConfigurationSection("Attributes")?.getKeys(false)?.forEach {
                    //val args = value.getString("Attributes.${it}")?.split("-")
//                    map[it] = arrayOf(
//                        Coerce.toDouble(args?.get(0)) * points[key]!!,
//                        Coerce.toDouble(args?.getOrElse(1) { args[0] }) * points[key]!!
//                    )
                    val args = value.getString("Attributes.$it")
                    val num = Coerce.toDouble(args) * points[key]!!
                    list.add("$it: $num")
                }
            }

            MilimAPI.setAttribute("FaithlPoint", player, list)
        }
    }

    fun condition(conf: Any?, player: Player): CompletableFuture<Boolean> {
        return if (conf != null) {
            try {
                return KetherShell.eval(
                    source = conf.asList(),
                    sender = adaptPlayer(player),
                    namespace = listOf("faithlpoint", "faithlpoint-internal")
                ).thenApply { result ->
                    Coerce.toBoolean(result)
                }
            } catch (e: Throwable) {
                e.printKetherErrorMessage()
                CompletableFuture.completedFuture(false)
            }
        } else {
            CompletableFuture.completedFuture(true)
        }
    }
}