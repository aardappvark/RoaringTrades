package com.roaringtrades.game.engine

import com.roaringtrades.game.model.*

object AchievementEngine {

    /**
     * Check all achievements and return the first newly unlocked one (if any).
     */
    fun checkAchievements(state: GameState): Achievement? {
        val unlocked = state.earnedAchievements

        // Check each achievement in order
        if (Achievement.FIRST_TRADE !in unlocked && state.totalBuysCount >= 1) {
            return Achievement.FIRST_TRADE
        }
        if (Achievement.TEN_K_CLUB !in unlocked && state.netWorth >= 10000) {
            return Achievement.TEN_K_CLUB
        }
        if (Achievement.FIFTY_K_CLUB !in unlocked && state.netWorth >= 50000) {
            return Achievement.FIFTY_K_CLUB
        }
        if (Achievement.HUNDRED_K_CLUB !in unlocked && state.netWorth >= 100000) {
            return Achievement.HUNDRED_K_CLUB
        }
        if (Achievement.SURVIVOR !in unlocked && state.chasesEncountered >= 5) {
            return Achievement.SURVIVOR
        }
        if (Achievement.SPEED_DEMON !in unlocked &&
            (state.currentVehicle == Vehicle.SPEEDBOAT || state.currentVehicle == Vehicle.ZEPPELIN)) {
            return Achievement.SPEED_DEMON
        }
        if (Achievement.SKY_KING !in unlocked && state.currentVehicle == Vehicle.ZEPPELIN) {
            return Achievement.SKY_KING
        }
        if (Achievement.SPEAKEASY_OWNER !in unlocked &&
            state.speakeasies.values.any { it.investmentLevel >= 1 }) {
            return Achievement.SPEAKEASY_OWNER
        }
        if (Achievement.SPEAKEASY_MOGUL !in unlocked &&
            state.speakeasies.values.any { it.investmentLevel >= 3 }) {
            return Achievement.SPEAKEASY_MOGUL
        }
        if (Achievement.FIGHT_CLUB !in unlocked && state.chasesWon >= 3) {
            return Achievement.FIGHT_CLUB
        }
        if (Achievement.WORLD_TRAVELER !in unlocked &&
            state.neighborhoodsVisited.size >= Neighborhood.entries.size) {
            return Achievement.WORLD_TRAVELER
        }
        if (Achievement.STREET_TOUGH !in unlocked && state.gangsFoughtOff >= 1) {
            return Achievement.STREET_TOUGH
        }
        if (Achievement.HOT_STREAK !in unlocked && state.consecutiveProfitTrades >= 5) {
            return Achievement.HOT_STREAK
        }
        if (Achievement.UNTOUCHABLE !in unlocked && state.gameOver && state.heat == 0) {
            return Achievement.UNTOUCHABLE
        }
        if (Achievement.AL_CAPONE !in unlocked && state.netWorth >= 500000) {
            return Achievement.AL_CAPONE
        }

        return null
    }
}
