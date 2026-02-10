package com.roaringtrades.game.engine

import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.Neighborhood
import kotlin.random.Random

object PriceEngine {

    fun generatePrices(
        neighborhood: Neighborhood,
        hotCommodity: Good?,
        crackdownGood: Good?
    ): Map<Good, Int> {
        return Good.entries.associateWith { good ->
            var price = good.basePrice.toFloat()

            // Neighborhood modifier
            price *= getNeighborhoodModifier(neighborhood, good)

            // Random fluctuation: +/- (35% * volatility)
            val fluctuation = 1.0f + (Random.nextFloat() - 0.5f) * good.volatility * 0.7f
            price *= fluctuation

            // Hot commodity: moderate price spike (1.4-1.8x)
            if (good == hotCommodity) {
                price *= (1.4f + Random.nextFloat() * 0.4f)
            }

            // Crackdown: price drops (0.4-0.65x)
            if (good == crackdownGood) {
                price *= (0.4f + Random.nextFloat() * 0.25f)
            }

            price.toInt().coerceIn(good.minPrice, good.maxPrice)
        }
    }

    private fun getNeighborhoodModifier(neighborhood: Neighborhood, good: Good): Float {
        return when (neighborhood) {
            Neighborhood.SOUTH_SIDE -> when (good) {
                Good.BATHTUB_GIN -> 0.7f
                Good.MOONSHINE -> 0.8f
                else -> 1.0f
            }
            Neighborhood.NORTH_SIDE -> when (good) {
                Good.CHAMPAGNE -> 1.3f
                Good.WHISKEY -> 1.2f
                Good.BATHTUB_GIN -> 0.8f
                else -> 1.1f
            }
            Neighborhood.WEST_SIDE -> when (good) {
                Good.MOONSHINE -> 1.1f
                Good.RUM -> 0.9f
                else -> 1.0f
            }
            Neighborhood.DOWNTOWN -> when (good) {
                Good.CHAMPAGNE -> 1.4f
                Good.WHISKEY -> 1.3f
                Good.BATHTUB_GIN -> 1.2f
                else -> 1.2f
            }
            Neighborhood.THE_DOCKS -> when (good) {
                Good.RUM -> 0.6f
                Good.CHAMPAGNE -> 0.7f
                Good.MOONSHINE -> 1.2f
                else -> 0.9f
            }
            Neighborhood.UPTOWN -> when (good) {
                Good.WHISKEY -> 1.1f
                Good.CHAMPAGNE -> 1.2f
                Good.BATHTUB_GIN -> 0.9f
                else -> 1.0f
            }
        }
    }

    fun selectDailySpecials(): Pair<Good, Good> {
        val hot = Good.entries.random()
        var crackdown = Good.entries.random()
        while (crackdown == hot) {
            crackdown = Good.entries.random()
        }
        return Pair(hot, crackdown)
    }
}
