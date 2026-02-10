package com.roaringtrades.game.engine

import com.roaringtrades.game.AppConfig
import com.roaringtrades.game.model.*
import kotlin.random.Random

object GameEngine {

    fun newGame(): GameState {
        val (hot, crackdown) = PriceEngine.selectDailySpecials()
        val startNeighborhood = Neighborhood.SOUTH_SIDE
        val prices = PriceEngine.generatePrices(startNeighborhood, hot, crackdown)
        val gangPrices = GangEngine.applyGangInfluence(prices, startNeighborhood)
        val headline = HeadlineEngine.generateHeadline(hot, crackdown)

        return GameState(
            day = 1,
            cash = AppConfig.Game.STARTING_CASH,
            currentNeighborhood = startNeighborhood,
            currentVehicle = Vehicle.ON_FOOT,
            vehicleHp = Vehicle.ON_FOOT.maxHp,
            heat = 0,
            prices = gangPrices,
            previousPrices = gangPrices,
            hotCommodity = hot,
            crackdownGood = crackdown,
            headline = headline
        )
    }

    fun buyVehicle(state: GameState, vehicle: Vehicle): GameState? {
        if (vehicle == state.currentVehicle) return null
        if (vehicle.price > state.cash) return null
        if (vehicle.capacity < state.currentVehicle.capacity) return null

        var newState = state.copy(
            cash = state.cash - vehicle.price,
            currentVehicle = vehicle,
            vehicleHp = vehicle.maxHp // new vehicle starts at full HP
        )

        // Check speed demon achievement
        val achievement = AchievementEngine.checkAchievements(newState)
        if (achievement != null) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + achievement,
                pendingAchievement = achievement
            )
        }

        return newState
    }

    /**
     * Pay off heat to reduce attention. Costs $50 per heat point.
     */
    fun payoffHeat(state: GameState, heatToClear: Int): GameState? {
        if (heatToClear <= 0 || heatToClear > state.heat) return null
        val cost = heatToClear * state.payoffCostPerHeat
        if (cost > state.cash) return null

        return state.copy(
            cash = state.cash - cost,
            heat = (state.heat - heatToClear).coerceAtLeast(0)
        )
    }

    fun repairVehicle(state: GameState): GameState? {
        if (!state.isVehicleDamaged) return null
        if (state.repairCost > state.cash) return null

        return state.copy(
            cash = state.cash - state.repairCost,
            vehicleHp = state.currentVehicle.maxHp
        )
    }

    fun buy(state: GameState, good: Good, quantity: Int): GameState? {
        val price = state.prices[good] ?: return null
        val totalCost = price * quantity
        if (totalCost > state.cash) return null
        if (quantity > state.freeCapacity) return null

        val currentItem = state.inventory[good] ?: InventoryItem()
        val updatedItem = currentItem.copy(
            quantity = currentItem.quantity + quantity,
            totalCostBasis = currentItem.totalCostBasis + totalCost
        )

        val heatGain = HeatSystem.calculateHeatGain(good, quantity, state.currentNeighborhood)
        val crackdownHeat = if (good == state.crackdownGood) heatGain else 0

        var newState = state.copy(
            cash = state.cash - totalCost,
            inventory = state.inventory + (good to updatedItem),
            heat = (state.heat + heatGain + crackdownHeat).coerceAtMost(100),
            totalBuysCount = state.totalBuysCount + 1
        )

        // Check achievements (first trade, big spender)
        if (totalCost >= 5000 && Achievement.BIG_SPENDER !in newState.earnedAchievements) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + Achievement.BIG_SPENDER,
                pendingAchievement = Achievement.BIG_SPENDER
            )
        } else {
            val achievement = AchievementEngine.checkAchievements(newState)
            if (achievement != null) {
                newState = newState.copy(
                    earnedAchievements = newState.earnedAchievements + achievement,
                    pendingAchievement = achievement
                )
            }
        }

        return newState
    }

    fun sell(state: GameState, good: Good, quantity: Int): GameState? {
        val price = state.prices[good] ?: return null
        val currentItem = state.inventory[good] ?: return null
        if (quantity > currentItem.quantity) return null

        val revenue = price * quantity
        val costBasisReduction = if (currentItem.quantity > 0) {
            (currentItem.totalCostBasis.toLong() * quantity / currentItem.quantity).toInt()
        } else 0

        val profit = revenue - costBasisReduction
        val newConsecutive = if (profit > 0) state.consecutiveProfitTrades + 1 else 0

        val updatedItem = currentItem.copy(
            quantity = currentItem.quantity - quantity,
            totalCostBasis = (currentItem.totalCostBasis - costBasisReduction).coerceAtLeast(0)
        )

        var newState = state.copy(
            cash = state.cash + revenue,
            inventory = state.inventory + (good to updatedItem),
            totalSellsCount = state.totalSellsCount + 1,
            consecutiveProfitTrades = newConsecutive
        )

        // Check achievements
        val achievement = AchievementEngine.checkAchievements(newState)
        if (achievement != null) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + achievement,
                pendingAchievement = achievement
            )
        }

        return newState
    }

    fun travel(state: GameState, destination: Neighborhood): GameState {
        if (destination == state.currentNeighborhood) return state

        val nextDay = state.day + 1
        val isGameOver = nextDay > state.maxDays

        // New day specials and prices
        val (hot, crackdown) = PriceEngine.selectDailySpecials()
        val basePrices = PriceEngine.generatePrices(destination, hot, crackdown)
        val newPrices = GangEngine.applyGangInfluence(basePrices, destination)

        // Reputation discount: reduce prices slightly in high-rep neighborhoods
        val rep = state.reputation[destination] ?: 0
        val repPrices = if (rep > 20) {
            val discount = 1f - (rep / 500f) // up to 20% discount at 100 rep
            newPrices.mapValues { (good, price) ->
                (price * discount).toInt().coerceIn(good.minPrice, good.maxPrice)
            }
        } else newPrices

        // Heat decay (vehicle bonus increases daily decay)
        val decayedHeat = HeatSystem.dailyHeatDecay(state.heat, state.currentVehicle.heatDecayBonus)

        // Loan shark daily interest
        val updatedLoan = if (state.loanShark.hasActiveLoan) {
            val newDebt = state.loanShark.debt + state.loanShark.dailyInterest
            state.loanShark.copy(
                debt = newDebt,
                daysUntilThreat = state.loanShark.daysUntilThreat + 1
            )
        } else state.loanShark

        // Loan shark penalty if overdue
        val loanPenalty = if (updatedLoan.isOverdue && Random.nextFloat() < 0.3f) {
            Random.nextInt(100, 300) // enforcers take some cash
        } else 0

        // Speakeasy daily income
        val speakeasyIncome = state.totalSpeakeasyIncome

        // Check for intercept (chase encounter instead of auto-seizure)
        val interceptCheck = HeatSystem.checkForIntercept(decayedHeat, state.currentVehicle.evasionBonus)
        val pendingChase = if (interceptCheck > 0) {
            ChaseEngine.createEncounter(decayedHeat)
        } else null

        // Random event (only if no chase)
        val event = if (!isGameOver && pendingChase == null) EventEngine.maybeGenerateEvent() else null

        // Check for rival gang encounter
        val gangEncounter = if (!isGameOver && pendingChase == null) {
            GangEngine.checkEncounter(
                destination,
                state.reputation[destination] ?: 0,
                state.vehicleHpPercent
            )
        } else null

        // Generate headline for next day
        val headline = if (!isGameOver) HeadlineEngine.generateHeadline(hot, crackdown) else null

        // Track visited neighborhoods
        val visited = state.neighborhoodsVisited + destination

        // Post-intercept heat (chase handles it)
        val postHeat = decayedHeat

        var newState = state.copy(
            day = nextDay,
            currentNeighborhood = destination,
            cash = (state.cash + speakeasyIncome - loanPenalty).coerceAtLeast(0),
            prices = repPrices,
            previousPrices = state.prices,
            hotCommodity = hot,
            crackdownGood = crackdown,
            heat = postHeat,
            gameOver = isGameOver,
            pendingChase = pendingChase,
            chaseResult = null,
            pendingEvent = if (gangEncounter == null) event else null,
            pendingEncounter = gangEncounter,
            loanShark = updatedLoan,
            headline = headline,
            neighborhoodsVisited = visited,
            chasesEncountered = if (pendingChase != null) state.chasesEncountered + 1 else state.chasesEncountered
        )

        // Check achievements
        val achievement = AchievementEngine.checkAchievements(newState)
        if (achievement != null) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + achievement,
                pendingAchievement = achievement
            )
        }

        return newState
    }

    /**
     * Player chose to FIGHT during a chase encounter.
     */
    fun fightChase(state: GameState): GameState {
        val encounter = state.pendingChase ?: return state
        val result = ChaseEngine.fight(state, encounter)
        return applyChaseResult(state, result)
    }

    /**
     * Player chose to FLEE during a chase encounter.
     */
    fun fleeChase(state: GameState): GameState {
        val encounter = state.pendingChase ?: return state
        val result = ChaseEngine.flee(state, encounter)
        return applyChaseResult(state, result)
    }

    private fun applyChaseResult(state: GameState, result: ChaseResult): GameState {
        return when (result) {
            is ChaseResult.FightWon -> {
                state.copy(
                    pendingChase = null,
                    chaseResult = result,
                    heat = (state.heat - result.heatReduced).coerceAtLeast(0),
                    vehicleHp = (state.vehicleHp - result.vehicleDamage).coerceAtLeast(0),
                    chasesWon = state.chasesWon + 1
                )
            }
            is ChaseResult.FightLost -> {
                val updatedInventory = applyGoodsLoss(state.inventory, result.goodsLost)
                state.copy(
                    pendingChase = null,
                    chaseResult = result,
                    cash = (state.cash - result.cashFine).coerceAtLeast(0),
                    heat = (state.heat + result.heatGained).coerceAtMost(100),
                    vehicleHp = (state.vehicleHp - result.vehicleDamage).coerceAtLeast(0),
                    inventory = updatedInventory
                )
            }
            is ChaseResult.FleeSuccess -> {
                val updatedInventory = applyGoodsLoss(state.inventory, result.goodsDropped)
                state.copy(
                    pendingChase = null,
                    chaseResult = result,
                    vehicleHp = (state.vehicleHp - result.vehicleDamage).coerceAtLeast(0),
                    inventory = updatedInventory
                )
            }
            is ChaseResult.FleeFailed -> {
                val updatedInventory = applyGoodsLoss(state.inventory, result.goodsLost)
                state.copy(
                    pendingChase = null,
                    chaseResult = result,
                    cash = (state.cash - result.cashFine).coerceAtLeast(0),
                    heat = (state.heat + result.heatGained).coerceAtMost(100),
                    vehicleHp = (state.vehicleHp - result.vehicleDamage).coerceAtLeast(0),
                    inventory = updatedInventory
                )
            }
        }
    }

    /**
     * Dismiss the chase result dialog.
     */
    fun dismissChaseResult(state: GameState): GameState {
        return state.copy(chaseResult = null)
    }

    /**
     * Apply gang encounter result to the game state.
     */
    fun applyEncounter(state: GameState): GameState {
        val encounter = state.pendingEncounter ?: return state
        return when (encounter) {
            is EncounterResult.ShakenDown -> {
                val updatedInventory = applyGoodsLoss(state.inventory, encounter.goodsLost)
                state.copy(
                    cash = (state.cash - encounter.cashLost).coerceAtLeast(0),
                    inventory = updatedInventory,
                    pendingEncounter = null
                )
            }
            is EncounterResult.Intimidated -> {
                state.copy(
                    heat = (state.heat + encounter.heatGained).coerceAtMost(100),
                    pendingEncounter = null
                )
            }
            is EncounterResult.FoughtOff -> {
                val newRep = state.reputation.toMutableMap()
                val currentRep = newRep[encounter.gang.homeNeighborhood] ?: 0
                newRep[encounter.gang.homeNeighborhood] = (currentRep + encounter.reputationGained).coerceAtMost(100)

                state.copy(
                    vehicleHp = (state.vehicleHp - encounter.vehicleDamage).coerceAtLeast(0),
                    reputation = newRep,
                    pendingEncounter = null,
                    gangsFoughtOff = state.gangsFoughtOff + 1
                )
            }
        }
    }

    /**
     * Take a loan from the shark.
     */
    fun takeLoan(state: GameState, amount: Int): GameState? {
        if (state.loanShark.hasActiveLoan) return null
        if (amount > state.loanShark.maxLoan) return null
        if (amount <= 0) return null

        return state.copy(
            cash = state.cash + amount,
            loanShark = state.loanShark.copy(
                debt = amount,
                hasActiveLoan = true,
                daysUntilThreat = 0
            )
        )
    }

    /**
     * Repay loan shark debt.
     */
    fun repayLoan(state: GameState, amount: Int): GameState? {
        if (!state.loanShark.hasActiveLoan) return null
        if (amount > state.cash) return null
        if (amount <= 0) return null

        val newDebt = (state.loanShark.debt - amount).coerceAtLeast(0)
        val paidOff = newDebt == 0

        var newState = state.copy(
            cash = state.cash - amount,
            loanShark = state.loanShark.copy(
                debt = newDebt,
                hasActiveLoan = !paidOff,
                daysUntilThreat = if (paidOff) 0 else state.loanShark.daysUntilThreat
            )
        )

        // Check debt free achievement
        if (paidOff && Achievement.DEBT_FREE !in newState.earnedAchievements) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + Achievement.DEBT_FREE,
                pendingAchievement = Achievement.DEBT_FREE
            )
        }

        return newState
    }

    /**
     * Invest in a speakeasy in a neighborhood.
     */
    fun investSpeakeasy(state: GameState, neighborhood: Neighborhood): GameState? {
        val speakeasy = state.speakeasies[neighborhood] ?: return null
        val upgradeCost = Speakeasy.getUpgradeCost(speakeasy.investmentLevel)
        if (upgradeCost > state.cash) return null
        if (speakeasy.investmentLevel >= 3) return null

        val updatedSpeakeasy = speakeasy.copy(
            investmentLevel = speakeasy.investmentLevel + 1,
            totalInvested = speakeasy.totalInvested + upgradeCost
        )

        var newState = state.copy(
            cash = state.cash - upgradeCost,
            speakeasies = state.speakeasies + (neighborhood to updatedSpeakeasy)
        )

        // Check speakeasy achievements
        val achievement = AchievementEngine.checkAchievements(newState)
        if (achievement != null) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + achievement,
                pendingAchievement = achievement
            )
        }

        return newState
    }

    fun dismissAchievement(state: GameState): GameState {
        return state.copy(pendingAchievement = null)
    }

    /**
     * Claim an earned achievement, collecting the cash reward.
     */
    fun claimAchievement(state: GameState, achievement: Achievement): GameState? {
        // Must be earned but not yet claimed
        if (achievement !in state.earnedAchievements) return null
        if (achievement in state.claimedAchievements) return null

        return state.copy(
            cash = state.cash + achievement.cashReward,
            claimedAchievements = state.claimedAchievements + achievement
        )
    }

    fun applyEvent(state: GameState, event: RandomEvent): GameState {
        var newState = when (event) {
            is RandomEvent.FindStash -> {
                val item = state.inventory[event.good] ?: InventoryItem()
                val canFit = state.freeCapacity
                val actualQty = minOf(event.quantity, canFit)
                if (actualQty <= 0) return state.copy(pendingEvent = null)
                state.copy(
                    inventory = state.inventory + (event.good to item.copy(
                        quantity = item.quantity + actualQty
                    )),
                    pendingEvent = null
                )
            }
            is RandomEvent.TipOff -> state.copy(
                heat = (state.heat - event.heatReduction).coerceAtLeast(0),
                pendingEvent = null
            )
            is RandomEvent.BigSale -> state.copy(
                cash = state.cash + event.cashBonus,
                pendingEvent = null
            )
            is RandomEvent.CapacityUpgrade -> state.copy(
                cash = state.cash + event.cashBonus,
                pendingEvent = null
            )
            is RandomEvent.Crackdown -> state.copy(
                heat = (state.heat + event.heatIncrease).coerceAtMost(100),
                pendingEvent = null
            )
            is RandomEvent.Shakedown -> state.copy(
                cash = (state.cash - event.cashLoss).coerceAtLeast(0),
                pendingEvent = null
            )
            is RandomEvent.Spoilage -> {
                val item = state.inventory[event.good] ?: return state.copy(pendingEvent = null)
                val actualLost = minOf(event.quantityLost, item.quantity)
                if (actualLost <= 0) return state.copy(pendingEvent = null)
                val costReduction = if (item.quantity > 0) {
                    (item.totalCostBasis.toLong() * actualLost / item.quantity).toInt()
                } else 0
                state.copy(
                    inventory = state.inventory + (event.good to item.copy(
                        quantity = item.quantity - actualLost,
                        totalCostBasis = (item.totalCostBasis - costReduction).coerceAtLeast(0)
                    )),
                    pendingEvent = null
                )
            }
            is RandomEvent.Informant -> state.copy(
                heat = (state.heat + event.heatIncrease).coerceAtMost(100),
                pendingEvent = null
            )
        }

        // Check achievements after event
        val achievement = AchievementEngine.checkAchievements(newState)
        if (achievement != null) {
            newState = newState.copy(
                earnedAchievements = newState.earnedAchievements + achievement,
                pendingAchievement = achievement
            )
        }

        return newState
    }

    private fun applyGoodsLoss(
        inventory: Map<Good, InventoryItem>,
        losses: Map<Good, Int>
    ): Map<Good, InventoryItem> {
        return inventory.mapValues { (good, item) ->
            val lost = losses[good] ?: 0
            if (lost > 0 && item.quantity > 0) {
                val costReduction = (item.totalCostBasis.toLong() * lost / item.quantity).toInt()
                item.copy(
                    quantity = (item.quantity - lost).coerceAtLeast(0),
                    totalCostBasis = (item.totalCostBasis - costReduction).coerceAtLeast(0)
                )
            } else item
        }
    }
}
