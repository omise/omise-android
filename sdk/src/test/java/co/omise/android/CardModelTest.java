package co.omise.android;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import co.omise.android.models.Card;
import co.omise.android.models.Serializer;

import static org.junit.Assert.assertEquals;

public class CardModelTest {

    private static final String CARD_JSON = "{\n" +
            "   \"object\":\"card\",\n" +
            "   \"id\":\"card_test_5086xl7amxfysl0ac5l\",\n" +
            "   \"livemode\":true,\n" +
            "   \"location\":\"/customers/cust_test_5086xleuh9ft4bn0ac2/cards/card_test_5086xl7amxfysl0ac5l\",\n" +
            "   \"country\":\"th\",\n" +
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
            "   \"created_at\":\"2015-06-02T05:41:46Z\",\n" +
            "   \"bank\":\"BBL\"\n" +
            "}";

    @Test
    public void cardConstructor_canSerializeByString() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(CARD_JSON.getBytes());
        Card card = serializer().deserialize(inputStream, Card.class);

        assertEquals("card_test_5086xl7amxfysl0ac5l", card.id);
        assertEquals(true, card.livemode);
        assertEquals("th", card.country);
        assertEquals("Bangkok", card.city);
        assertEquals("10320", card.postalCode);
        assertEquals("", card.financing);
        assertEquals("4242", card.lastDigits);
        assertEquals("Visa", card.brand);
        assertEquals(10, card.expirationMonth);
        assertEquals(2018, card.expirationYear);
        assertEquals("mKleiBfwp+PoJWB/ipngANuECUmRKjyxROwFW5IO7TM=", card.fingerprint);
        assertEquals("Somchai Prasert", card.name);
        assertEquals(true, card.securityCodeCheck);
        assertEquals(new DateTime("2015-06-02T05:41:46Z").getMillis(), card.created.getMillis());
        assertEquals("BBL", card.bank);
    }

    private Serializer serializer() {
        return new Serializer();
    }
}