package com.roaringtrades.game.model

enum class Vehicle(
    val displayName: String,
    val emoji: String,
    val description: String,
    val capacity: Int,
    val heatDecayBonus: Int,
    val evasionBonus: Float,
    val maxHp: Int,
    val speed: Int, // 1-10, affects flee success
    val fightBonus: Float, // bonus to fight success
    val price: Int
) {
    ON_FOOT(
        displayName = "On Foot",
        emoji = "\uD83D\uDEB6",
        description = "Just your pockets. Low profile but can't carry much.",
        capacity = 30,
        heatDecayBonus = 0,
        evasionBonus = 0.0f,
        maxHp = 20,
        speed = 3,
        fightBonus = 0.0f,
        price = 0
    ),
    BICYCLE(
        displayName = "Bicycle",
        emoji = "\uD83D\uDEB2",
        description = "Quick and quiet. Basket holds a bit more.",
        capacity = 60,
        heatDecayBonus = 1,
        evasionBonus = 0.05f,
        maxHp = 30,
        speed = 5,
        fightBonus = 0.0f,
        price = 1500
    ),
    MODEL_T(
        displayName = "Model T",
        emoji = "\uD83D\uDE97",
        description = "A reliable Ford. Blends in with traffic.",
        capacity = 120,
        heatDecayBonus = 2,
        evasionBonus = 0.10f,
        maxHp = 60,
        speed = 6,
        fightBonus = 0.05f,
        price = 8000
    ),
    DELIVERY_TRUCK(
        displayName = "Delivery Truck",
        emoji = "\uD83D\uDE9A",
        description = "Hide your goods behind \"Laundry Service\" signs.",
        capacity = 250,
        heatDecayBonus = 3,
        evasionBonus = 0.15f,
        maxHp = 100,
        speed = 4,
        fightBonus = 0.10f,
        price = 25000
    ),
    ARMORED_CAR(
        displayName = "Armored Car",
        emoji = "\uD83D\uDE93",
        description = "Reinforced and intimidating. They think twice before pursuing.",
        capacity = 400,
        heatDecayBonus = 5,
        evasionBonus = 0.25f,
        maxHp = 180,
        speed = 5,
        fightBonus = 0.20f,
        price = 60000
    ),
    SPEEDBOAT(
        displayName = "Speedboat",
        emoji = "\u26F5",
        description = "Run the lakefront. Nobody catches you on the water.",
        capacity = 500,
        heatDecayBonus = 8,
        evasionBonus = 0.35f,
        maxHp = 130,
        speed = 9,
        fightBonus = 0.15f,
        price = 120000
    ),
    ZEPPELIN(
        displayName = "Zeppelin",
        emoji = "\uD83D\uDEE9\uFE0F",
        description = "The ultimate trading machine. You own the skies of Chicago.",
        capacity = 1000,
        heatDecayBonus = 15,
        evasionBonus = 0.50f,
        maxHp = 250,
        speed = 10,
        fightBonus = 0.30f,
        price = 500000
    )
}
