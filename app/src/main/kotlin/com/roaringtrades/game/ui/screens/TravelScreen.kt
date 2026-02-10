package com.roaringtrades.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.model.Neighborhood
import com.roaringtrades.game.model.RivalGang
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen
import com.roaringtrades.game.ui.viewmodel.GameViewModel

@Composable
fun TravelScreen(viewModel: GameViewModel, onTraveled: () -> Unit = {}) {
    val state by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Your Destination",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Currently at: ${state.currentNeighborhood.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Day ${state.day} of ${state.maxDays} \u2022 Traveling uses 1 day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Neighborhood grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Neighborhood.entries) { neighborhood ->
                val isCurrent = neighborhood == state.currentNeighborhood

                Card(
                    onClick = {
                        if (!isCurrent) {
                            viewModel.travel(neighborhood)
                            onTraveled()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp),
                    enabled = !isCurrent,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) {
                            Gold.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = if (isCurrent) {
                        CardDefaults.outlinedCardBorder()
                    } else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isCurrent) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Current location",
                                tint = Gold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.height(2.dp))
                        }

                        Text(
                            neighborhood.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            neighborhood.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Risk/reward indicators
                        Spacer(Modifier.height(4.dp))
                        if (neighborhood.heatModifier >= 1.3f) {
                            Text(
                                "\u26A0\uFE0F High risk, premium prices",
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed,
                                textAlign = TextAlign.Center
                            )
                        } else if (neighborhood.heatModifier <= 0.85f) {
                            Text(
                                "\u2705 Low risk, lower prices",
                                style = MaterialTheme.typography.labelSmall,
                                color = RichGreen,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                "\u2696\uFE0F Moderate risk",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                        // Show gang territory
                        RivalGang.getGangForNeighborhood(neighborhood)?.let { gang ->
                            Text(
                                "${gang.emoji} ${gang.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (isCurrent) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "You are here",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold
                            )
                        }
                    }
                }
            }
        }
    }
}
