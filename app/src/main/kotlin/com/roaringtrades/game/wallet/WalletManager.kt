package com.roaringtrades.game.wallet

import android.net.Uri
import com.roaringtrades.game.AppConfig
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult

data class WalletConnectResult(
    val publicKey: String,
    val walletName: String?
)

object WalletManager {

    fun createAdapter(): MobileWalletAdapter {
        return MobileWalletAdapter(
            connectionIdentity = ConnectionIdentity(
                identityUri = Uri.parse(AppConfig.Identity.URI),
                iconUri = Uri.parse(AppConfig.Identity.ICON_URI),
                identityName = AppConfig.Identity.NAME
            )
        )
    }

    suspend fun connect(
        adapter: MobileWalletAdapter,
        sender: ActivityResultSender
    ): Result<WalletConnectResult> {
        return try {
            val result = adapter.transact(sender) {
                authorize(
                    identityUri = Uri.parse(AppConfig.Identity.URI),
                    iconUri = Uri.parse(AppConfig.Identity.ICON_URI),
                    identityName = AppConfig.Identity.NAME,
                    chain = AppConfig.Wallet.CHAIN
                )
            }

            when (result) {
                is TransactionResult.Success -> {
                    val accounts = result.authResult.accounts
                    if (accounts.isNotEmpty()) {
                        val pubKeyBytes = accounts.first().publicKey
                        when {
                            !isValidSolanaPublicKey(pubKeyBytes) ->
                                Result.failure(Exception("Invalid wallet public key format"))
                            else -> {
                                val pubKeyBase58 = bytesToBase58(pubKeyBytes)
                                if (!isValidBase58Address(pubKeyBase58)) {
                                    Result.failure(Exception("Invalid wallet address encoding"))
                                } else {
                                    val walletName = result.authResult.walletUriBase?.host
                                        ?: "Solana Wallet"
                                    Result.success(WalletConnectResult(pubKeyBase58, walletName))
                                }
                            }
                        }
                    } else {
                        Result.failure(Exception("No accounts returned from wallet"))
                    }
                }
                is TransactionResult.NoWalletFound -> {
                    Result.failure(Exception("No MWA-compatible wallet found. Please install a Solana wallet app."))
                }
                is TransactionResult.Failure -> {
                    Result.failure(result.e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validates that the raw public key bytes are a valid Solana Ed25519 public key.
     * Solana public keys are exactly 32 bytes and should not be all zeros.
     */
    private fun isValidSolanaPublicKey(bytes: ByteArray): Boolean {
        if (bytes.size != 32) return false
        // Reject all-zero keys (invalid/uninitialized)
        if (bytes.all { it.toInt() == 0 }) return false
        return true
    }

    /**
     * Validates that the Base58-encoded address matches expected Solana format.
     * Valid Solana addresses are 32-44 characters using the Base58 alphabet.
     */
    private fun isValidBase58Address(address: String): Boolean {
        val base58Chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        if (address.length !in 32..44) return false
        return address.all { it in base58Chars }
    }

    private fun bytesToBase58(bytes: ByteArray): String {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        var num = java.math.BigInteger(1, bytes)
        val sb = StringBuilder()
        val base = java.math.BigInteger.valueOf(58)

        while (num > java.math.BigInteger.ZERO) {
            val divRem = num.divideAndRemainder(base)
            sb.append(alphabet[divRem[1].toInt()])
            num = divRem[0]
        }

        for (byte in bytes) {
            if (byte.toInt() == 0) {
                sb.append(alphabet[0])
            } else {
                break
            }
        }

        return sb.reverse().toString()
    }
}
