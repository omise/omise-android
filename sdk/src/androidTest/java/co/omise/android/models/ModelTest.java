package co.omise.android.models;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import co.omise.android.SDKTest;

@RunWith(AndroidJUnit4.class)
public class ModelTest extends SDKTest {

    public static class Dummy extends Model {
        public Dummy() {
            id = "acct_4x7d2wtqnj2f4klrfsc";
            location = "account";
            livemode = false;
            created = DateTime.parse("2015-05-20T04:57:36Z");
        }
    }

    @Test
    public void testParcelable() {
        Dummy dummy = new Dummy();
        Bundle bundle = new Bundle();
        bundle.putParcelable("test", dummy);

        assertCorrectFields(Objects.requireNonNull(bundle.getParcelable("test")));
    }

    private void assertCorrectFields(Dummy dummy) {
        assertEquals("acct_4x7d2wtqnj2f4klrfsc", dummy.id);
        assertFalse(dummy.livemode);

        assert dummy.created != null;
        DateTime created = dummy.created.withZone(DateTimeZone.UTC);
        assertEquals(2015, created.getYear());
        assertEquals(5, created.getMonthOfYear());
        assertEquals(20, created.getDayOfMonth());
        assertEquals(4, created.getHourOfDay());
        assertEquals(57, created.getMinuteOfHour());
        assertEquals(36, created.getSecondOfMinute());
    }
}
