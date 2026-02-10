package com.roaringtrades.game.model

sealed class RandomEvent(
    val title: String,
    val isGood: Boolean
) {
    data class FindStash(val good: Good, val quantity: Int) : RandomEvent(
        title = "Lucky Find!",
        isGood = true
    ) {
        override val description: String
            get() = "You found $quantity units of ${good.displayName} hidden in an alley!"
    }

    data class TipOff(val heatReduction: Int) : RandomEvent(
        title = "Friendly Tip",
        isGood = true
    ) {
        override val description: String
            get() = "A friendly barkeep tipped you off about a patrol route. Heat reduced by $heatReduction!"
    }

    data class BigSale(val cashBonus: Int) : RandomEvent(
        title = "Big Payday!",
        isGood = true
    ) {
        override val description: String
            get() = "A wealthy patron paid top dollar for your finest stock! +$$cashBonus"
    }

    data class CapacityUpgrade(val cashBonus: Int) : RandomEvent(
        title = "Mechanic's Favor!",
        isGood = true
    ) {
        override val description: String
            get() = "A grateful mechanic gave you a discount voucher! +\$$cashBonus toward your next ride."
    }

    data class Crackdown(val heatIncrease: Int) : RandomEvent(
        title = "Crackdown!",
        isGood = false
    ) {
        override val description: String
            get() = "The authorities swept through the neighborhood! Heat increased by $heatIncrease."
    }

    data class Shakedown(val cashLoss: Int) : RandomEvent(
        title = "Shakedown!",
        isGood = false
    ) {
        override val description: String
            get() = "A rival crew shook you down for cash! Lost $$cashLoss."
    }

    data class Spoilage(val good: Good, val quantityLost: Int) : RandomEvent(
        title = "Spoiled Goods!",
        isGood = false
    ) {
        override val description: String
            get() = "$quantityLost units of ${good.displayName} went bad!"
    }

    data class Informant(val heatIncrease: Int) : RandomEvent(
        title = "Ratted Out!",
        isGood = false
    ) {
        override val description: String
            get() = "Someone talked to the authorities! Heat skyrocketed by $heatIncrease!"
    }

    abstract val description: String
}
