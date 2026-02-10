package com.roaringtrades.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.engine.ScoreCalculator
import com.roaringtrades.game.model.Achievement
import com.roaringtrades.game.model.LoanShark
import com.roaringtrades.game.model.Neighborhood
import com.roaringtrades.game.model.Speakeasy
import com.roaringtrades.game.ui.components.HeatIndicator
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen
import com.roaringtrades.game.ui.theme.SolanaPurple
import com.roaringtrades.game.ui.viewmodel.GameViewModel

@Composable
fun StatusScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()
    val walletAddress by viewModel.walletAddress.collectAsState()
    val scores = remember { viewModel.getTopScores() }
    val rank = ScoreCalculator.getRank(state.netWorth)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Financial summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Financial Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FinStatItem("Cash", "\$${String.format("%,d", state.cash)}")
                    FinStatItem("Inventory", "\$${String.format("%,d", state.inventoryValue)}")
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FinStatItem("Net Worth", "\$${String.format("%,d", state.netWorth)}")
                    val profit = state.profitFromStart
                    FinStatItem("Profit", "${if (profit >= 0) "+" else ""}\$${String.format("%,d", profit)}", if (profit >= 0) RichGreen else DangerRed)
                }
                if (state.loanShark.hasActiveLoan || state.totalSpeakeasyIncome > 0) {
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (state.loanShark.hasActiveLoan) FinStatItem("Debt", "-\$${String.format("%,d", state.loanShark.debt)}", DangerRed)
                        if (state.totalSpeakeasyIncome > 0) FinStatItem("Speakeasies", "+\$${state.totalSpeakeasyIncome}/day", RichGreen)
                    }
                }
            }
        }

        // Rank
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.1f))) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Rank", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(rank, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Gold)
            }
        }

        // Heat
        Card(modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(16.dp)) { HeatIndicator(heat = state.heat) } }

        // Loan Shark
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${LoanShark.LOAN_SHARK_EMOJI} ${LoanShark.LOAN_SHARK_NAME} - Loan Shark", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (state.loanShark.hasActiveLoan) {
                    Text("Debt: \$${String.format("%,d", state.loanShark.debt)}", color = DangerRed, fontWeight = FontWeight.Bold)
                    Text("Daily interest: 10% (\$${state.loanShark.dailyInterest}/day)", style = MaterialTheme.typography.bodySmall)
                    Text("Status: ${state.loanShark.threatLevel} ${state.loanShark.threatEmoji}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val halfDebt = state.loanShark.debt / 2
                        if (halfDebt > 0 && halfDebt <= state.cash) {
                            OutlinedButton(onClick = { viewModel.repayLoan(halfDebt) }, modifier = Modifier.weight(1f)) { Text("Pay \$${String.format("%,d", halfDebt)}") }
                        }
                        if (state.loanShark.debt <= state.cash) {
                            Button(onClick = { viewModel.repayLoan(state.loanShark.debt) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = RichGreen)) { Text("Pay All \$${String.format("%,d", state.loanShark.debt)}") }
                        }
                    }
                } else {
                    Text("\"Need some cash, friend? I got you.\"", style = MaterialTheme.typography.bodySmall)
                    Text("10% daily interest. Don't be late.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.takeLoan(1000) }, modifier = Modifier.weight(1f)) { Text("Borrow \$1K") }
                        OutlinedButton(onClick = { viewModel.takeLoan(3000) }, modifier = Modifier.weight(1f)) { Text("Borrow \$3K") }
                        Button(onClick = { viewModel.takeLoan(5000) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Borrow \$5K") }
                    }
                }
            }
        }

        // Speakeasies
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("\uD83C\uDF78 Speakeasies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (state.totalSpeakeasyIncome > 0) {
                    Text("Daily income: +\$${state.totalSpeakeasyIncome}", style = MaterialTheme.typography.bodySmall, color = RichGreen)
                }
                Spacer(Modifier.height(8.dp))
                Neighborhood.entries.forEach { neighborhood ->
                    val speakeasy = state.speakeasies[neighborhood] ?: Speakeasy(neighborhood)
                    val upgradeCost = Speakeasy.getUpgradeCost(speakeasy.investmentLevel)
                    val canAfford = upgradeCost <= state.cash
                    val isMaxed = speakeasy.investmentLevel >= 3
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${speakeasy.emoji} ${speakeasy.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("${neighborhood.displayName} - Tier ${speakeasy.investmentLevel}${if (speakeasy.dailyIncome > 0) " (+\$${speakeasy.dailyIncome}/day)" else ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!isMaxed) {
                            Button(onClick = { viewModel.investSpeakeasy(neighborhood) }, enabled = canAfford, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                                Text(if (speakeasy.investmentLevel == 0) "Open \$${String.format("%,d", upgradeCost)}" else "Up \$${String.format("%,d", upgradeCost)}", style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Text("\uD83C\uDF1F MAX", style = MaterialTheme.typography.labelSmall, color = Gold, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (neighborhood != Neighborhood.entries.last()) HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        // Achievements
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "\uD83C\uDFC6 Achievements (${state.earnedAchievements.size}/${Achievement.entries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.unclaimedCount > 0) {
                        Badge(containerColor = DangerRed) {
                            Text("${state.unclaimedCount} to claim")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Ready to claim (earned but unclaimed) — shown first with gold highlight
                val readyToClaim = Achievement.entries.filter { it in state.earnedAchievements && it !in state.claimedAchievements }
                val claimed = Achievement.entries.filter { it in state.claimedAchievements }
                val locked = Achievement.entries.filter { it !in state.earnedAchievements }

                readyToClaim.forEach { a ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("${a.emoji} ", style = MaterialTheme.typography.bodyMedium)
                                Column {
                                    Text(a.displayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Gold)
                                    Text(a.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Button(
                                onClick = { viewModel.claimAchievement(a) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Gold)
                            ) {
                                Text("+\$${String.format("%,d", a.cashReward)}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Claimed achievements
                claimed.forEach { a ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${a.emoji} ", style = MaterialTheme.typography.bodyMedium)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(a.displayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = RichGreen)
                            Text(a.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("\u2705", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if ((readyToClaim.isNotEmpty() || claimed.isNotEmpty()) && locked.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }

                // Locked achievements
                locked.forEach { a ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("\uD83D\uDD12 ", style = MaterialTheme.typography.bodyMedium)
                        Column {
                            Text("???", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(a.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }

        // Reputation
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("\uD83E\uDD1D Reputation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Neighborhood.entries.forEach { n ->
                    val rep = state.reputation[n] ?: 0
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(n.displayName, style = MaterialTheme.typography.bodySmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(progress = { rep / 100f }, modifier = Modifier.width(60.dp).height(6.dp), color = Gold)
                            Spacer(Modifier.width(8.dp))
                            Text("$rep", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Wallet
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Wallet, contentDescription = null, tint = SolanaPurple, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Wallet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                walletAddress?.let { Text("Connected: ${viewModel.getShortWalletAddress()}", style = MaterialTheme.typography.bodyMedium, color = SolanaPurple) }
            }
        }

        // Leaderboard
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Leaderboard, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Leaderboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                if (scores.isEmpty()) {
                    Text("No scores yet. Complete a game!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    scores.forEachIndexed { i, entry ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${i + 1}. ${entry.shortAddress}", style = MaterialTheme.typography.bodyMedium)
                            Text("\$${String.format("%,d", entry.netWorth)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Gold)
                        }
                        if (i < scores.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }

        // New Game
        Button(onClick = { viewModel.newGame() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("New Game")
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FinStatItem(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
