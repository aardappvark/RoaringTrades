package com.roaringtrades.game.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.model.Vehicle
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen
import com.roaringtrades.game.ui.viewmodel.GameViewModel

@Composable
fun GarageScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Garage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Current ride: ${state.currentVehicle.emoji} ${state.currentVehicle.displayName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Cash: \$${String.format("%,d", state.cash)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Current vehicle stats with HP
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                border = BorderStroke(2.dp, Gold)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "${state.currentVehicle.emoji} ${state.currentVehicle.displayName} (Current)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.currentVehicle.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    VehicleStats(state.currentVehicle)

                    // HP Bar
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "\u2764\uFE0F HP: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { state.vehicleHpPercent },
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp),
                            color = when {
                                state.vehicleHpPercent >= 0.6f -> RichGreen
                                state.vehicleHpPercent >= 0.3f -> Gold
                                else -> DangerRed
                            },
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${state.vehicleHp}/${state.currentVehicle.maxHp}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Repair button
                    if (state.isVehicleDamaged) {
                        Spacer(Modifier.height(8.dp))
                        val canAffordRepair = state.repairCost <= state.cash
                        Button(
                            onClick = { viewModel.repairVehicle() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = canAffordRepair,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RichGreen
                            )
                        ) {
                            Text(
                                "\uD83D\uDD27 Repair for \$${String.format("%,d", state.repairCost)}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (!canAffordRepair) {
                            Text(
                                "Need \$${String.format("%,d", state.repairCost - state.cash)} more",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Available Upgrades",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
        }

        // Vehicle list
        items(Vehicle.entries.filter { it != state.currentVehicle }) { vehicle ->
            val canAfford = vehicle.price <= state.cash
            val isUpgrade = vehicle.capacity > state.currentVehicle.capacity
            val isOwned = vehicle.ordinal < state.currentVehicle.ordinal

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOwned) {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${vehicle.emoji} ${vehicle.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (vehicle.price > 0) {
                            Text(
                                "\$${String.format("%,d", vehicle.price)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (canAfford) RichGreen else DangerRed
                            )
                        } else {
                            Text(
                                "Free",
                                style = MaterialTheme.typography.titleMedium,
                                color = RichGreen
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        vehicle.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))
                    VehicleStats(vehicle)

                    // Comparison with current
                    if (!isOwned) {
                        val capacityDiff = vehicle.capacity - state.currentVehicle.capacity
                        val heatDiff = vehicle.heatDecayBonus - state.currentVehicle.heatDecayBonus
                        val evasionDiff = vehicle.evasionBonus - state.currentVehicle.evasionBonus

                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ComparisonChip(
                                diff = if (capacityDiff > 0) "+$capacityDiff" else "$capacityDiff",
                                isPositive = capacityDiff > 0
                            )
                            ComparisonChip(
                                diff = if (heatDiff > 0) "+$heatDiff" else "$heatDiff",
                                isPositive = heatDiff > 0
                            )
                            ComparisonChip(
                                diff = if (evasionDiff > 0) "+${(evasionDiff * 100).toInt()}%" else "${(evasionDiff * 100).toInt()}%",
                                isPositive = evasionDiff > 0
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    when {
                        isOwned -> {
                            Text(
                                "Already passed this ride",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        !isUpgrade -> {
                            Text(
                                "Not an upgrade",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        !canAfford -> {
                            Text(
                                "Need \$${String.format("%,d", vehicle.price - state.cash)} more",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerRed
                            )
                        }
                        else -> {
                            Button(
                                onClick = { viewModel.buyVehicle(vehicle) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold
                                )
                            ) {
                                Text(
                                    "Buy for \$${String.format("%,d", vehicle.price)}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun VehicleStats(vehicle: Vehicle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Capacity", value = "${vehicle.capacity}")
        StatItem(label = "Heat Decay", value = "+${5 + vehicle.heatDecayBonus}/day")
        StatItem(label = "HP", value = "${vehicle.maxHp}")
        StatItem(label = "Speed", value = "${vehicle.speed}/10")
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ComparisonChip(diff: String, isPositive: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isPositive) RichGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f)
    ) {
        Text(
            diff,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isPositive) RichGreen else DangerRed,
            fontWeight = FontWeight.Bold
        )
    }
}
