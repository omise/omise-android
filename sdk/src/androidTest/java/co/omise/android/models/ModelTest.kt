package co.omise.android.models

import android.os.Bundle
import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.SDKTest
import co.omise.android.extensions.getParcelableCompat
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Objects

@RunWith(AndroidJUnit4::class)
class ModelTest : SDKTest() {
    @Parcelize
    class Dummy(
        override var modelObject: String? = "dummy",
        override var id: String? = "acct_4x7d2wtqnj2f4klrfsc",
        override var livemode: Boolean = false,
        override var location: String? = "account",
        override var created: DateTime? = DateTime.parse("2015-05-20T04:57:36Z"),
        override var deleted: Boolean = false,
    ) : Model, Parcelable

    @Test
    fun testParcelable() {
        val dummy = Dummy()
        val bundle = Bundle()
        bundle.putParcelable("test", dummy)

        assertCorrectFields(Objects.requireNonNull(bundle.getParcelableCompat("test")))
    }

    private fun assertCorrectFields(dummy: Dummy) {
        assertEquals("acct_4x7d2wtqnj2f4klrfsc", dummy.id)
        assertFalse(dummy.livemode)

        assert(dummy.created != null)
        val created = dummy.created!!.withZone(DateTimeZone.UTC)
        assertEquals(2015, created.year)
        assertEquals(5, created.monthOfYear)
        assertEquals(20, created.dayOfMonth)
        assertEquals(4, created.hourOfDay)
        assertEquals(57, created.minuteOfHour)
        assertEquals(36, created.secondOfMinute)
    }
}
