package com.roaringtrades.game.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.roaringtrades.game.model.LeaderboardEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LeaderboardManager(context: Context) {

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

    private val json = Json { ignoreUnknownKeys = true }

    fun getTopScores(limit: Int = 10): List<LeaderboardEntry> {
        val raw = prefs.getString(KEY_SCORES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<LeaderboardEntry>>(raw)
                .sortedByDescending { it.netWorth }
                .take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addScore(entry: LeaderboardEntry) {
        val current = getAllScores().toMutableList()
        current.add(entry)
        val sorted = current.sortedByDescending { it.netWorth }.take(MAX_ENTRIES)
        prefs.edit()
            .putString(KEY_SCORES, json.encodeToString(sorted))
            .apply()
    }

    fun getPlayerBest(walletAddress: String): LeaderboardEntry? {
        return getAllScores()
            .filter { it.walletAddress == walletAddress }
            .maxByOrNull { it.netWorth }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun getAllScores(): List<LeaderboardEntry> {
        val raw = prefs.getString(KEY_SCORES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<LeaderboardEntry>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val PREFS_NAME = "roaring_trades_leaderboard"
        private const val KEY_SCORES = "scores"
        private const val MAX_ENTRIES = 50
    }
}
