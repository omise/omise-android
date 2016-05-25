package co.omise.android;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

public class ModelTest extends SDKTest {
    public static final String MODEL_JSON = "{\"object\":\"account\",\"id\":\"acct_4x7d2wtqnj2f4klrfsc\",\"email\":\"gedeon@gedeon.be\",\"created\":\"2015-05-20T04:57:36Z\"}";

    public class Dummy extends Model {
        public Dummy(String rawJson) throws JSONException {
            super(rawJson);
        }
    }

    public void testJsonCtor() throws JSONException {
        Dummy dummy = new Dummy(MODEL_JSON);
        assertEquals("acct_4x7d2wtqnj2f4klrfsc", dummy.id);
        assertFalse(dummy.livemode);
        assertNull(dummy.location);

        DateTime created = dummy.created.withZone(DateTimeZone.UTC);
        assertEquals(2015, created.getYear());
        assertEquals(5, created.getMonthOfYear());
        assertEquals(20, created.getDayOfMonth());
        assertEquals(4, created.getHourOfDay());
        assertEquals(57, created.getMinuteOfHour());
        assertEquals(36, created.getSecondOfMinute());
    }
}
