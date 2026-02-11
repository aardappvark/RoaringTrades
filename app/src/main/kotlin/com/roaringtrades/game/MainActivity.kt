package com.roaringtrades.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roaringtrades.game.data.GamePreferences
import com.roaringtrades.game.ui.navigation.AppNavigation
import com.roaringtrades.game.ui.screens.WalletConnectScreen
import com.roaringtrades.game.ui.theme.RoaringTradesTheme
import com.roaringtrades.game.ui.viewmodel.GameViewModel
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

class MainActivity : ComponentActivity() {

    private lateinit var activityResultSender: ActivityResultSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultSender = ActivityResultSender(this)

        setContent {
            RoaringTradesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoaringTradesApp(activityResultSender)
                }
            }
        }
    }
}

@Composable
fun RoaringTradesApp(activityResultSender: ActivityResultSender) {
    val context = LocalContext.current
    val gamePrefs = remember { GamePreferences(context) }

    var isWalletConnected by remember {
        mutableStateOf(gamePrefs.isWalletConnected())
    }

    var hasAcceptedDisclaimer by remember {
        mutableStateOf(gamePrefs.hasAcceptedDisclaimer())
    }

    // Trigger SGT check whenever wallet is connected (uses 24h cache internally)
    if (isWalletConnected) {
        val viewModel: GameViewModel = viewModel()
        val heliusApiKey = context.getString(R.string.helius_api_key)
        LaunchedEffect(Unit) {
            val rpcUrl = if (heliusApiKey.isNotEmpty()) {
                AppConfig.Rpc.heliusUrl(heliusApiKey)
            } else {
                com.midmightbit.sgt.SgtConstants.DEFAULT_RPC_URL
            }
            viewModel.checkSgtStatus(rpcUrl)
        }
    }

    if (!isWalletConnected) {
        WalletConnectScreen(
            activityResultSender = activityResultSender,
            onConnected = { publicKey, walletName ->
                gamePrefs.saveWalletConnection(publicKey, walletName)
                isWalletConnected = true
            }
        )
    } else if (!hasAcceptedDisclaimer) {
        DisclaimerDialog(
            onAccept = {
                gamePrefs.setDisclaimerAccepted()
                hasAcceptedDisclaimer = true
            }
        )
    } else {
        AppNavigation()
    }
}

@Composable
fun DisclaimerDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Can't dismiss without accepting */ },
        title = {
            Text(
                "\uD83C\uDFAD Disclaimer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Roaring Trades is a work of fiction set in the 1920s Prohibition era. " +
                "All characters, groups, locations, and events are entirely fictional.\n\n" +
                "This game is for entertainment purposes only. It does not promote, " +
                "encourage, or glorify any real-world activity depicted in the game.\n\n" +
                "Don't try this at home \u2014 or anywhere else. " +
                "Please drink responsibly and obey all laws in your jurisdiction.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("I Understand")
            }
        }
    )
}
