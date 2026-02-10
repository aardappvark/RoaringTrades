package com.roaringtrades.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.model.ChaseEncounter
import com.roaringtrades.game.model.ChaseResult
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen

@Composable
fun ChaseEncounterDialog(
    encounter: ChaseEncounter,
    onFight: () -> Unit,
    onFlee: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Can't dismiss - must choose */ },
        title = {
            Text(
                "\uD83D\uDEA8 The Heat Is On!",
                color = DangerRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    encounter.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pursuit Strength:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "${encounter.pursuitStrength}/100",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fight odds:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                encounter.fightOddsDescription,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("If caught:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Lose ${encounter.seizurePercentage}% goods",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onFight,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("\uD83E\uDD4A Fight", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onFlee,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Text("\uD83C\uDFC3 Flee", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun ChaseResultDialog(
    result: ChaseResult,
    onDismiss: () -> Unit
) {
    val (title, description, isGood) = when (result) {
        is ChaseResult.FightWon -> Triple(
            "\uD83E\uDD4A You Won the Fight!",
            "You shook off the pursuit! Heat reduced by ${result.heatReduced}.\nVehicle took ${result.vehicleDamage} damage.",
            true
        )
        is ChaseResult.FightLost -> {
            val lossText = result.goodsLost.entries.joinToString("\n") { (good, qty) ->
                "  ${good.emoji} ${good.displayName}: -$qty"
            }
            Triple(
                "\uD83D\uDC6E Fight Lost!",
                "They overpowered you!\nFined: \$${result.cashFine}\nVehicle damage: ${result.vehicleDamage}\nSeized:\n$lossText",
                false
            )
        }
        is ChaseResult.FleeSuccess -> {
            val dropText = if (result.goodsDropped.isNotEmpty()) {
                "\nDropped while fleeing:\n" + result.goodsDropped.entries.joinToString("\n") { (good, qty) ->
                    "  ${good.emoji} ${good.displayName}: -$qty"
                }
            } else ""
            Triple(
                "\uD83C\uDFC3 You Escaped!",
                "You outran the pursuit! Vehicle took ${result.vehicleDamage} damage.$dropText",
                true
            )
        }
        is ChaseResult.FleeFailed -> {
            val lossText = result.goodsLost.entries.joinToString("\n") { (good, qty) ->
                "  ${good.emoji} ${good.displayName}: -$qty"
            }
            Triple(
                "\uD83D\uDEA8 Caught!",
                "They caught up to you!\nFined: \$${result.cashFine}\nVehicle damage: ${result.vehicleDamage}\nSeized:\n$lossText",
                false
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                color = if (isGood) RichGreen else DangerRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Continue")
            }
        }
    )
}
