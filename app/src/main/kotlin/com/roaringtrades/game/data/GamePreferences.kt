package com.roaringtrades.game.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class GamePreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isWalletConnected(): Boolean = prefs.getBoolean(KEY_WALLET_CONNECTED, false)

    fun getWalletAddress(): String? = prefs.getString(KEY_WALLET_ADDRESS, null)

    fun getWalletName(): String? = prefs.getString(KEY_WALLET_NAME, null)

    fun getShortWalletAddress(): String {
        val address = getWalletAddress() ?: return ""
        return if (address.length > 10) {
            "${address.take(5)}...${address.takeLast(5)}"
        } else address
    }

    fun saveWalletConnection(publicKey: String, walletName: String?) {
        prefs.edit()
            .putBoolean(KEY_WALLET_CONNECTED, true)
            .putString(KEY_WALLET_ADDRESS, publicKey)
            .putString(KEY_WALLET_NAME, walletName ?: "Solana Wallet")
            .putLong(KEY_WALLET_CONNECTED_AT, System.currentTimeMillis())
            .apply()
    }

    fun disconnectWallet() {
        prefs.edit()
            .putBoolean(KEY_WALLET_CONNECTED, false)
            .remove(KEY_WALLET_ADDRESS)
            .remove(KEY_WALLET_NAME)
            .remove(KEY_WALLET_CONNECTED_AT)
            .apply()
    }

    fun hasAcceptedDisclaimer(): Boolean = prefs.getBoolean(KEY_DISCLAIMER_ACCEPTED, false)

    fun setDisclaimerAccepted() {
        prefs.edit().putBoolean(KEY_DISCLAIMER_ACCEPTED, true).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "roaring_trades_prefs"
        private const val KEY_WALLET_CONNECTED = "wallet_connected"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
        private const val KEY_WALLET_NAME = "wallet_name"
        private const val KEY_WALLET_CONNECTED_AT = "wallet_connected_at"
        private const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"
    }
}
