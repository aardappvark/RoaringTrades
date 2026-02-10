package com.roaringtrades.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.engine.ScoreCalculator
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen
import com.roaringtrades.game.ui.viewmodel.GameViewModel

@Composable
fun GameOverScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()
    val walletAddress by viewModel.walletAddress.collectAsState()
    val result = ScoreCalculator.calculateResult(state.cash, state.inventoryValue)
    val profitColor = if (result.profitFromStart >= 0) RichGreen else DangerRed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Game Over!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Gold
        )

        Spacer(Modifier.height(24.dp))

        // Rank
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Gold.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Rank",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    result.rank,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Score details
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ScoreRow("Final Cash", "\$${String.format("%,d", result.finalCash)}")
                ScoreRow("Inventory Value", "\$${String.format("%,d", result.inventoryValue)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ScoreRow(
                    "Net Worth",
                    "\$${String.format("%,d", result.netWorth)}",
                    fontWeight = FontWeight.Bold
                )
                ScoreRow(
                    "Profit",
                    "${if (result.profitFromStart >= 0) "+" else ""}\$${String.format("%,d", result.profitFromStart)}",
                    valueColor = profitColor,
                    fontWeight = FontWeight.Bold
                )
                ScoreRow(
                    "Return",
                    "${String.format("%.1f", result.percentReturn)}%",
                    valueColor = profitColor
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Save score if wallet connected
        if (walletAddress != null) {
            Button(
                onClick = {
                    viewModel.saveScore()
                    viewModel.newGame()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Score & Play Again")
            }
        } else {
            Button(
                onClick = { viewModel.newGame() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play Again")
            }
        }
    }
}

@Composable
private fun ScoreRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = valueColor
        )
    }
}
