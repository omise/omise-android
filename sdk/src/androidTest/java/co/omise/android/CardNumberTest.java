package co.omise.android;

import co.omise.android.models.CardBrand;

public class CardNumberTest extends SDKTest {
    public void testNormalize() {
        assertEquals("", CardNumber.normalize(null));
        assertEquals("4242424242424242", CardNumber.normalize("\r\r4242-4242-4242-4242  "));
        assertEquals("42424242", CardNumber.normalize("the42quick42brown42fox42jumps"));
    }

    public void testFormat() {
        assertEquals("", CardNumber.format(null));
        assertEquals("", CardNumber.format(" "));
        assertEquals("4242 4", CardNumber.format("4242--4"));
        assertEquals("4242 4242 4242 4242", CardNumber.format("\r\r4242-4242-4242-4242  "));
        assertEquals("4242 4242", CardNumber.format("the42quick42brown42fox42jumps"));
    }

    public void testBrand() {
        assertEquals(CardBrand.VISA, CardNumber.brand("4242424242424242"));
        assertEquals(CardBrand.VISA, CardNumber.brand("4242-4242-4242-4242"));
        assertEquals(CardBrand.MASTERCARD, CardNumber.brand("5454545454545454"));
        assertEquals(CardBrand.JCB, CardNumber.brand("3566111111111113"));
    }

    public void testLuhn() {
        assertTrue(CardNumber.luhn("4242424242424242"));
        assertTrue(CardNumber.luhn("4242-4242-4242-4242"));
        assertFalse(CardNumber.luhn("4242424242424243"));

        assertTrue(CardNumber.luhn("344242343442423442"));
    }
}
