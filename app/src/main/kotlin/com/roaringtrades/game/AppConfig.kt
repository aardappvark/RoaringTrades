package com.roaringtrades.game

object AppConfig {

    object Identity {
        const val NAME = "Roaring Trades"
        const val URI = "https://midnightrungames.github.io/RoaringTrades"
        const val ICON_URI = "favicon.png"
    }

    object Wallet {
        const val CLUSTER = "mainnet-beta"
        const val CHAIN = "solana:mainnet"
    }

    object Game {
        const val STARTING_CASH = 1500
        const val MAX_DAYS = 30
        const val STARTING_CAPACITY = 100
    }
}
