package co.omise.android.models

import co.omise.android.R
import java.util.regex.Pattern

class CardBrand(
        val name: String,
        pattern: String,
        private val minLength: Int,
        private val maxLength: Int,
        val logoResourceId: Int
) {
    private val pattern: Pattern = Pattern.compile("$pattern[0-9]+")

    fun match(pan: String?): Boolean {
        return if (pan == null || pan.isEmpty()) false else pattern.matcher(pan).matches()
    }

    fun valid(pan: String): Boolean {
        return match(pan) && minLength <= pan.length && pan.length <= maxLength
    }

    companion object {
        @JvmField
        val AMEX = CardBrand("amex", "^3[47]", 15, 15, R.drawable.brand_amex)
        @JvmField
        val DINERS = CardBrand("diners", "^3(0[0-5]|6)", 14, 14, R.drawable.brand_diners)
        @JvmField
        val JCB = CardBrand("jcb", "^35(2[89]|[3-8])", 16, 16, R.drawable.brand_jcb)
        @JvmField
        val LASER = CardBrand("laser", "^(6304|670[69]|6771)", 16, 19, -1) // TODO: Laser logo?
        @JvmField
        val VISA = CardBrand("visa", "^4", 16, 16, R.drawable.brand_visa)
        @JvmField
        val MASTERCARD = CardBrand("mastercard", "^5[1-5]", 16, 16, R.drawable.brand_mastercard)
        @JvmField
        val MAESTRO = CardBrand("maestro", "^(5018|5020|5038|6304|6759|676[1-3])", 12, 19, R.drawable.brand_mastercard) // TODO: <- maestro logo?
        @JvmField
        val DISCOVER = CardBrand("discover", "^(6011|622(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[0-1][0-9]|92[0-5]|64[4-9])|65)", 16, 16, -1) // TODO: Discover logo?

        @JvmField
        val ALL = arrayOf(AMEX, DINERS, JCB, LASER, VISA, MASTERCARD, MAESTRO, DISCOVER)
    }
}
