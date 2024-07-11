package co.omise.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.TokenizationMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenizationMethodTest {
    @Test
    fun addTokenizationToPaymentMethodCapability() {
        val paymentMethodList: List<PaymentMethod> =
            (1..10).map {
                PaymentMethod(
                    "Method no: $it",
                    listOf("thb, usd, myr, sgd, jpy"),
                    listOf("VISA", "MASTER", "LASER"),
                    listOf(1, 2, 4, 6),
                )
            }
        val tokenizationMethodList: List<String> =
            listOf(
                "googlepay",
                "kanpay",
            )
        val capability =
            Capability(
                mutableListOf("a", "b", "c", "d"),
                paymentMethodList as MutableList<PaymentMethod>?,
                tokenizationMethodList,
                false,
            )

        capability.paymentMethods?.any { it.name == "Method no: 1" }?.let { assertTrue(it) }
        capability.tokenizationMethods?.any { it == "googlepay" }?.let { assertTrue(it) }
        capability.tokenizationMethods?.any { it == "kanpay" }?.let { assertTrue(it) }
    }

    @Test
    fun canCreateTokenizationMethodFromName() {
        val gpayToken = TokenizationMethod.creator("googlepay")
        val card = TokenizationMethod.creator("card")

        assertEquals(TokenizationMethod.GooglePay, gpayToken)
        assertEquals(TokenizationMethod.Card, card)
    }

    @Test
    fun cannotCreateTokenizationMethodFromInvalidName() {
        val tokenizationMethod = TokenizationMethod.creator("aaa")

        assertEquals(TokenizationMethod.Unknown("aaa"), tokenizationMethod)
    }
}
