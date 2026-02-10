package com.roaringtrades.game.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val walletAddress: String,
    val shortAddress: String,
    val netWorth: Int,
    val rank: String,
    val timestamp: Long
)
