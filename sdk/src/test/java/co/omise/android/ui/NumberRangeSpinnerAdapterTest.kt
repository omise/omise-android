package co.omise.android.ui

import android.content.Context
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NumberRangeSpinnerAdapterTest {
    private lateinit var context: Context
    private lateinit var adapter: TestNumberRangeSpinnerAdapter

    class TestNumberRangeSpinnerAdapter(min: Int, max: Int) : NumberRangeSpinnerAdapter(min, max)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        adapter = TestNumberRangeSpinnerAdapter(1, 10)
    }

    @Test
    fun testGetCount() {
        assertEquals(10, adapter.count)
    }

    @Test
    fun testGetItem() {
        assertEquals(1, adapter.getItem(0))
        assertEquals(10, adapter.getItem(9))
    }

    @Test
    fun testGetPosition() {
        assertEquals(0, adapter.getPosition(1))
        assertEquals(9, adapter.getPosition(10))
    }

    @Test
    fun testGetItemId() {
        // The implementation uses hashCode of the Integer item
        assertEquals(1.hashCode().toLong(), adapter.getItemId(0))
    }

    @Test
    fun testGetView() {
        val parent = android.widget.FrameLayout(context)
        val view = adapter.getView(0, null, parent)
        assertNotNull(view)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        assertEquals("1", textView.text)
    }

    @Test
    fun testGetDropDownView() {
        val parent = android.widget.FrameLayout(context)
        val view = adapter.getDropDownView(0, null, parent)
        assertNotNull(view)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        assertEquals("1", textView.text)
    }
}
