package com.roaringtrades.game.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.midmightbit.sgt.SgtChecker
import com.midmightbit.sgt.SgtConstants
import com.roaringtrades.game.AppConfig
import com.roaringtrades.game.data.GamePreferences
import com.roaringtrades.game.data.LeaderboardManager
import com.roaringtrades.game.engine.GameEngine
import com.roaringtrades.game.engine.ScoreCalculator
import com.roaringtrades.game.model.Achievement
import com.roaringtrades.game.model.GameState
import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.LeaderboardEntry
import com.roaringtrades.game.model.Neighborhood
import com.roaringtrades.game.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gamePrefs = GamePreferences(application)
    private val leaderboard = LeaderboardManager(application)

    private val _gameState = MutableStateFlow(GameEngine.newGame(hasSgt = gamePrefs.hasSgt()))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _walletAddress = MutableStateFlow(gamePrefs.getWalletAddress())
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

    // SGT (Seeker Genesis Token) verification state
    private val _hasSgt = MutableStateFlow(gamePrefs.hasSgt())
    val hasSgt: StateFlow<Boolean> = _hasSgt.asStateFlow()

    private val _sgtCheckInProgress = MutableStateFlow(false)
    val sgtCheckInProgress: StateFlow<Boolean> = _sgtCheckInProgress.asStateFlow()

    fun buy(good: Good, quantity: Int) {
        val newState = GameEngine.buy(_gameState.value, good, quantity)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    fun sell(good: Good, quantity: Int) {
        val newState = GameEngine.sell(_gameState.value, good, quantity)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    fun travel(destination: Neighborhood) {
        val newState = GameEngine.travel(_gameState.value, destination)
        _gameState.value = newState
    }

    fun buyVehicle(vehicle: Vehicle) {
        val newState = GameEngine.buyVehicle(_gameState.value, vehicle)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    fun repairVehicle() {
        val newState = GameEngine.repairVehicle(_gameState.value)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    // Pay off heat
    fun payoffHeat(heatToClear: Int) {
        val newState = GameEngine.payoffHeat(_gameState.value, heatToClear)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    // Chase encounter
    fun fightChase() {
        _gameState.value = GameEngine.fightChase(_gameState.value)
    }

    fun fleeChase() {
        _gameState.value = GameEngine.fleeChase(_gameState.value)
    }

    fun dismissChaseResult() {
        _gameState.value = GameEngine.dismissChaseResult(_gameState.value)
    }

    // Gang encounter
    fun dismissEncounter() {
        _gameState.value = GameEngine.applyEncounter(_gameState.value)
    }

    // Loan shark
    fun takeLoan(amount: Int) {
        val newState = GameEngine.takeLoan(_gameState.value, amount)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    fun repayLoan(amount: Int) {
        val newState = GameEngine.repayLoan(_gameState.value, amount)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    // Speakeasy
    fun investSpeakeasy(neighborhood: Neighborhood) {
        val newState = GameEngine.investSpeakeasy(_gameState.value, neighborhood)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    // Achievements
    fun dismissAchievement() {
        _gameState.value = GameEngine.dismissAchievement(_gameState.value)
    }

    fun claimAchievement(achievement: Achievement) {
        val newState = GameEngine.claimAchievement(_gameState.value, achievement)
        if (newState != null) {
            _gameState.value = newState
        }
    }

    fun dismissEvent() {
        val event = _gameState.value.pendingEvent ?: return
        _gameState.value = GameEngine.applyEvent(_gameState.value, event)
    }

    fun newGame() {
        _gameState.value = GameEngine.newGame(hasSgt = _hasSgt.value)
    }

    fun saveScore() {
        val address = _walletAddress.value ?: return
        val state = _gameState.value
        val result = ScoreCalculator.calculateResult(state.cash, state.inventoryValue)

        leaderboard.addScore(
            LeaderboardEntry(
                walletAddress = address,
                shortAddress = gamePrefs.getShortWalletAddress(),
                netWorth = result.netWorth,
                rank = result.rank,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun getTopScores(limit: Int = 10): List<LeaderboardEntry> {
        return leaderboard.getTopScores(limit)
    }

    fun setWalletConnected(publicKey: String, walletName: String?) {
        gamePrefs.saveWalletConnection(publicKey, walletName)
        _walletAddress.value = publicKey
    }

    fun isWalletConnected(): Boolean = gamePrefs.isWalletConnected()

    fun getShortWalletAddress(): String = gamePrefs.getShortWalletAddress()

    fun disconnectWallet() {
        gamePrefs.disconnectWallet()
        _walletAddress.value = null
        _hasSgt.value = false
    }

    /**
     * Check if the connected wallet holds a Seeker Genesis Token.
     * Uses cached result if checked within the last 24 hours.
     * On failure, preserves the last known cached value.
     *
     * @param rpcUrl Optional custom RPC URL (defaults to public mainnet-beta)
     */
    fun checkSgtStatus(rpcUrl: String = SgtConstants.DEFAULT_RPC_URL) {
        val address = _walletAddress.value ?: return
        Log.d(TAG, "SGT check starting for wallet: ${address.take(8)}...")

        if (!gamePrefs.shouldRecheckSgt()) {
            val cached = gamePrefs.hasSgt()
            Log.d(TAG, "SGT check skipped (cached within 24h) — hasSgt=$cached")
            _hasSgt.value = cached
            return
        }

        Log.d(TAG, "SGT check calling RPC: ${rpcUrl.take(50)}...")
        viewModelScope.launch {
            _sgtCheckInProgress.value = true
            val result = SgtChecker.checkWallet(address, rpcUrl)
            result.fold(
                onSuccess = { hasSgt ->
                    Log.d(TAG, "✅ SGT check SUCCESS — hasSgt=$hasSgt")
                    gamePrefs.setSgtStatus(hasSgt)
                    _hasSgt.value = hasSgt
                },
                onFailure = { error ->
                    Log.e(TAG, "❌ SGT check FAILED — ${error.message}", error)
                    // On error, keep the cached value
                    _hasSgt.value = gamePrefs.hasSgt()
                }
            )
            _sgtCheckInProgress.value = false
        }
    }

    companion object {
        private const val TAG = "RoaringTrades_SGT"
    }
}
