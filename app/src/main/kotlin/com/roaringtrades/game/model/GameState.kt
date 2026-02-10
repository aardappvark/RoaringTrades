package com.roaringtrades.game.model

import com.roaringtrades.game.AppConfig

data class GameState(
    val day: Int = 1,
    val maxDays: Int = AppConfig.Game.MAX_DAYS,
    val cash: Int = AppConfig.Game.STARTING_CASH,
    val currentNeighborhood: Neighborhood = Neighborhood.SOUTH_SIDE,
    val inventory: Map<Good, InventoryItem> = Good.entries.associateWith { InventoryItem() },
    val currentVehicle: Vehicle = Vehicle.ON_FOOT,
    val vehicleHp: Int = Vehicle.ON_FOOT.maxHp,
    val heat: Int = 0,
    val prices: Map<Good, Int> = emptyMap(),
    val previousPrices: Map<Good, Int> = emptyMap(),
    val hotCommodity: Good? = null,
    val crackdownGood: Good? = null,
    val pendingEvent: RandomEvent? = null,
    val gameOver: Boolean = false,
    // Chase system
    val pendingChase: ChaseEncounter? = null,
    val chaseResult: ChaseResult? = null,
    // Rival gangs
    val pendingEncounter: EncounterResult? = null,
    // Loan shark
    val loanShark: LoanShark = LoanShark(),
    // Speakeasies
    val speakeasies: Map<Neighborhood, Speakeasy> = Neighborhood.entries.associateWith { Speakeasy(it) },
    // Reputation per neighborhood (0-100)
    val reputation: Map<Neighborhood, Int> = Neighborhood.entries.associateWith { 0 },
    // Neighborhoods visited (for achievements)
    val neighborhoodsVisited: Set<Neighborhood> = setOf(Neighborhood.SOUTH_SIDE),
    // Daily headline
    val headline: Headline? = null,
    // Achievements: earned = ready to claim, claimed = collected reward
    val earnedAchievements: Set<Achievement> = emptySet(),
    val claimedAchievements: Set<Achievement> = emptySet(),
    val pendingAchievement: Achievement? = null,
    // Stats tracking
    val totalBuysCount: Int = 0,
    val totalSellsCount: Int = 0,
    val consecutiveProfitTrades: Int = 0,
    val chasesEncountered: Int = 0,
    val chasesWon: Int = 0,
    val gangsFoughtOff: Int = 0
) {
    val capacity: Int
        get() = currentVehicle.capacity

    val usedCapacity: Int
        get() = inventory.values.sumOf { it.quantity }

    val freeCapacity: Int
        get() = capacity - usedCapacity

    val inventoryValue: Int
        get() = inventory.entries.sumOf { (good, item) ->
            item.quantity * (prices[good] ?: good.basePrice)
        }

    val netWorth: Int
        get() = cash + inventoryValue + totalSpeakeasyValue - loanShark.debt

    val totalCostBasis: Int
        get() = inventory.values.sumOf { it.totalCostBasis }

    val profitFromStart: Int
        get() = netWorth - AppConfig.Game.STARTING_CASH

    val vehicleHpPercent: Float
        get() = if (currentVehicle.maxHp > 0) vehicleHp.toFloat() / currentVehicle.maxHp else 0f

    val isVehicleDamaged: Boolean
        get() = vehicleHp < currentVehicle.maxHp

    val vehicleHpStatus: String
        get() = when {
            vehicleHpPercent >= 0.8f -> "Good"
            vehicleHpPercent >= 0.5f -> "Damaged"
            vehicleHpPercent >= 0.25f -> "Critical"
            else -> "Totaled"
        }

    val totalSpeakeasyIncome: Int
        get() = speakeasies.values.sumOf { it.dailyIncome }

    val totalSpeakeasyValue: Int
        get() = speakeasies.values.sumOf { it.totalInvested }

    val repairCost: Int
        get() {
            val damage = currentVehicle.maxHp - vehicleHp
            return (damage * 3) // $3 per HP
        }

    // Achievements helpers
    val unclaimedCount: Int
        get() = earnedAchievements.size - claimedAchievements.size

    val totalAchievements: Int
        get() = earnedAchievements.size

    // Pay off heat: $50 per heat point
    val payoffCostPerHeat: Int
        get() = 50

    val payoffCost: Int
        get() = heat * payoffCostPerHeat

    val canPayoff: Boolean
        get() = heat > 0 && payoffCost <= cash
}
