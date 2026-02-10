package com.roaringtrades.game.engine

import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.RandomEvent
import kotlin.random.Random

object EventEngine {

    private const val EVENT_CHANCE = 0.25f

    fun maybeGenerateEvent(): RandomEvent? {
        if (Random.nextFloat() > EVENT_CHANCE) return null

        val allEvents = listOf(
            RandomEvent.FindStash(
                good = Good.entries.random(),
                quantity = Random.nextInt(2, 5)
            ),
            RandomEvent.TipOff(
                heatReduction = Random.nextInt(8, 15)
            ),
            RandomEvent.BigSale(
                cashBonus = Random.nextInt(100, 300)
            ),
            RandomEvent.CapacityUpgrade(
                cashBonus = Random.nextInt(150, 400)
            ),
            RandomEvent.Crackdown(
                heatIncrease = Random.nextInt(15, 30)
            ),
            RandomEvent.Shakedown(
                cashLoss = Random.nextInt(150, 500)
            ),
            RandomEvent.Spoilage(
                good = Good.entries.random(),
                quantityLost = Random.nextInt(2, 6)
            ),
            RandomEvent.Informant(
                heatIncrease = Random.nextInt(20, 40)
            )
        )

        return allEvents.random()
    }
}
