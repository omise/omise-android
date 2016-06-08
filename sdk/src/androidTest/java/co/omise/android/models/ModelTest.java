package co.omise.android.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

import co.omise.android.SDKLog;
import co.omise.android.SDKTest;

public class ModelTest extends SDKTest {
    public static final String MODEL_JSON = "{\"object\":\"account\",\"id\":\"acct_4x7d2wtqnj2f4klrfsc\",\"email\":\"gedeon@gedeon.be\",\"created\":\"2015-05-20T04:57:36Z\"}";

    public static class Dummy extends Model {
        public static final Parcelable.Creator<Dummy> CREATOR = new Creator<Dummy>() {
            @Override
            public Dummy createFromParcel(Parcel source) {
                try {
                    return new Dummy(source.readString());
                } catch (JSONException e) {
                    SDKLog.wtf("failed to deparcelize a Dummy object.", e);
                    return null;
                }
            }

            @Override
            public Dummy[] newArray(int size) {
                return new Dummy[size];
            }
        };

        public Dummy(String rawJson) throws JSONException {
            super(rawJson);
        }
    }

    public void testJsonCtor() throws JSONException {
        assertCorrectFields(new Dummy(MODEL_JSON));
    }

    public void testParcelable() throws JSONException {
        Dummy dummy = new Dummy(MODEL_JSON);
        Bundle bundle = new Bundle();
        bundle.putParcelable("test", dummy);

        assertCorrectFields(bundle.<Dummy>getParcelable("test"));
    }

    private void assertCorrectFields(Dummy dummy) {
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
