package com.roaringtrades.game.model

data class Speakeasy(
    val neighborhood: Neighborhood,
    val investmentLevel: Int = 0, // 0 = not invested, 1-3 = tiers
    val totalInvested: Int = 0
) {
    val dailyIncome: Int
        get() = when (investmentLevel) {
            1 -> 25
            2 -> 75
            3 -> 200
            else -> 0
        }

    val name: String
        get() = when (neighborhood) {
            Neighborhood.SOUTH_SIDE -> "The Blind Pig"
            Neighborhood.NORTH_SIDE -> "The Gilded Lily"
            Neighborhood.WEST_SIDE -> "The Iron Horse"
            Neighborhood.DOWNTOWN -> "Club Gatsby"
            Neighborhood.THE_DOCKS -> "The Rusty Anchor"
            Neighborhood.UPTOWN -> "The Velvet Room"
        }

    val emoji: String
        get() = when (investmentLevel) {
            0 -> "\uD83C\uDFDA" // old building
            1 -> "\uD83C\uDF7A" // beer mug
            2 -> "\uD83C\uDF78" // cocktail
            3 -> "\uD83C\uDF1F" // star
            else -> "\uD83C\uDFDA"
        }

    companion object {
        fun getUpgradeCost(currentLevel: Int): Int = when (currentLevel) {
            0 -> 3000  // open it up
            1 -> 10000  // upgrade to tier 2
            2 -> 30000 // upgrade to tier 3
            else -> Int.MAX_VALUE // maxed out
        }

        fun getUpgradeDescription(targetLevel: Int): String = when (targetLevel) {
            1 -> "Open for business - a small, hidden bar"
            2 -> "Add live jazz and premium stock"
            3 -> "The hottest spot in town - VIP lounge"
            else -> "Maxed out"
        }
    }
}
