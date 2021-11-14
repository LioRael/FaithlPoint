package com.faithl.bukkit.faithlpoint.internal.listener

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.module.database.EmptyTask.forEach

object DatabaseListener{

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.setupDataContainer()
        FaithlPoint.menus.forEach{
            it.buildMenu(e.player)
        }
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