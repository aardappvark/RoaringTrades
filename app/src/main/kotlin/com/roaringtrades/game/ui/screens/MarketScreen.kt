package com.roaringtrades.game.ui.screens

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
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.InventoryItem
import com.roaringtrades.game.model.Neighborhood
import com.roaringtrades.game.model.RivalGang
import com.roaringtrades.game.ui.components.*
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen
import com.roaringtrades.game.ui.theme.WarmBrown
import com.roaringtrades.game.ui.viewmodel.GameViewModel

@Composable
fun MarketScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()

    // Priority dialogs (chase > result > ambush > event > achievement)
    state.pendingChase?.let { chase ->
        ChaseEncounterDialog(
            encounter = chase,
            onFight = { viewModel.fightChase() },
            onFlee = { viewModel.fleeChase() }
        )
    }

    state.chaseResult?.let { result ->
        ChaseResultDialog(
            result = result,
            onDismiss = { viewModel.dismissChaseResult() }
        )
    }

    state.pendingEncounter?.let { encounter ->
        GangEncounterDialog(
            encounter = encounter,
            onDismiss = { viewModel.dismissEncounter() }
        )
    }

    state.pendingEvent?.let { event ->
        EventDialog(
            event = event,
            onDismiss = { viewModel.dismissEvent() }
        )
    }

    state.pendingAchievement?.let { achievement ->
        AchievementPopup(
            achievement = achievement,
            onClaim = { viewModel.claimAchievement(achievement) },
            onDismiss = { viewModel.dismissAchievement() }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Headline banner
        state.headline?.let { headline ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WarmBrown.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        "\uD83D\uDCF0 ${headline.text}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = WarmBrown,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Top info bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Day ${state.day}/${state.maxDays}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "\$${String.format("%,d", state.cash)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${state.currentNeighborhood.displayName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            val heatMod = state.currentNeighborhood.heatModifier
                            if (heatMod >= 1.3f) {
                                Text(
                                    " \u26A0\uFE0F High risk, better prices",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DangerRed
                                )
                            } else if (heatMod <= 0.85f) {
                                Text(
                                    " \u2705 Low risk, lower prices",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RichGreen
                                )
                            }
                        }
                        Text(
                            "${state.currentVehicle.emoji} ${state.usedCapacity}/${state.capacity}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Vehicle HP + Speakeasy income + Debt row
                    if (state.isVehicleDamaged || state.totalSpeakeasyIncome > 0 || state.loanShark.hasActiveLoan) {
                        Spacer(Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (state.isVehicleDamaged) {
                                Text(
                                    "\u2764\uFE0F ${state.vehicleHp}/${state.currentVehicle.maxHp}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (state.vehicleHpPercent < 0.3f) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (state.totalSpeakeasyIncome > 0) {
                                Text(
                                    "\uD83C\uDF78 +\$${state.totalSpeakeasyIncome}/day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RichGreen
                                )
                            }
                            if (state.loanShark.hasActiveLoan) {
                                Text(
                                    "${state.loanShark.threatEmoji} \$${String.format("%,d", state.loanShark.debt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DangerRed
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    HeatIndicator(heat = state.heat)

                    // Pay off heat option (only show when heat > 0)
                    if (state.heat > 0) {
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "\uD83D\uDCB5 Pay off heat (\$${state.payoffCostPerHeat}/pt)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Small payoff: 10 heat or all if less
                                val smallPayoff = minOf(10, state.heat)
                                val smallCost = smallPayoff * state.payoffCostPerHeat
                                if (smallPayoff > 0 && smallPayoff < state.heat) {
                                    OutlinedButton(
                                        onClick = { viewModel.payoffHeat(smallPayoff) },
                                        enabled = smallCost <= state.cash,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("-${smallPayoff} \$${String.format("%,d", smallCost)}", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                // Full payoff: clear all heat
                                Button(
                                    onClick = { viewModel.payoffHeat(state.heat) },
                                    enabled = state.canPayoff,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                                ) {
                                    Text("Clear \$${String.format("%,d", state.payoffCost)}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gang territory warning
        RivalGang.getGangForNeighborhood(state.currentNeighborhood)?.let { gang ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Gold.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        "${gang.emoji} ${gang.displayName} territory - ${gang.specialty.displayName} prices inflated",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold
                    )
                }
            }
        }

        // Hot commodity banner
        state.hotCommodity?.let { hot ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RichGreen.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        "\uD83D\uDD25 Hot: ${hot.displayName} prices soaring!",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = RichGreen
                    )
                }
            }
        }

        // Crackdown banner
        state.crackdownGood?.let { crackdown ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DangerRed.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        "\uD83D\uDEA8 Crackdown: ${crackdown.displayName} - cheap but risky!",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = DangerRed
                    )
                }
            }
        }

        // Goods list
        items(Good.entries) { good ->
            GoodRow(
                good = good,
                currentPrice = state.prices[good] ?: good.basePrice,
                previousPrice = state.previousPrices[good],
                inventoryItem = state.inventory[good] ?: InventoryItem(),
                isHot = good == state.hotCommodity,
                isCrackdown = good == state.crackdownGood,
                playerCash = state.cash,
                freeCapacity = state.freeCapacity,
                onBuy = { qty -> viewModel.buy(good, qty) },
                onSell = { qty -> viewModel.sell(good, qty) }
            )
        }

        // Bottom spacer
        item { Spacer(Modifier.height(16.dp)) }
    }
}
