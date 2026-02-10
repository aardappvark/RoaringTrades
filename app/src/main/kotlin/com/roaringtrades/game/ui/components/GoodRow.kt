package com.roaringtrades.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.model.Good
import com.roaringtrades.game.model.InventoryItem
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen

@Composable
fun GoodRow(
    good: Good,
    currentPrice: Int,
    previousPrice: Int?,
    inventoryItem: InventoryItem,
    isHot: Boolean,
    isCrackdown: Boolean,
    playerCash: Int,
    freeCapacity: Int,
    onBuy: (Int) -> Unit,
    onSell: (Int) -> Unit
) {
    var showBuyDialog by remember { mutableStateOf(false) }
    var showSellDialog by remember { mutableStateOf(false) }

    // Price direction
    val priceArrow = when {
        previousPrice == null -> ""
        currentPrice > previousPrice -> "\u2191"
        currentPrice < previousPrice -> "\u2193"
        else -> "\u2192"
    }
    val arrowColor = when {
        previousPrice == null -> MaterialTheme.colorScheme.onSurface
        currentPrice > previousPrice -> DangerRed
        currentPrice < previousPrice -> RichGreen
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // P&L calculation
    val avgCost = inventoryItem.averageCost
    val bookPnl = if (inventoryItem.quantity > 0) {
        ((currentPrice - avgCost) * inventoryItem.quantity).toInt()
    } else 0
    val pnlColor = when {
        bookPnl > 0 -> RichGreen
        bookPnl < 0 -> DangerRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Max buy/sell quantities
    val maxBuy = minOf(playerCash / currentPrice, freeCapacity).coerceAtLeast(0)
    val maxSell = inventoryItem.quantity

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isHot -> RichGreen.copy(alpha = 0.08f)
                isCrackdown -> DangerRed.copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Good name + price + arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(good.emoji, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                good.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (isHot) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "\uD83D\uDD25",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            if (isCrackdown) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "\uD83D\uDEA8",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        if (inventoryItem.quantity > 0) {
                            Text(
                                "Owned: ${inventoryItem.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "\$$currentPrice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (priceArrow.isNotEmpty()) {
                            Spacer(Modifier.width(4.dp))
                            Text(
                                priceArrow,
                                color = arrowColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Text(
                        "Range: \$${good.minPrice} - \$${good.maxPrice}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Row 2: Avg cost + P&L (only if holding)
            if (inventoryItem.quantity > 0) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Avg cost: \$${String.format("%.0f", avgCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "P&L: ${if (bookPnl >= 0) "+" else ""}\$$bookPnl",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = pnlColor
                    )
                }
            }

            // Row 3: Sell/Buy buttons (sell left, buy right)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (maxSell > 0) {
                    Button(
                        onClick = { showSellDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold
                        )
                    ) {
                        Text("Sell (${maxSell})")
                    }
                } else {
                    OutlinedButton(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sell")
                    }
                }
                Button(
                    onClick = { showBuyDialog = true },
                    enabled = maxBuy > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RichGreen
                    )
                ) {
                    Text("Buy")
                }
            }
        }
    }

    // Buy dialog
    if (showBuyDialog && maxBuy > 0) {
        QuantityPickerDialog(
            title = "Buy ${good.displayName}",
            maxQuantity = maxBuy,
            pricePerUnit = currentPrice,
            onConfirm = { qty ->
                onBuy(qty)
                showBuyDialog = false
            },
            onDismiss = { showBuyDialog = false }
        )
    }

    // Sell dialog
    if (showSellDialog && maxSell > 0) {
        QuantityPickerDialog(
            title = "Sell ${good.displayName}",
            maxQuantity = maxSell,
            pricePerUnit = currentPrice,
            onConfirm = { qty ->
                onSell(qty)
                showSellDialog = false
            },
            onDismiss = { showSellDialog = false }
        )
    }
}
