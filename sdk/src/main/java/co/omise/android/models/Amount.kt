package co.omise.android.models

/**
 * Amount handles formatting of a given amount based on the supplied currency.
 *
 * @param amount The specified amount.
 * @param currency The specified currency.
 */
data class Amount(val amount: Long, val currency: String) {

    val localAmount: Double
        get() = when (currency.toLowerCase()) {
            "jpy" -> amount.toDouble()
            else -> amount / 100.0
        }

    fun toAmountString(): String {
        return "$localAmount ${currency.toUpperCase()}"
    }

    companion object {

        @JvmStatic
        fun fromLocalAmount(localAMount: Double, currency: String): Amount {
            val subunitAmount = if (currency == "jpy") {
                localAMount.toLong()
            } else {
                localAMount.toLong() * 100
            }
            return Amount(subunitAmount, currency)
        }
    }
}
