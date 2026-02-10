package com.roaringtrades.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.engine.HeatSystem
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen

@Composable
fun HeatIndicator(heat: Int, modifier: Modifier = Modifier) {
    val level = HeatSystem.getHeatLevel(heat)
    val color = when (level) {
        HeatSystem.HeatLevel.NONE -> RichGreen
        HeatSystem.HeatLevel.LOW -> Gold
        HeatSystem.HeatLevel.MEDIUM -> DangerRed.copy(alpha = 0.7f)
        HeatSystem.HeatLevel.HIGH -> DangerRed
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Heat: ${level.label}",
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
            if (level.emoji.isNotEmpty()) {
                Text(text = level.emoji, style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = heat / 100f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
