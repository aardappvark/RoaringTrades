package com.roaringtrades.game.model

enum class Achievement(
    val displayName: String,
    val emoji: String,
    val description: String,
    val cashReward: Int // Cash bonus when claimed
) {
    FIRST_TRADE(
        displayName = "First Deal",
        emoji = "\uD83E\uDD1D",
        description = "Complete your first buy",
        cashReward = 100
    ),
    BIG_SPENDER(
        displayName = "Big Spender",
        emoji = "\uD83D\uDCB0",
        description = "Spend \$5,000 in a single purchase",
        cashReward = 250
    ),
    TEN_K_CLUB(
        displayName = "10K Club",
        emoji = "\uD83D\uDCB5",
        description = "Reach \$10,000 net worth",
        cashReward = 500
    ),
    FIFTY_K_CLUB(
        displayName = "50K Club",
        emoji = "\uD83D\uDC8E",
        description = "Reach \$50,000 net worth",
        cashReward = 1000
    ),
    HUNDRED_K_CLUB(
        displayName = "100K Club",
        emoji = "\uD83D\uDC51",
        description = "Reach \$100,000 net worth",
        cashReward = 2500
    ),
    SURVIVOR(
        displayName = "Survivor",
        emoji = "\uD83D\uDEE1\uFE0F",
        description = "Survive 5 chase encounters",
        cashReward = 500
    ),
    SPEED_DEMON(
        displayName = "Speed Demon",
        emoji = "\u26A1",
        description = "Buy the Speedboat",
        cashReward = 1000
    ),
    SPEAKEASY_OWNER(
        displayName = "Speakeasy Owner",
        emoji = "\uD83C\uDF78",
        description = "Open your first speakeasy",
        cashReward = 300
    ),
    SPEAKEASY_MOGUL(
        displayName = "Speakeasy Mogul",
        emoji = "\uD83C\uDF1F",
        description = "Max out a speakeasy to tier 3",
        cashReward = 2000
    ),
    FIGHT_CLUB(
        displayName = "Fight Club",
        emoji = "\uD83E\uDD4A",
        description = "Win 3 chase encounters",
        cashReward = 750
    ),
    DEBT_FREE(
        displayName = "Debt Free",
        emoji = "\u2705",
        description = "Pay off a loan shark debt",
        cashReward = 500
    ),
    WORLD_TRAVELER(
        displayName = "World Traveler",
        emoji = "\uD83C\uDF0D",
        description = "Visit all 6 neighborhoods",
        cashReward = 300
    ),
    STREET_TOUGH(
        displayName = "Street Tough",
        emoji = "\uD83D\uDCA5",
        description = "Stand your ground against a rival gang",
        cashReward = 400
    ),
    HOT_STREAK(
        displayName = "Hot Streak",
        emoji = "\uD83D\uDD25",
        description = "Make profit 5 trades in a row",
        cashReward = 750
    ),
    UNTOUCHABLE(
        displayName = "The Untouchable",
        emoji = "\uD83D\uDE0E",
        description = "End the game with 0 heat",
        cashReward = 1500
    ),
    AL_CAPONE(
        displayName = "Al Capone",
        emoji = "\uD83C\uDFC6",
        description = "Reach Al Capone rank",
        cashReward = 5000
    ),
    SKY_KING(
        displayName = "Sky King",
        emoji = "\uD83D\uDEE9\uFE0F",
        description = "Buy the Zeppelin",
        cashReward = 2000
    )
}
