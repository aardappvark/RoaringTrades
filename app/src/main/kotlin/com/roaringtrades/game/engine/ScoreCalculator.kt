package com.roaringtrades.game.engine

import com.roaringtrades.game.AppConfig

object ScoreCalculator {

    data class GameResult(
        val finalCash: Int,
        val inventoryValue: Int,
        val netWorth: Int,
        val rank: String,
        val profitFromStart: Int,
        val percentReturn: Float
    )

    fun calculateResult(
        cash: Int,
        inventoryValue: Int,
        startingCash: Int = AppConfig.Game.STARTING_CASH
    ): GameResult {
        val netWorth = cash + inventoryValue
        val profit = netWorth - startingCash
        val percentReturn = if (startingCash > 0) {
            (profit.toFloat() / startingCash) * 100f
        } else 0f

        val rank = getRank(netWorth)

        return GameResult(
            finalCash = cash,
            inventoryValue = inventoryValue,
            netWorth = netWorth,
            rank = rank,
            profitFromStart = profit,
            percentReturn = percentReturn
        )
    }

    fun getRank(netWorth: Int): String = when {
        netWorth < 2000 -> "Street Peddler"
        netWorth < 5000 -> "Small-Time Hustler"
        netWorth < 15000 -> "Neighborhood Trader"
        netWorth < 50000 -> "District Boss"
        netWorth < 150000 -> "Speakeasy King"
        netWorth < 500000 -> "Chicago Kingpin"
        else -> "Al Capone"
    }
}
