package com.faithl.bukkit.faithlpoint

import com.alibaba.fastjson.JSONObject
import com.faithl.bukkit.faithlpoint.internal.conf.Loader
import com.faithl.bukkit.faithlpoint.internal.display.PointMenu
import com.faithl.bukkit.faithlpoint.internal.point.Point
import com.faithl.bukkit.faithlpoint.util.JsonUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.env.RuntimeDependency
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.*
import taboolib.common.util.Version
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.lang.sendLang
import taboolib.module.metrics.Metrics
import taboolib.module.ui.receptacle.Receptacle
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang

@RuntimeDependency(value = "com.alibaba:fastjson:1.2.78")
object FaithlPoint: Plugin() {

    @Config("settings.yml", migrate = true,autoReload = true)
    lateinit var setting: Configuration

    val plugin by lazy { BukkitPlugin.getInstance() }

    var isOutDate = false

    override fun onLoad() {
        Metrics(13249, pluginVersion, runningPlatform)
    }

    override fun onEnable() {
        Loader.loadLevels()
        init()
    }

    fun init() {
        try {
            val type = setting.getString("Options.Database.Type")!!
            val host = setting.getString("Options.Database.Host")!!
            val database = setting.getString("Options.Database.Database")!!
            val user = setting.getString("Options.Database.User")!!
            val password = setting.getString("Options.Database.Password")!!
            val port = setting.getInt("Options.Database.Port")
            val table = "${pluginId.lowercase()}_data"
            if (type.contains("MySQL", true)) {
                setupPlayerDatabase(host, port, user, password, database, table)
                console().sendLang("Plugin-Database-Enabled", "MySQL")
            } else {
                setupPlayerDatabase(newFile(getDataFolder(), "pointData.db"))
                console().sendLang("Plugin-Database-Enabled", "SQLite")
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            disablePlugin()
            return
        }
        if (onlinePlayers().isNotEmpty())
            for (player in onlinePlayers())
                player.setupDataContainer()
        if (Bukkit.getPluginManager().isPluginEnabled("AttributePlus"))
            console().sendLang("Plugin-Hooked", "AttributePlus")
        else
            disablePlugin()
    }

    /**
     * Check update
     *
     * @param sender
     */
    fun checkUpdate(sender: Player? = null){
        if(!setting.getBoolean("Options.Check-Update"))
            return
        val json = JsonUtil.loadJson("https://api.faithl.com/version.php?plugin=FaithlPoint")
        val `object` = JSONObject.parseObject(json)
        val version = Version(`object`.getString("version"))
        if (version > Version(pluginVersion)){
            isOutDate = true
            if (sender == null)
                console().sendLang("Plugin-Update",pluginVersion,version)
            else
                sender.sendLang("Plugin-Update",pluginVersion,version.source,"https://www.mcbbs.net/thread-1275680-1-1.html")
        }
    }

    var playerPoints:MutableList<Point> = mutableListOf()
    var attributes = mutableMapOf<String, ConfigurationSection>()
    var menus:MutableList<PointMenu> = mutableListOf()
    var playerReceptacle = mutableMapOf<Player, Receptacle?>()
}