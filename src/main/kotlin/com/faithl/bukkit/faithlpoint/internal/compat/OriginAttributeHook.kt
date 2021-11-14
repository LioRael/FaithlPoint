package com.faithl.bukkit.faithlpoint.internal.compat

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.OriginAttributeAPI.loadList
import ac.github.oa.api.OriginAttributeAPI.setExtra
import com.faithl.bukkit.faithlpoint.FaithlPoint
import com.faithl.bukkit.faithlpoint.api.FaithlPointAPI
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common5.Coerce


internal object OriginAttributeHook {

    @SubscribeEvent(bind = "ac.github.oa.api.event.entity.EntityUpdateEvent")
    fun e1(e: OptionalEvent) {
        val entity = e.source.getProperty<LivingEntity>("entity")
        if (entity is Player) {
            OriginAttributeAPI.removeExtra(entity.uniqueId,"FaithlPoint.${entity.name}")
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
            val attr = loadList(entity,list)
            setExtra(entity.uniqueId, "FaithlPoint.${entity.name}", attr)
        }
    }

}