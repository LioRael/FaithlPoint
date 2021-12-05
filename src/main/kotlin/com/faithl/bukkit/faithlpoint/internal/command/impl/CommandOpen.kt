package com.faithl.bukkit.faithlpoint.internal.command.impl

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import com.faithl.bukkit.faithlpoint.internal.display.PointMenu
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

object CommandOpen {
    val command = subCommand {
        dynamic(commit = "player") {
            suggestion<ProxyCommandSender> { _, _ ->
                onlinePlayers().map { it.name }
            }
            dynamic(commit = "menu") {
                suggestion<ProxyCommandSender> { _, _ ->
                    FaithlPoint.menus.map { it.tag!! }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1)) ?: return@execute
                    val menu = FaithlPointAPI.getMenu(argument)
                    if (menu!!.permission != "default" && !player.hasPermission(menu.permission)){
                        return@execute
                    }
                    menu.buildMenu(player).open(player)
                    sender.sendLang("Command-Open-Info")
                }
            }
        }
    }
}