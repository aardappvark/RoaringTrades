package com.roaringtrades.game.model

/**
 * Represents a chase encounter when the player draws too much attention.
 * Instead of auto-seizure, player gets to choose Fight or Flee.
 */
data class ChaseEncounter(
    val seizurePercentage: Int,
    val pursuitStrength: Int // 1-100, determines fight difficulty
) {
    val description: String
        get() = when {
            pursuitStrength >= 80 -> "A full squad of agents blocks your path!"
            pursuitStrength >= 60 -> "Prohibition agents have cornered you!"
            pursuitStrength >= 40 -> "A pair of watchmen spotted your goods!"
            else -> "A lone agent is tailing you!"
        }

    val fightOddsDescription: String
        get() = when {
            pursuitStrength >= 80 -> "Very Risky"
            pursuitStrength >= 60 -> "Risky"
            pursuitStrength >= 40 -> "Even Odds"
            else -> "Good Odds"
        }

    val fleeOddsDescription: String
        get() = "Depends on speed & cargo weight"
}

/**
 * Result of a chase encounter after the player makes a choice.
 */
sealed class ChaseResult {
    data class FightWon(
        val heatReduced: Int,
        val vehicleDamage: Int
    ) : ChaseResult()

    data class FightLost(
        val goodsLost: Map<Good, Int>,
        val cashFine: Int,
        val vehicleDamage: Int,
        val heatGained: Int
    ) : ChaseResult()

    data class FleeSuccess(
        val goodsDropped: Map<Good, Int>, // might drop some goods while fleeing
        val vehicleDamage: Int
    ) : ChaseResult()

    data class FleeFailed(
        val goodsLost: Map<Good, Int>,
        val cashFine: Int,
        val vehicleDamage: Int,
        val heatGained: Int
    ) : ChaseResult()
}
