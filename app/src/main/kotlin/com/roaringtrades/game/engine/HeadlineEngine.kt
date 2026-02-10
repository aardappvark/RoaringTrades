package com.roaringtrades.game.engine

import com.roaringtrades.game.model.*
import kotlin.random.Random

object HeadlineEngine {

    fun generateHeadline(nextHot: Good, nextCrackdown: Good): Headline {
        val roll = Random.nextFloat()
        return when {
            roll < 0.30f -> generatePriceUpHint(nextHot)
            roll < 0.55f -> generatePriceDownHint(nextCrackdown)
            roll < 0.70f -> generateHeatWarning()
            roll < 0.85f -> generateGangNews()
            else -> generateGeneralNews()
        }
    }

    private fun generatePriceUpHint(good: Good): Headline {
        val text = when (good) {
            Good.BATHTUB_GIN -> "SOCIALITES DEMAND MORE GIN - Cocktail parties sweep the city!"
            Good.WHISKEY -> "WHISKEY SHORTAGE LOOMS - Irish imports running dry!"
            Good.RUM -> "CARIBBEAN CONNECTIONS CUT - Rum supply dwindles!"
            Good.MOONSHINE -> "MOUNTAIN MEN DISAPPEAR - Moonshine supplies vanish!"
            Good.CHAMPAGNE -> "FRENCH LUXURY IN DEMAND - High society thirsty for bubbles!"
        }
        return Headline(text = text, hint = HeadlineHint.PriceUp(good))
    }

    private fun generatePriceDownHint(good: Good): Headline {
        val text = when (good) {
            Good.BATHTUB_GIN -> "FEDS CRACK DOWN ON GIN - Multiple bathtub operations shut down!"
            Good.WHISKEY -> "WHISKEY WAREHOUSES SEIZED - Agents celebrate major haul!"
            Good.RUM -> "COAST GUARD INTERCEPTS RUM RUNNERS - Prices expected to tumble!"
            Good.MOONSHINE -> "MOONSHINE STILLS FOUND - Revenue agents on the warpath!"
            Good.CHAMPAGNE -> "CHAMPAGNE TRADE DISRUPTED - Luxury goods supply flooded!"
        }
        return Headline(text = text, hint = HeadlineHint.PriceDown(good))
    }

    private fun generateHeatWarning(): Headline {
        val neighborhood = Neighborhood.entries.random()
        val text = when (neighborhood) {
            Neighborhood.SOUTH_SIDE -> "SOUTH SIDE UNDER WATCH - Extra patrols deployed!"
            Neighborhood.NORTH_SIDE -> "NORTH SIDE CRACKDOWN - Wealthy residents demand action!"
            Neighborhood.WEST_SIDE -> "WEST SIDE WAREHOUSES WATCHED - Feds stake out industrial zone!"
            Neighborhood.DOWNTOWN -> "DOWNTOWN SWEEP PLANNED - Commissioner vows cleanup!"
            Neighborhood.THE_DOCKS -> "HARBOR PATROL DOUBLED - Coast Guard on high alert!"
            Neighborhood.UPTOWN -> "UPTOWN SPEAKEASIES TARGETED - Agents going undercover!"
        }
        return Headline(text = text, hint = HeadlineHint.HeatWarning(neighborhood))
    }

    private fun generateGangNews(): Headline {
        val gang = RivalGang.entries.random()
        val text = when (gang) {
            RivalGang.SOUTH_SIDE_BOYS -> "SOUTH SIDE BOYS EXPAND TERRITORY - Rival traders beware!"
            RivalGang.NORTH_SHORE_SYNDICATE -> "NORTH SHORE SYNDICATE FLEXES - High-end operations tighten!"
            RivalGang.DOCK_RATS -> "DOCK RATS CONTROL WATERFRONT - Traders pay the toll!"
            RivalGang.DOWNTOWN_OUTFIT -> "DOWNTOWN OUTFIT GROWING BOLD - Territory disputes heat up!"
        }
        return Headline(text = text, hint = HeadlineHint.GangActivity(gang))
    }

    private fun generateGeneralNews(): Headline {
        val headlines = listOf(
            "PROHIBITION ENFORCEMENT DOUBLED - President demands results!",
            "NEW SPEAKEASIES OPEN NIGHTLY - Chicago can't stop the party!",
            "JAZZ AGE IN FULL SWING - Music fills the underground!",
            "MAYOR PROMISES CLEAN STREETS - Election season rhetoric!",
            "STOCK MARKET SOARS - Wall Street celebrating!",
            "LABOR STRIKES SPREAD - Workers demand fair wages!",
            "BABE RUTH HITS ANOTHER - Yankees dominate the diamond!"
        )
        return Headline(text = headlines.random(), hint = HeadlineHint.GeneralNews)
    }
}
