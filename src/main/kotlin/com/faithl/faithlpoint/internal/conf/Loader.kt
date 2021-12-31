package com.faithl.faithlpoint.internal.conf

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import com.faithl.faithlpoint.internal.display.PointMenu
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import taboolib.platform.util.sendLang
import java.io.File

object Loader {
    private val folder by lazy {
        FaithlPoint.menus.clear()
        val folder = File(FaithlPoint.plugin.dataFolder, "menus")
        if (!folder.exists()) {
            arrayOf(
                "example.yml",
            ).forEach { releaseResourceFile("menus/$it", true) }
        }
        folder
    }

    fun loadLevels(sender: CommandSender = Bukkit.getConsoleSender()) {
        val files = mutableListOf<File>().also {
            it.addAll(filterMenuFiles(folder))
            it.addAll(FaithlPoint.setting.getStringList("Loader.Menu-Files").flatMap { filterMenuFiles(File(it)) })
        }
        val tasks = mutableListOf<File>().also { tasks ->
            files.forEach { file ->
                if (!tasks.any { it.nameWithoutExtension == file.nameWithoutExtension } && file.extension == "yml")
                    tasks.add(file)
            }
        }
        val serializingTime = System.currentTimeMillis()
        tasks.forEach {
            val conf = Configuration.loadFromFile(it)
            val tag = conf.getString("Tag")
            if (FaithlPointAPI.getMenu(tag!!) == null)
                PointMenu(conf)
        }
        sender.sendLang("Menu-Loader-Loaded", FaithlPoint.menus.size, System.currentTimeMillis() - serializingTime)
    }

    private fun filterMenuFiles(file: File): List<File> {
        return mutableListOf<File>().run {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(filterMenuFiles(it))
                }
            } else if (!file.name.startsWith("#")) {
                add(file)
            }
            this
        }
    }
}