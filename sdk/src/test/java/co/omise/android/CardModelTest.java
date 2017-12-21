package co.omise.android;

import org.json.JSONException;
import org.junit.Test;

import co.omise.android.models.Card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CardModelTest {

    private static final String CARD_JSON = "{\n" +
            "   \"object\":\"card\",\n" +
            "   \"id\":\"card_test_5086xl7amxfysl0ac5l\",\n" +
            "   \"livemode\":false,\n" +
            "   \"location\":\"/customers/cust_test_5086xleuh9ft4bn0ac2/cards/card_test_5086xl7amxfysl0ac5l\",\n" +
            "   \"country\":\"us\",\n" +
            "   \"city\":\"Bangkok\",\n" +
            "   \"postal_code\":\"10320\",\n" +
            "   \"financing\":\"\",\n" +
            "   \"last_digits\":\"4242\",\n" +
            "   \"brand\":\"Visa\",\n" +
            "   \"expiration_month\":10,\n" +
            "   \"expiration_year\":2018,\n" +
            "   \"fingerprint\":\"mKleiBfwp+PoJWB/ipngANuECUmRKjyxROwFW5IO7TM=\",\n" +
            "   \"name\":\"Somchai Prasert\",\n" +
            "   \"security_code_check\":true,\n" +
            "   \"created\":\"2015-06-02T05:41:46Z\",\n" +
            "   \"bank\":\"BBL\"\n" +
            "}";

    @Test
    public void card() throws JSONException {
        Card card = new Card(CARD_JSON);
        assertEquals("Visa", card.brand);
        assertEquals("BBL", card.bank);
        assertFalse(card.livemode);
    }
}