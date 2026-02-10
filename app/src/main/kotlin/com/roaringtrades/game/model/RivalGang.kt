package com.roaringtrades.game.model

enum class RivalGang(
    val displayName: String,
    val emoji: String,
    val homeNeighborhood: Neighborhood,
    val specialty: Good,
    val priceInfluence: Float, // multiplier on their specialty in their territory
    val encounterChance: Float // chance per visit to their territory
) {
    SOUTH_SIDE_BOYS(
        displayName = "South Side Boys",
        emoji = "\uD83E\uDD1C",
        homeNeighborhood = Neighborhood.SOUTH_SIDE,
        specialty = Good.BATHTUB_GIN,
        priceInfluence = 1.3f,
        encounterChance = 0.15f
    ),
    NORTH_SHORE_SYNDICATE(
        displayName = "North Shore Syndicate",
        emoji = "\uD83C\uDFA9",
        homeNeighborhood = Neighborhood.NORTH_SIDE,
        specialty = Good.CHAMPAGNE,
        priceInfluence = 1.25f,
        encounterChance = 0.12f
    ),
    DOCK_RATS(
        displayName = "Dock Rats",
        emoji = "\uD83D\uDC00",
        homeNeighborhood = Neighborhood.THE_DOCKS,
        specialty = Good.RUM,
        priceInfluence = 1.35f,
        encounterChance = 0.18f
    ),
    DOWNTOWN_OUTFIT(
        displayName = "Downtown Outfit",
        emoji = "\uD83D\uDD2B",
        homeNeighborhood = Neighborhood.DOWNTOWN,
        specialty = Good.WHISKEY,
        priceInfluence = 1.4f,
        encounterChance = 0.20f
    );

    companion object {
        fun getGangForNeighborhood(neighborhood: Neighborhood): RivalGang? {
            return entries.find { it.homeNeighborhood == neighborhood }
        }
    }
}

sealed class EncounterResult {
    data class ShakenDown(
        val gang: RivalGang,
        val cashLost: Int,
        val goodsLost: Map<Good, Int>
    ) : EncounterResult() {
        val description: String
            get() {
                val goodsText = if (goodsLost.isNotEmpty()) {
                    val items = goodsLost.entries.joinToString(", ") { "${it.value} ${it.key.displayName}" }
                    " They took $items."
                } else ""
                return "The ${gang.displayName} ${gang.emoji} confronted you! Lost \$$cashLost.$goodsText"
            }
    }

    data class Intimidated(
        val gang: RivalGang,
        val heatGained: Int
    ) : EncounterResult() {
        val description: String
            get() = "The ${gang.displayName} ${gang.emoji} warned you to stay off their territory! Heat +$heatGained"
    }

    data class FoughtOff(
        val gang: RivalGang,
        val vehicleDamage: Int,
        val reputationGained: Int
    ) : EncounterResult() {
        val description: String
            get() = "You stood your ground against the ${gang.displayName} ${gang.emoji}! Earned respect in ${gang.homeNeighborhood.displayName}."
    }
}
