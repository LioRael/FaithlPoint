package com.faithl.faithlpoint.internal.command.impl

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.lang.sendLang

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
                    if (menu?.permission != null && !player.hasPermission(menu.permission)) {
                        return@execute
                    }
                    menu?.buildMenu(player)?.open(player) ?: return@execute
                    sender.sendLang("Command-Open-Info")
                }
            }
        }
    }
}