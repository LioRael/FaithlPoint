package com.faithl.faithlpoint.internal.compat

import com.faithl.faithlpoint.FaithlPoint
import com.faithl.faithlpoint.api.FaithlPointAPI
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderHook : PlaceholderExpansion {
    override val identifier: String
        get() = "fpoint"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return when (args) {
            "availablePoints" -> FaithlPointAPI.getPoint(player!!).getAvailablePoints().toString()
            "totalPoints" -> FaithlPointAPI.getPoint(player!!).totalPoints.toString()
            else -> {
                val points = FaithlPointAPI.getPoint(player!!).points
                Coerce.toString(points[args])
            }
        }
    }
}