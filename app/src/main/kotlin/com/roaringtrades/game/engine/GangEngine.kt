package com.roaringtrades.game.engine

import com.roaringtrades.game.model.*
import kotlin.random.Random

object GangEngine {

    /**
     * Check if a rival gang confronts the player when entering a neighborhood.
     */
    fun checkEncounter(
        neighborhood: Neighborhood,
        reputation: Int,
        vehicleHpPercent: Float
    ): EncounterResult? {
        val gang = RivalGang.getGangForNeighborhood(neighborhood) ?: return null

        // Reputation reduces encounter chance, but damaged vehicles attract gangs
        val repReduction = reputation / 200f // 0-0.5 reduction
        val damageBonus = (1f - vehicleHpPercent) * 0.15f // Up to +15% if vehicle is wrecked
        val effectiveChance = (gang.encounterChance - repReduction + damageBonus).coerceAtLeast(0.02f)

        if (Random.nextFloat() >= effectiveChance) return null

        // Encounter happened! Determine type
        val roll = Random.nextFloat()
        return when {
            roll < 0.45f -> {
                // Shaken down
                val cashLost = Random.nextInt(100, 500)
                EncounterResult.ShakenDown(
                    gang = gang,
                    cashLost = cashLost,
                    goodsLost = emptyMap() // just cash for simplicity
                )
            }
            roll < 0.75f -> {
                // Intimidated
                EncounterResult.Intimidated(
                    gang = gang,
                    heatGained = Random.nextInt(5, 15)
                )
            }
            else -> {
                // Fought off (player's vehicle helps)
                val vehicleDamage = Random.nextInt(5, 15)
                EncounterResult.FoughtOff(
                    gang = gang,
                    vehicleDamage = vehicleDamage,
                    reputationGained = Random.nextInt(5, 15)
                )
            }
        }
    }

    /**
     * Apply rival gang price influence to prices.
     */
    fun applyGangInfluence(
        prices: Map<Good, Int>,
        neighborhood: Neighborhood
    ): Map<Good, Int> {
        val gang = RivalGang.getGangForNeighborhood(neighborhood) ?: return prices
        return prices.mapValues { (good, price) ->
            if (good == gang.specialty) {
                (price * gang.priceInfluence).toInt().coerceIn(good.minPrice, good.maxPrice)
            } else {
                price
            }
        }
    }
}
