package co.omise.android.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

final class JSON {
    private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    public static String string(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getString(key) : null;
    }

    public static boolean bool(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getBoolean(key) : false;
    }

    public static int integer(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getInt(key) : 0;
    }

    public static long long_(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getLong(key) : 0;
    }

    public static DateTime dateTime(JSONObject json, String key) throws JSONException {
        return json.has(key) ?
                DateTime.parse(json.getString(key), dateTimeFormatter) :
                null;
    }
}
