package com.roaringtrades.game.model

data class InventoryItem(
    val quantity: Int = 0,
    val totalCostBasis: Int = 0
) {
    val averageCost: Float
        get() = if (quantity > 0) totalCostBasis.toFloat() / quantity else 0f
}
