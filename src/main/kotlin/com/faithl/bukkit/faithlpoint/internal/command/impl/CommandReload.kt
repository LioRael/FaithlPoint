package com.faithl.bukkit.faithlpoint.internal.command.impl

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.internal.conf.Loader
import com.faithl.bukkit.faithlpoint.internal.display.PointMenu
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang

object CommandReload {
    val command = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            for (player in onlinePlayers()){
                if (FaithlPoint.playerReceptacle[player.cast()] != null){
                    FaithlPoint.playerReceptacle[player.cast()]!!.close()
                }
            }
            Language.reload()
            FaithlPoint.setting.reload()
            FaithlPoint.menus.clear()
            FaithlPoint.attributes.clear()
            FaithlPoint.init()
            Loader.loadLevels(sender.cast())
            sender.sendLang("Plugin-Reloaded", pluginVersion,KotlinVersion.CURRENT.toString())
        }
    }
}