package com.faithl.bukkit.faithlpoint.internal.compat

import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.serverct.ersha.api.AttributeAPI
import org.serverct.ersha.attribute.data.AttributeData
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.util.Version
import taboolib.common5.Coerce

internal object AttributePlus3Hook {

    @SubscribeEvent(bind = "org.serverct.ersha.api.event.AttrUpdateAttributeEvent\$After")
    fun e1(e: OptionalEvent) {
        if (Version(Bukkit.getPluginManager().getPlugin("AttributePlus")!!.description.version) <= Version("3.0.0")){
            return
        }
        val attributeData = e.source.getProperty<AttributeData>("attributeData")!!
        val sourceEntity = attributeData.sourceEntity
        if (sourceEntity is Player) {
            val attrData = AttributeAPI.getAttrData(sourceEntity)
            AttributeAPI.takeSourceAttribute(attrData, "FaithlPoint.${sourceEntity.name}")
            val attributes = FaithlPoint.attributes
            if (FaithlPoint.attributes.isEmpty()){
                return
            }
            val points = FaithlPointAPI.getPoint(sourceEntity).points
            val map = HashMap<String, Array<Number>>()
            attributes.forEach { (key, value) ->
                value.getKeys(false).forEach {
                    val args = value.getString(it)!!.split("-")
                    map[it] = arrayOf(Coerce.toDouble(args[0]) * points[key]!!, Coerce.toDouble(args.getOrElse(1) { args[0] }) * points[key]!!)
                }
            }
            AttributeAPI.addSourceAttribute(attrData, "FaithlPoint.${sourceEntity.name}", map, false)
        }
    }
}
