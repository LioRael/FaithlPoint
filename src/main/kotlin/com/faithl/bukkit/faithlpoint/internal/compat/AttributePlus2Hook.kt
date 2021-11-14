package com.faithl.bukkit.faithlpoint.internal.compat

import ac.github.oa.api.OriginAttributeAPI
import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.serverct.ersha.jd.AttributeAPI
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.util.Version
import taboolib.common5.Coerce

internal object AttributePlus2Hook {

    @SubscribeEvent(bind= "org.serverct.ersha.jd.event.AttrAttributeUpdateEvent")
    fun e(e: OptionalEvent){
        if (Version(Bukkit.getPluginManager().getPlugin("AttributePlus")!!.description.version) >= Version("3.0.0")){
            return
        }
        val attributeData = e.source.getProperty<org.serverct.ersha.jd.attribute.AttributeData>("attributeData")!!
        val entity = e.source.getProperty<Entity>("entity")
        if (entity is Player) {
            val attrData = AttributeAPI.getAttrData(entity)
            AttributeAPI.deleteAttribute(entity, "FaithlPoint.${entity.name}")
            val attributes = FaithlPoint.attributes
            if (FaithlPoint.attributes.isEmpty()){
                return
            }
            val points = FaithlPointAPI.getPoint(entity).points
            val list = mutableListOf<String>()
            attributes.forEach { (key, value) ->
                value.getKeys(false).forEach {
                    list += "${it}: ${Coerce.toDouble(value.getString(it)) * points[key]!!}"
                }
            }
            AttributeAPI.addAttribute(entity, "FaithlPoint.${entity.name}", list, false)
        }
    }

}