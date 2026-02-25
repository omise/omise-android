package co.omise.android

import co.omise.android.models.CardBrand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardNumberTest {
    @Test
    fun testNormalize() {
        assertEquals("", CardNumber.normalize(null))
        assertEquals("4242424242424242", CardNumber.normalize("\r\r4242-4242-4242-4242  "))
        assertEquals("42424242", CardNumber.normalize("the42quick42brown42fox42jumps"))
    }

    @Test
    fun testFormat() {
        assertEquals("", CardNumber.format(null))
        assertEquals("", CardNumber.format(" "))
        assertEquals("4242 4", CardNumber.format("4242--4"))
        assertEquals("4242 4242 4242 4242", CardNumber.format("\r\r4242-4242-4242-4242  "))
        assertEquals("4242 4242", CardNumber.format("the42quick42brown42fox42jumps"))
    }

    @Test
    fun testBrand() {
        assertEquals(CardBrand.VISA, CardNumber.brand("4242424242424242"))
        assertEquals(CardBrand.VISA, CardNumber.brand("4242-4242-4242-4242"))
        assertEquals(CardBrand.MASTERCARD, CardNumber.brand("5454545454545454"))
        assertEquals(CardBrand.JCB, CardNumber.brand("3566111111111113"))
        assertEquals(CardBrand.UNIONPAY, CardNumber.brand("6242424242424242426"))
        assertEquals(CardBrand.UNIONPAY, CardNumber.brand("8142424242424242"))
        assertNull(CardNumber.brand("1234567890123456")) // Unknown brand
    }

    @Test
    fun testLuhn() {
        assertTrue(CardNumber.luhn("4242424242424242"))
        assertTrue(CardNumber.luhn("4242-4242-4242-4242"))
        assertFalse(CardNumber.luhn("4242424242424243"))

        assertTrue(CardNumber.luhn("344242343442423442"))
        assertTrue(CardNumber.luhn("6242424242424242426"))
        assertTrue(CardNumber.luhn("810055106191988"))
    }
}
