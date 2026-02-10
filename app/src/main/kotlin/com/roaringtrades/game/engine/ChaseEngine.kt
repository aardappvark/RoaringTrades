package com.roaringtrades.game.engine

import com.roaringtrades.game.model.*
import kotlin.random.Random

object ChaseEngine {

    /**
     * Create a chase encounter based on current heat level.
     */
    fun createEncounter(heat: Int): ChaseEncounter {
        val seizure = Random.nextInt(20, 51)
        val pursuitStrength = when {
            heat >= 90 -> Random.nextInt(70, 100)
            heat >= 70 -> Random.nextInt(50, 80)
            heat >= 50 -> Random.nextInt(30, 60)
            else -> Random.nextInt(15, 45)
        }
        return ChaseEncounter(
            seizurePercentage = seizure,
            pursuitStrength = pursuitStrength
        )
    }

    /**
     * Resolve a FIGHT choice.
     * Success depends on vehicle fight bonus, vehicle HP %, and pursuit strength.
     */
    fun fight(
        state: GameState,
        encounter: ChaseEncounter
    ): ChaseResult {
        val vehicleHpFactor = state.vehicleHpPercent // 0..1
        val fightBonus = state.currentVehicle.fightBonus // 0..0.20
        val reputationBonus = (state.reputation[state.currentNeighborhood] ?: 0) / 200f // 0..0.5

        // Player fight power: 0.3 base + vehicle bonus + hp factor * 0.3 + rep bonus
        val playerPower = 0.3f + fightBonus + (vehicleHpFactor * 0.3f) + reputationBonus
        // Pursuit power: strength/100
        val pursuitPower = encounter.pursuitStrength / 100f

        val roll = Random.nextFloat()
        val winChance = (playerPower / (playerPower + pursuitPower)).coerceIn(0.1f, 0.85f)

        // Vehicle always takes some damage in a fight
        val vehicleDamage = Random.nextInt(5, 20)

        return if (roll < winChance) {
            // Won the fight
            ChaseResult.FightWon(
                heatReduced = Random.nextInt(10, 25),
                vehicleDamage = vehicleDamage
            )
        } else {
            // Lost the fight - harsher penalty
            val goodsLost = HeatSystem.executeSeizure(
                state.inventory,
                encounter.seizurePercentage + 10 // extra penalty for fighting
            )
            ChaseResult.FightLost(
                goodsLost = goodsLost,
                cashFine = Random.nextInt(200, 800),
                vehicleDamage = vehicleDamage + Random.nextInt(10, 25),
                heatGained = Random.nextInt(5, 15)
            )
        }
    }

    /**
     * Resolve a FLEE choice.
     * Success depends on vehicle speed, cargo weight (lighter = faster), and pursuit strength.
     */
    fun flee(
        state: GameState,
        encounter: ChaseEncounter
    ): ChaseResult {
        val speedFactor = state.currentVehicle.speed / 10f // 0.3..0.9
        val loadFactor = 1f - (state.usedCapacity.toFloat() / state.capacity.toFloat().coerceAtLeast(1f)) * 0.3f
        val vehicleHpFactor = state.vehicleHpPercent * 0.2f

        val fleeChance = (speedFactor * loadFactor + vehicleHpFactor).coerceIn(0.15f, 0.90f)
        // Pursuit catch bonus based on strength
        val adjustedFleeChance = (fleeChance - encounter.pursuitStrength / 200f).coerceIn(0.15f, 0.85f)

        val roll = Random.nextFloat()

        // Small damage from the chase either way
        val vehicleDamage = Random.nextInt(2, 10)

        return if (roll < adjustedFleeChance) {
            // Escaped! But might drop some goods
            val droppedGoods = if (Random.nextFloat() < 0.3f && state.usedCapacity > 0) {
                // Drop a few items while fleeing
                val randomGood = state.inventory.entries
                    .filter { it.value.quantity > 0 }
                    .randomOrNull()
                if (randomGood != null) {
                    val dropQty = minOf(Random.nextInt(1, 4), randomGood.value.quantity)
                    mapOf(randomGood.key to dropQty)
                } else emptyMap()
            } else emptyMap()

            ChaseResult.FleeSuccess(
                goodsDropped = droppedGoods,
                vehicleDamage = vehicleDamage
            )
        } else {
            // Caught! Standard seizure
            val goodsLost = HeatSystem.executeSeizure(
                state.inventory,
                encounter.seizurePercentage
            )
            ChaseResult.FleeFailed(
                goodsLost = goodsLost,
                cashFine = Random.nextInt(100, 400),
                vehicleDamage = vehicleDamage + Random.nextInt(5, 15),
                heatGained = Random.nextInt(5, 10)
            )
        }
    }
}
