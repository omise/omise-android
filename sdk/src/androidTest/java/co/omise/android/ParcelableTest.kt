package co.omise.android

import android.os.Bundle
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.*
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParcelableTest {

    @Test
    fun testModelParceling_success() {
        val model = Model(
                "object",
                "id",
                true,
                "here",
                DateTime.parse("2015-05-20T04:57:36Z"),
                false
        )

        assertObjectParceling(model)
    }

    @Test
    fun testSourceParceling_success() {
        val source = Source(
                type = SourceType.TrueMoney,
                flow = FlowType.Redirect,
                amount = 4200000L,
                currency = "thb",
                storeId = "ID",
                name = "JOHN DOE",
                email = "a@bc.om",
                phoneNumber = "123434523413"
        )

        assertObjectParceling(source)
    }

    @Test
    fun testTokenParceling_success() {
        val card = Card(
                name = "John Doe",
                country = "Thailand",
                city = "Bangkok",
                postalCode = "12345",
                lastDigits = "1234",
                bank = "kbank",
                expirationMonth = 11,
                expirationYear = 22)
        val token = Token(
                false,
                card)

        assertObjectParceling(token)
    }

    @Test
    fun testCapabilityParceling_success() {
        val paymentMethodList: List<PaymentMethod> = (1..10).map {
            PaymentMethod().apply {
                id = "id-$it"
                name = "Method no: $it"
                currencies = listOf("thb, usd, myr, sgd, jpy")
                cardBrands = listOf("VISA", "MASTER", "LASER")
                installmentTerms = listOf(1, 2, 4, 6)
            }
        }
        val capability = Capability(
                mutableListOf("a", "b", "c", "d"),
                paymentMethodList,
                false)

        assertObjectParceling(capability)
    }

    private fun <T : Model> assertObjectParceling(model: T) {
        val bundle1 = Bundle()
        bundle1.putParcelable(model::class.java.simpleName, model)

        val parcel = Parcel.obtain()
        parcel.writeBundle(bundle1)

        parcel.setDataPosition(0)
        val bundle2 = parcel.readBundle()
        bundle2!!.classLoader = model::class.java.classLoader

        val finalModel = bundle2.getParcelable<T>(model::class.java.simpleName)
        assertEquals(model, finalModel)
    }
}
