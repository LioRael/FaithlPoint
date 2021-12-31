package com.faithl.faithlpoint.internal.compat

import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderHook : PlaceholderExpansion {
    override val identifier: String
        get() = "fpoint"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        when (args) {
            "availablePoints" -> return FaithlPointAPI.getPoint(player!!).getAvailablePoints().toString()
            "totalPoints" -> return FaithlPointAPI.getPoint(player!!).totalPoints.toString()
        }
        return "null"
    }
}