package com.roaringtrades.game

object AppConfig {

    object Identity {
        const val NAME = "Roaring Trades"
        const val URI = "https://aardappvark.github.io/RoaringTrades"
        const val ICON_URI = "favicon.png"
    }

    object Wallet {
        const val CLUSTER = "mainnet-beta"
        const val CHAIN = "solana:mainnet"
    }

    object Rpc {
        /**
         * Helius free-tier RPC endpoint for SGT verification.
         * Set HELIUS_API_KEY in local.properties and pass at runtime.
         * Falls back to public mainnet-beta RPC if not set.
         */
        private const val HELIUS_MAINNET_BASE = "https://mainnet.helius-rpc.com/?api-key="

        /** Construct the full Helius RPC URL with the given API key */
        fun heliusUrl(apiKey: String): String = "$HELIUS_MAINNET_BASE$apiKey"
    }

    object Game {
        const val STARTING_CASH = 1500
        const val MAX_DAYS = 30
        const val SGT_BONUS_DAYS = 5
        const val STARTING_CAPACITY = 100
    }
}
