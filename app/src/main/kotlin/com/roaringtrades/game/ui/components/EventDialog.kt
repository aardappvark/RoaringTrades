package com.roaringtrades.game.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.roaringtrades.game.model.RandomEvent
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.RichGreen

@Composable
fun EventDialog(
    event: RandomEvent,
    onDismiss: () -> Unit
) {
    val titleColor = if (event.isGood) RichGreen else DangerRed

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = event.title,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(event.description)
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun InterceptDialog(
    interceptLoss: Map<com.roaringtrades.game.model.Good, Int>,
    onDismiss: () -> Unit
) {
    val lossText = interceptLoss.entries.joinToString("\n") { (good, qty) ->
        "${good.emoji} ${good.displayName}: -$qty units"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Caught!",
                color = DangerRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("The authorities caught you! Seized goods:\n\n$lossText")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Lay Low")
            }
        }
    )
}
