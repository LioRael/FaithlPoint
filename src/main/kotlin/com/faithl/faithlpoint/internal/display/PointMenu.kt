package com.faithl.faithlpoint.internal.display

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.XSound
import taboolib.module.chat.colored
import taboolib.module.lang.TypeActionBar
import taboolib.module.ui.receptacle.*
import taboolib.module.ui.receptacle.ReceptacleClickType.*
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.buildItem

/**
 * Point menu
 *
 * @property conf
 * @constructor Create empty Point menu
 */
class PointMenu(private val conf: ConfigurationSection) {

    val tag: String? = conf.getString("Tag")
    val title: String? = conf.getString("Title")
    private val rows: Int = conf.getInt("Rows")
    private val layout: MutableList<String> = conf.getStringList("Layout").toMutableList()
    var slots = ArrayList<List<Char>>()
    var items = HashMap<Char, ItemStack>()
    val permission: String? = conf.getString("Permission")

    fun buildMenu(player: Player) = buildReceptacle(title ?: "&b加点系统".colored(), rows) {
        slots.clear()
        items.clear()
        layout.forEach { slot ->
            slots.addAll(listOf(slot.toCharArray().toList()))
        }
        build(this, player)
        FaithlPoint.playerReceptacle[player] = this
        onClick { player, event ->
            player.updateInventory()
            event.isCancelled = true
            items.forEach {
                if (event.itemStack == it.value) {
                    val type = conf.getString("Item.${it.key}.Type")
                    val actions = conf.getStringList("Item.${it.key}.Actions").toMutableList()
                    if (type == "Item") {
                        refresh(event.slot)
                        val map = mutableMapOf(
                            "{player}" to player.name,
                            "{totalPoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).totalPoints),
                            "{availablePoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).getAvailablePoints())
                        )
                        doActions(actions, map, player, it.key.toString())
                    }
                    if (type == "Attribute") {
                        val tag = conf.getString("Item.${it.key}.AttributeTag")
                        when (event.receptacleClickType) {
                            LEFT -> {
                                if (conf.getBoolean("Item.${it.key}.Function.Left-Click", true)) {
                                    if (FaithlPointAPI.condition(conf["Item.${it.key}.Condition.Left-Click"], player)
                                            .get()
                                    ) {
                                        FaithlPointAPI.getPoint(player).addAttribute(tag!!, 1)
                                    }
                                }
                            }
                            RIGHT -> {
                                if (conf.getBoolean("Item.${it.key}.Function.Right-Click", true)) {
                                    if (FaithlPointAPI.condition(conf["Item.${it.key}.Condition.Right-Click"], player)
                                            .get()
                                    ) {
                                        FaithlPointAPI.getPoint(player).takeAttribute(tag!!, 1)
                                    }
                                }
                            }
                            SHIFT_LEFT -> {
                                if (conf.getBoolean("Item.${it.key}.Function.Shift-Left-Click", true)) {
                                    if (FaithlPointAPI.condition(
                                            conf["Item.${it.key}.Condition.Shift-Left-Click"],
                                            player
                                        ).get()
                                    ) {
                                        FaithlPointAPI.getPoint(player)
                                            .addAttribute(tag!!, FaithlPointAPI.getPoint(player).getAvailablePoints())
                                    }
                                }
                            }
                            SHIFT_RIGHT -> {
                                if (conf.getBoolean("Item.${it.key}.Function.Shift-Right-Click", true)) {
                                    if (FaithlPointAPI.condition(
                                            conf["Item.${it.key}.Condition.Shift-Right-Click"],
                                            player
                                        ).get()
                                    ) {
                                        FaithlPointAPI.getPoint(player)
                                            .takeAttribute(tag!!, FaithlPointAPI.getPoint(player).getPoints(tag)!!)
                                    }
                                }
                            }
                            else -> {
                                if (conf.getBoolean("Item.${it.key}.Function", true)) {
                                    if (FaithlPointAPI.condition(conf["Item.${it.key}.Condition.Else"], player).get()) {
                                        FaithlPointAPI.getPoint(player).addAttribute(tag!!, 1)
                                    }
                                }
                            }
                        }
                        val map = mutableMapOf(
                            "{totalPoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).totalPoints),
                            "{availablePoints}" to Coerce.toString(
                                FaithlPointAPI.getPoint(player).getAvailablePoints()
                            ),
                            "{tag}" to conf.getString("Item.${it.key}.AttributeTag"),
                            "{take}" to conf.getInt("Item.${it.key}.Take").toString(),
                            "{player}" to player.name,
                            "{point}" to Coerce.toString(
                                FaithlPointAPI.getPoint(player)
                                    .getPoints(conf.getString("Item.${it.key}.AttributeTag")!!) ?: "0"
                            )
                        )
                        doActions(actions, map, player, it.key.toString())
                    }
                    build(this, player)
                    return@onClick
                }
            }
        }
        onClose { player, _ ->
            player.updateInventory()
            FaithlPoint.playerReceptacle[player] = null
        }
    }

