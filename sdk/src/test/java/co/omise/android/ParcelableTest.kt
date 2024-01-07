package co.omise.android

import android.os.Bundle
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.Capability
import co.omise.android.models.Card
import co.omise.android.models.FlowType
import co.omise.android.models.Model
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.Token
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParcelableTest {
    @Parcelize
    data class Dummy(
        override var modelObject: String?,
        override var id: String?,
        override var livemode: Boolean,
        override var location: String?,
        override var created: DateTime?,
        override var deleted: Boolean,
    ) : Model

    @Test
    fun modelParceling_success() {
        val model =
            Dummy(
                "object",
                "id",
                true,
                "here",
                DateTime.parse("2015-05-20T04:57:36Z"),
                false,
            )

        assertObjectParceling(model)
    }

    @Test
    fun sourceParceling_success() {
        val source =
            Source(
                type = SourceType.TrueMoney,
                flow = FlowType.Redirect,
                amount = 4200000L,
                currency = "thb",
                storeId = "ID",
                name = "JOHN DOE",
                email = "a@bc.om",
                phoneNumber = "123434523413",
            )

        assertObjectParceling(source)
    }

    @Test
    fun tokenParceling_success() {
        val card =
            Card(
                name = "John Doe",
                country = "Thailand",
                city = "Bangkok",
                postalCode = "12345",
                lastDigits = "1234",
                bank = "kbank",
                expirationMonth = 11,
                expirationYear = 22,
            )
        val token =
            Token(
                false,
                card,
            )

        assertObjectParceling(token)
    }

    @Test
    fun capabilityParceling_success() {
        val paymentMethodList: List<PaymentMethod> =
            (1..10).map {
                PaymentMethod(
                    "Method no: $it",
                    listOf("thb, usd, myr, sgd, jpy"),
                    listOf("VISA", "MASTER", "LASER"),
                    listOf(1, 2, 4, 6),
                )
            }
        val capability =
            Capability(
                mutableListOf("a", "b", "c", "d"),
                paymentMethodList as MutableList<PaymentMethod>?,
                null,
                false,
            )

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
