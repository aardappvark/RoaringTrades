package com.roaringtrades.game.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.roaringtrades.game.model.EncounterResult
import com.roaringtrades.game.ui.theme.DangerRed
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.RichGreen

@Composable
fun GangEncounterDialog(
    encounter: EncounterResult,
    onDismiss: () -> Unit
) {
    val (title, color) = when (encounter) {
        is EncounterResult.ShakenDown -> "Confrontation!" to DangerRed
        is EncounterResult.Intimidated -> "Warning!" to Gold
        is EncounterResult.FoughtOff -> "Stood Your Ground!" to RichGreen
    }

    val description = when (encounter) {
        is EncounterResult.ShakenDown -> encounter.description
        is EncounterResult.Intimidated -> encounter.description
        is EncounterResult.FoughtOff -> encounter.description
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                color = color,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
