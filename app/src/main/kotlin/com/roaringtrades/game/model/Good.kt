package com.roaringtrades.game.model

enum class Good(
    val displayName: String,
    val emoji: String,
    val basePrice: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val volatility: Float,
    val capacityPerUnit: Int
) {
    BATHTUB_GIN(
        displayName = "Bathtub Gin",
        emoji = "\uD83E\uDDEA",
        basePrice = 50,
        minPrice = 15,
        maxPrice = 200,
        volatility = 0.6f,
        capacityPerUnit = 1
    ),
    WHISKEY(
        displayName = "Whiskey",
        emoji = "\uD83E\uDD43",
        basePrice = 120,
        minPrice = 40,
        maxPrice = 400,
        volatility = 0.5f,
        capacityPerUnit = 1
    ),
    RUM(
        displayName = "Rum",
        emoji = "\uD83C\uDF79",
        basePrice = 80,
        minPrice = 25,
        maxPrice = 300,
        volatility = 0.55f,
        capacityPerUnit = 1
    ),
    MOONSHINE(
        displayName = "Moonshine",
        emoji = "\uD83C\uDF1D",
        basePrice = 200,
        minPrice = 60,
        maxPrice = 700,
        volatility = 0.7f,
        capacityPerUnit = 1
    ),
    CHAMPAGNE(
        displayName = "Champagne",
        emoji = "\uD83C\uDF7E",
        basePrice = 300,
        minPrice = 100,
        maxPrice = 1000,
        volatility = 0.4f,
        capacityPerUnit = 1
    )
}
