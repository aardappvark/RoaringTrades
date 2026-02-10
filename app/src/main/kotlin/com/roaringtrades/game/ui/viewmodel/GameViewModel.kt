package com.roaringtrades.game.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gamePrefs = GamePreferences(application)
    private val leaderboard = LeaderboardManager(application)

    private val _gameState = MutableStateFlow(GameEngine.newGame())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _walletAddress = MutableStateFlow(gamePrefs.getWalletAddress())
    val walletAddress: StateFlow<String?> = _walletAddress.asStateFlow()

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
        _gameState.value = GameEngine.newGame()
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
    }
}
