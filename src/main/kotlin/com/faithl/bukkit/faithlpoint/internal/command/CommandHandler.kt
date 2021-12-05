package com.faithl.bukkit.faithlpoint.internal.command

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.internal.command.impl.CommandAdd
import com.faithl.bukkit.faithlpoint.internal.command.impl.CommandOpen
import com.faithl.bukkit.faithlpoint.internal.command.impl.CommandReload
import com.faithl.bukkit.faithlpoint.internal.command.impl.CommandTake
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText


@CommandHeader(name = "faithlpoint", aliases = ["fpoint","point"], permission = "FaithlPoint.access")
object CommandHandler {
    @CommandBody
    val reload = CommandReload.command

    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val open = CommandOpen.command

    @CommandBody
    val add = CommandAdd.command

    @CommandBody
    val take = CommandTake.command

    @CommandBody
    val main = mainCommand{
        execute<CommandSender> { sender, _, argument ->
            if (argument.isEmpty()) {
                generateMainHelper(sender)
                return@execute
            }
            incorrectCommand { _, _, _, _ ->
                sender.sendMessage("§8[§3FaithlPoint§8] §8[§cERROR§8] Args §6$argument §3not found.")
                TellrawJson()
                    .append("§8[§3FaithlPoint§8] §8[§bINFO§8] Type ").append("§f/FaithlPoint help")
                    .hoverText("§f/FaithlPoint help §8- §7more help...")
                    .suggestCommand("/FaithlPoint help")
                    .append("§3 for help.")
                    .sendTo(adaptCommandSender(sender))
            }
        }
    }

    @CommandBody
    val help = subCommand{
        execute<CommandSender> { sender, _, _ ->
            generateMainHelper(sender)
        }
    }

    private fun generateMainHelper(sender: CommandSender){
        val proxySender = adaptCommandSender(sender)
        proxySender.sendMessage("")
        TellrawJson()
            .append("  ").append("§3FaithlPoint")
            .hoverText("§7FaithlPoint is modern and advanced Minecraft point-plugin")
            .append(" ").append("§f${FaithlPoint.plugin.description.version}")
            .hoverText("""
                §7Plugin version: §2${FaithlPoint.plugin.description.version}
            """.trimIndent()).sendTo(proxySender)
        proxySender.sendMessage("")
        TellrawJson()
            .append("  §7${sender.asLangText("Command-Help-Type")}: ").append("§f/FaithlPoint §8[...]")
            .hoverText("§f/FaithlPoint §8[...]")
            .suggestCommand("/FaithlPoint ")
            .sendTo(proxySender)
        proxySender.sendMessage("  §7${sender.asLangText("Command-Help-Args")}:")
        fun displayArg(name: String, desc: String) {
            TellrawJson()
                .append("    §8- ").append("§f$name")
                .hoverText("§f/FaithlPoint $name §8- §7$desc")
                .suggestCommand("/FaithlPoint $name ")
                .sendTo(proxySender)
            proxySender.sendMessage("      §7$desc")
        }
        displayArg("open", sender.asLangText("Command-Open-Description"))
        displayArg("add", sender.asLangText("Command-Add-Description"))
        displayArg("take", sender.asLangText("Command-Take-Description"))
        displayArg("reload", sender.asLangText("Command-Reload-Description"))
        proxySender.sendMessage("")
    }
}