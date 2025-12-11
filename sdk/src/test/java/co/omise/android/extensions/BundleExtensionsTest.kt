package co.omise.android.extensions

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.CardBrand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BundleExtensionsTest {
    
    @Test
    fun getParcelableCompat_withValidKey_shouldReturnParcelable() {
        val bundle = Bundle()
        val cardBrand = CardBrand.VISA
        bundle.putParcelable("test_key", cardBrand)
        
        val result = bundle.getParcelableCompat<CardBrand>("test_key")
        
        assertNotNull(result)
        assertEquals(cardBrand, result)
    }
    
    @Test
    fun getParcelableCompat_withInvalidKey_shouldReturnNull() {
        val bundle = Bundle()
        
        val result = bundle.getParcelableCompat<CardBrand>("non_existent_key")
        
        assertEquals(null, result)
    }
    
    @Test
    fun getParcelableCompat_withNullKey_shouldReturnNull() {
        val bundle = Bundle()
        bundle.putParcelable("test_key", CardBrand.MASTERCARD)
        
        val result = bundle.getParcelableCompat<CardBrand>(null)
        
        assertEquals(null, result)
    }
    
    @Test
    fun getParcelableArrayCompat_withValidArray_shouldReturnArray() {
        val bundle = Bundle()
        val brands = arrayOf(CardBrand.VISA, CardBrand.MASTERCARD)
        bundle.putParcelableArray("brands", brands)
        
        val result = bundle.getParcelableArrayCompat<CardBrand>("brands")
        
        assertNotNull(result)
        assertEquals(2, result.size)
    }
    
    @Test
    fun getParcelableArrayCompat_withEmptyArray_shouldReturnEmptyArray() {
        val bundle = Bundle()
        bundle.putParcelableArray("brands", emptyArray())
        
        val result = bundle.getParcelableArrayCompat<CardBrand>("brands")
        
        assertNotNull(result)
        assertEquals(0, result.size)
    }
    
    @Test
    fun getParcelableArrayCompat_withNonExistentKey_shouldReturnEmptyArray() {
        val bundle = Bundle()
        
        val result = bundle.getParcelableArrayCompat<CardBrand>("non_existent")
        
        assertNotNull(result)
        assertEquals(0, result.size)
    }
}

