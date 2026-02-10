package com.roaringtrades.game.model

data class LoanShark(
    val debt: Int = 0,
    val interestRate: Float = 0.10f, // 10% daily
    val maxLoan: Int = 5000,
    val daysUntilThreat: Int = 0, // days since last payment before threats
    val hasActiveLoan: Boolean = false
) {
    val dailyInterest: Int
        get() = (debt * interestRate).toInt()

    val isOverdue: Boolean
        get() = daysUntilThreat >= 5

    val threatLevel: String
        get() = when {
            !hasActiveLoan -> "None"
            daysUntilThreat < 3 -> "Patient"
            daysUntilThreat < 5 -> "Impatient"
            daysUntilThreat < 7 -> "Threatening"
            else -> "Dangerous"
        }

    val threatEmoji: String
        get() = when {
            !hasActiveLoan -> ""
            daysUntilThreat < 3 -> "\uD83D\uDE10"
            daysUntilThreat < 5 -> "\uD83D\uDE20"
            daysUntilThreat < 7 -> "\uD83D\uDE21"
            else -> "\uD83D\uDC80"
        }

    companion object {
        const val LOAN_SHARK_NAME = "Vinnie the Fixer"
        const val LOAN_SHARK_EMOJI = "\uD83E\uDDD4"
    }
}
