package com.roaringtrades.game.model

enum class Neighborhood(
    val displayName: String,
    val description: String,
    val heatModifier: Float
) {
    SOUTH_SIDE("South Side", "Rough territory, cheap goods", 1.2f),
    NORTH_SIDE("North Side", "Wealthy clientele, premium prices", 0.8f),
    WEST_SIDE("West Side", "Industrial district, bulk deals", 1.0f),
    DOWNTOWN("Downtown", "High risk, high reward", 1.5f),
    THE_DOCKS("The Docks", "Import hub, rare finds", 1.1f),
    UPTOWN("Uptown", "Speakeasy central, steady trade", 0.9f)
}
