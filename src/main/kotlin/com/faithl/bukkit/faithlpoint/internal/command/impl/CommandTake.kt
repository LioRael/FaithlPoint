package com.faithl.bukkit.faithlpoint.internal.command.impl

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.FaithlPoint.attributes
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import com.faithl.bukkit.faithlpoint.internal.display.PointMenu
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.common5.Coerce
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

object CommandTake {
    val command = subCommand {
        dynamic(commit = "player") {
            suggestion<ProxyCommandSender> { _, _ ->
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, _, argument ->
                val player = Bukkit.getPlayerExact(sender.name) ?: return@execute
                if (FaithlPointAPI.getPoint(player).getAvailablePoints() >= Coerce.toInteger(argument)){
                    FaithlPointAPI.getPoint(player).totalPoints -= Coerce.toInteger(argument)
                    player.sendLang("Point-Took",argument)
                }else{
                    for (attr in FaithlPoint.attributes){
                        if (FaithlPointAPI.getPoint(player).getPoints(attr.key)!! >= Coerce.toInteger(argument)){
                            FaithlPointAPI.getPoint(player).takeAttribute(attr.key, Coerce.toInteger(argument))
                            FaithlPointAPI.getPoint(player).totalPoints -= Coerce.toInteger(argument)
                            player.sendLang("Point-Took-From-Attr",argument)
                            break
                        }
                    }
                }
            }
            dynamic(commit = "value") {
                restrict<ProxyCommandSender> { _, _, argument ->
                    Coerce.asInteger(argument).isPresent
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1)) ?: return@execute
                    if (FaithlPointAPI.getPoint(player).getAvailablePoints() >= Coerce.toInteger(argument)){
                        FaithlPointAPI.getPoint(player).totalPoints -= Coerce.toInteger(argument)
                        player.sendLang("Point-Took",argument)
                    }else{
                        for (attr in FaithlPoint.attributes){
                            if (FaithlPointAPI.getPoint(player).getPoints(attr.key)!! >= Coerce.toInteger(argument)){
                                FaithlPointAPI.getPoint(player).takeAttribute(attr.key, Coerce.toInteger(argument))
                                FaithlPointAPI.getPoint(player).totalPoints -= Coerce.toInteger(argument)
                                player.sendLang("Point-Took-From-Attr",argument)
                                break
                            }
                        }
                    }
                    sender.sendLang("Command-Take-Info",player.name,argument)
                }
            }
        }
    }
}