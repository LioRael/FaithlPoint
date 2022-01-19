package com.faithl.faithlpoint.internal.command.impl

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common5.Coerce
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

object CommandAttribute {

    val command = subCommand {
        dynamic("type") {
            suggestion<ProxyCommandSender> { _, _ ->
                listOf("add", "take", "clear", "addAll")
            }
            dynamic("attribute") {
                suggestion<ProxyCommandSender> { _, _ ->
                    FaithlPoint.attributes.keys.toList()
                }
                dynamic("player") {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map {
                            it.name
                        }
                    }
                    execute<ProxyCommandSender> { _, context, argument ->
                        val type = context.argument(-2)
                        val attribute = context.argument(-1)
                        val player = getProxyPlayer(argument) ?: return@execute
                        when (type) {
                            "clear" -> {
                                val conf = FaithlPoint.attributes[attribute]
                                if (!conf!!.getBoolean("Function.Shift-Right-Click", true)) {
                                    return@execute
                                }
                                if (FaithlPointAPI.condition(conf["Condition.Shift-Right-Click"], player.cast())
                                        .get()
                                ) {
                                    if (FaithlPointAPI.getPoint(player.cast())
                                            .takeAttribute(
                                                attribute,
                                                FaithlPointAPI.getPoint(player.cast()).getPoints(attribute)!!
                                            )
                                    ) {
                                        player.sendLang(
                                            "Command-Attribute-Take",
                                            player.name,
                                            attribute,
                                            FaithlPointAPI.getPoint(player.cast()).getPoints(attribute)!!
                                        )
                                    }
                                }
                            }
                            "addAll" -> {
                                val conf = FaithlPoint.attributes[attribute]
                                if (!conf!!.getBoolean("Function.Shift-Left-Click", true)) {
                                    return@execute
                                }
                                if (FaithlPointAPI.condition(conf["Condition.Shift-Left-Click"], player.cast()).get()) {
                                    if (FaithlPointAPI.getPoint(player.cast())
                                            .takeAttribute(
                                                attribute,
                                                FaithlPointAPI.getPoint(player.cast()).getAvailablePoints()
                                            )
                                    ) {
                                        player.sendLang(
                                            "Command-Attribute-Add",
                                            player.name,
                                            attribute,
                                            FaithlPointAPI.getPoint(player.cast()).getAvailablePoints()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    dynamic("point", optional = true) {
                        restrict<ProxyCommandSender> { _, _, argument ->
                            Coerce.asInteger(argument).isPresent
                        }
                        execute<ProxyCommandSender> { _, context, argument ->
                            val type = context.argument(-3)
                            val attribute = context.argument(-2)
                            val player = getProxyPlayer(context.argument(-1)) ?: return@execute
                            when (type) {
                                "add" -> {
                                    val conf = FaithlPoint.attributes[attribute]
                                    if (!conf!!.getBoolean("Function.Left-Click", true)) {
                                        return@execute
                                    }
                                    if (FaithlPointAPI.condition(conf["Condition.Left-Click"], player.cast()).get()) {
                                        if (FaithlPointAPI.getPoint(player.cast())
                                                .addAttribute(attribute, Coerce.toInteger(argument))
                                        ) {
                                            player.sendLang("Command-Attribute-Add", player.name, attribute, argument)
                                        }
                                    }
                                }
                                "take" -> {
                                    val conf = FaithlPoint.attributes[attribute]
                                    if (!conf!!.getBoolean("Function.Right-Click", true)) {
                                        return@execute
                                    }
                                    if (FaithlPointAPI.condition(conf["Condition.Right-Click"], player.cast()).get()) {
                                        if (FaithlPointAPI.getPoint(player.cast())
                                                .takeAttribute(attribute, Coerce.toInteger(argument))
                                        ) {
                                            player.sendLang("Command-Attribute-Take", player.name, attribute, argument)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}