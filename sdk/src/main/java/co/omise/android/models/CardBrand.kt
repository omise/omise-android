package co.omise.android.models

import android.os.Parcelable
import co.omise.android.R
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.regex.Pattern

/**
 * CardBrand holds information about all card brands recognized in the SDK.
 *
 * @param name Card brand name.
 * @param patternStr Pattern that can be used to recognize the brand's card numbers.
 * @param minLength Minimum length of the brand's card numbers.
 * @param maxLength Maximum length of the brand's card numbers.
 * @param logoResourceId Resource ID for the brand's logo image.
 */
@Parcelize
data class CardBrand(
    val name: String,
    val patternStr: String,
    private val minLength: Int,
    private val maxLength: Int,
    val logoResourceId: Int,
) : Parcelable {
    @IgnoredOnParcel
    private val pattern: Pattern = Pattern.compile("$patternStr[0-9]+")

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
        val VISA = CardBrand("visa", "^4", 16, 16, R.drawable.brand_visa)

        @JvmField
        val MASTERCARD = CardBrand("mastercard", "^5[1-5]", 16, 16, R.drawable.brand_mastercard)

        @JvmField
        val MAESTRO = CardBrand("maestro", "^(5018|5020|5038|6304|6759|676[1-3])", 12, 19, R.drawable.brand_maestro)

        @JvmField
        val DISCOVER =
            CardBrand(
                "discover",
                "^(6011|622(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[0-1][0-9]|92[0-5]|64[4-9])|65)",
                16,
                16,
                R.drawable.brand_discover,
            )

        @JvmField
        val ALL = arrayOf(AMEX, DINERS, JCB, VISA, MASTERCARD, MAESTRO, DISCOVER)
    }
}
