package com.roaringtrades.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaringtrades.game.ui.theme.Gold
import com.roaringtrades.game.ui.theme.SolanaPurple
import com.roaringtrades.game.wallet.WalletManager
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.launch

@Composable
fun WalletConnectScreen(
    activityResultSender: ActivityResultSender,
    onConnected: (publicKey: String, walletName: String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isConnecting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val adapter = remember { WalletManager.createAdapter() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            "ROARING",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Gold
        )
        Text(
            "TRADES",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Gold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Chicago, 1920s",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        // Game description
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Buy low, sell high. Build your trading empire in 30 days.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Connect your Solana wallet to track your high scores on the leaderboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Connect button
        Button(
            onClick = {
                scope.launch {
                    isConnecting = true
                    errorMessage = null

                    val result = WalletManager.connect(adapter, activityResultSender)
                    result.fold(
                        onSuccess = { connectResult ->
                            onConnected(connectResult.publicKey, connectResult.walletName)
                        },
                        onFailure = { error ->
                            errorMessage = error.message ?: "Failed to connect wallet"
                        }
                    )
                    isConnecting = false
                }
            },
            enabled = !isConnecting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SolanaPurple
            )
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Connecting...")
            } else {
                Icon(
                    Icons.Filled.AccountBalanceWallet,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Connect Wallet")
            }
        }

        // Error message
        errorMessage?.let { error ->
            Spacer(Modifier.height(12.dp))
            Text(
                error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "A Midnight Run Games production",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
