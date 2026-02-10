package com.roaringtrades.game.model

data class Headline(
    val text: String,
    val hint: HeadlineHint
)

sealed class HeadlineHint {
    data class PriceUp(val good: Good) : HeadlineHint()
    data class PriceDown(val good: Good) : HeadlineHint()
    data class HeatWarning(val neighborhood: Neighborhood) : HeadlineHint()
    data class GangActivity(val gang: RivalGang) : HeadlineHint()
    object GeneralNews : HeadlineHint()
}
