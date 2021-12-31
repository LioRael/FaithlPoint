package com.faithl.faithlpoint.internal.listener

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer

object DatabaseListener {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.setupDataContainer()
        FaithlPoint.menus.forEach {
            it.buildMenu(e.player)
        }
        FaithlPointAPI.updateAttribute(e.player)
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }

    @SubscribeEvent
    fun e(e: PlayerKickEvent) {
        e.player.releaseDataContainer()
    }
}