    private fun doActions(
        actions: MutableList<String>,
        map: MutableMap<String, String?>,
        player: Player,
        item: String
    ) {
        for (str in listReplace(actions, map, player, item)) {
            val chain = str.split("]".toRegex(), 2).toMutableList()
            val type = chain[0]
            val action = chain[1].replaceFirst(" ", "").colored()
            if (type.contains("ActionBar") || type.contains("A"))
                sendActionBar(action, player)
            if (type.contains("Title") || type.contains("T"))
                player.sendTitle(action.split("||")[0], action.split("||")[1], 5, 20, 5)
            if (type.contains("Sound") || type.contains("S"))
                XSound.play(player, action)
            if (type.contains("Message") || type.contains("M"))
                player.sendMessage(action)
            if (type.contains("Command") || type.contains("C"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action)
            if (type.contains("Close"))
                player.closeInventory()
        }
    }

    private fun sendActionBar(msg: String, player: Player) {
        val message = TypeActionBar()
        message.text = msg
        message.send(adaptPlayer(player))
    }

    private fun set(slot: Char, itemStack: ItemStack) {
        items[slot] = itemStack
    }

    private fun build(receptacle: Receptacle, player: Player) {
        for (item in conf.getConfigurationSection("Item")?.getKeys(false)!!) {
            if (!setSlot(item, player))
                continue
        }
        var row = 0
        while (row < slots.size) {
            val line = slots[row]
            var cel = 0
            while (cel < line.size && cel < 9) {
                receptacle.setItem(items[line[cel]] ?: ItemStack(Material.AIR), row * 9 + cel)
                cel++
            }
            row++
        }
        receptacle.refresh()
    }

    fun setSlot(item: String, player: Player): Boolean {
        val optional = XMaterial.matchXMaterial(conf.getString("Item.${item}.Material") ?: return false)
        val material = if (optional.isPresent) {
            optional.get()
        } else {
            console().sendMessage("无效的Material $optional,追踪节点 Item.${item}.Material")
            XMaterial.AIR
        }
        set(Coerce.toChar(item), buildItem(material) {
            val type = conf.getString("Item.${item}.Type")
            if (type == "Attribute") {
                FaithlPoint.attributes[conf.getString("Item.${item}.AttributeTag")!!] =
                    conf.getConfigurationSection("Item.${item}")!!
                val map = mutableMapOf(
                    "{totalPoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).totalPoints),
                    "{availablePoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).getAvailablePoints()),
                    "{tag}" to conf.getString("Item.${item}.AttributeTag"),
                    "{take}" to conf.getInt("Item.${item}.Take").toString(),
                    "{player}" to player.name,
                    "{point}" to Coerce.toString(
                        FaithlPointAPI.getPoint(player).getPoints(conf.getString("Item.${item}.AttributeTag")!!) ?: "0"
                    )
                )
                name = strReplace(conf.getString("Item.${item}.Name")!!.colored(), map, player)
                for (s in listReplace(conf.getStringList("Item.${item}.Lore").colored(), map, player, item)) {
                    lore += s.colored()
                }
            }
            if (type == "Item") {
                val map = mutableMapOf(
                    "{player}" to player.name,
                    "{totalPoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).totalPoints),
                    "{availablePoints}" to Coerce.toString(FaithlPointAPI.getPoint(player).getAvailablePoints())
                )
                name = strReplace(conf.getString("Item.${item}.Name")!!.colored(), map, player)
                for (s in listReplace(conf.getStringList("Item.${item}.Lore").colored(), map, player, item)) {
                    lore += s.colored()
                }
            }
            for (s in conf.getStringList("Item.${item}.Enchants")) {
                val enchant = s.split(":")[0]
                val level = Coerce.toInteger(s.split(":")[1])
                enchants[XEnchantment.valueOf(enchant).parseEnchantment()!!] =
                    (enchants[XEnchantment.valueOf(enchant).parseEnchantment()!!] ?: 0) + level
            }
        }
        )
        return true
    }

    private fun listReplace(
        lore: List<String>,
        map: MutableMap<String, String?>,
        player: Player,
        item: String
    ): List<String> {
        val listReplaced = mutableListOf<String>()
        for (str in lore) {
            var strReplaced = str
            map.forEach {
                strReplaced = strReplaced.replace(it.key, it.value!!, ignoreCase = true)
            }
            val attrs = conf.getConfigurationSection("Item.${item}.Attributes")
            if (attrs == null) {
                listReplaced.add(strReplaced)
                continue
            }
            for (attr in attrs.getKeys(false)) {
                if (str.contains("{Attribute.$attr}", true)) {
                    strReplaced = strReplaced.replace(
                        "{Attribute.$attr}", ((conf.getString("Item.${item}.Attributes.$attr")
                            ?.toDoubleOrNull()
                            ?: 0.0) * FaithlPointAPI.getPoint(player)
                            .getPoints(conf.getString("Item.${item}.AttributeTag")!!)!!).toString(), true
                    )
                }
            }
            listReplaced.add(strReplaced)
        }
        return listReplaced.replacePlaceholder(player)
    }

    private fun strReplace(name: String, map: MutableMap<String, String?>, player: Player): String {
        var strReplaced = name
        map.forEach {
            strReplaced = strReplaced.replace(it.key, it.value!!, ignoreCase = true)
        }
        return strReplaced.replacePlaceholder(player)
    }

    init {
        FaithlPoint.menus.add(this)
    }
}