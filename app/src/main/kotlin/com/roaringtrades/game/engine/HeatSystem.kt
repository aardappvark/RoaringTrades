package com.roaringtrades.game.engine

import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.InventoryItem
import com.roaringtrades.game.model.Neighborhood
import kotlin.random.Random

object HeatSystem {

    enum class HeatLevel(val label: String, val emoji: String, val interceptChance: Float) {
        NONE("No Heat", "", 0.0f),
        LOW("Low Heat", "\u26A0\uFE0F", 0.10f),
        MEDIUM("Medium Heat", "\u26A0\uFE0F\u26A0\uFE0F", 0.20f),
        HIGH("High Heat", "\u26A0\uFE0F\u26A0\uFE0F\u26A0\uFE0F", 0.30f)
    }

    fun getHeatLevel(heat: Int): HeatLevel = when {
        heat <= 30 -> HeatLevel.NONE
        heat <= 60 -> HeatLevel.LOW
        heat <= 80 -> HeatLevel.MEDIUM
        else -> HeatLevel.HIGH
    }

    fun calculateHeatGain(@Suppress("UNUSED_PARAMETER") good: Good, quantity: Int, neighborhood: Neighborhood): Int {
        val base = quantity
        return (base * neighborhood.heatModifier).toInt().coerceAtLeast(1)
    }

    fun dailyHeatDecay(currentHeat: Int, vehicleBonus: Int = 0): Int {
        return (currentHeat - 5 - vehicleBonus).coerceAtLeast(0)
    }

    fun checkForIntercept(heat: Int, evasionBonus: Float = 0.0f): Int {
        val level = getHeatLevel(heat)
        if (level.interceptChance == 0.0f) return 0

        val effectiveChance = (level.interceptChance - evasionBonus).coerceAtLeast(0.0f)
        if (effectiveChance == 0.0f) return 0

        val roll = Random.nextFloat()
        return if (roll < effectiveChance) {
            Random.nextInt(20, 51)
        } else {
            0
        }
    }

    fun executeSeizure(
        inventory: Map<Good, InventoryItem>,
        lossPercentage: Int
    ): Map<Good, Int> {
        val seized = mutableMapOf<Good, Int>()
        for ((good, item) in inventory) {
            if (item.quantity > 0) {
                val lost = (item.quantity * lossPercentage / 100).coerceAtLeast(1)
                seized[good] = lost
            }
        }
        return seized
    }
}
