package co.omise.android;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class OmiseError extends Error {
    public final String location;
    public final String code;
    public final String message;
    public final DateTime created;

    public OmiseError(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    public OmiseError(JSONObject json) throws JSONException {
        location = JSON.string(json, "location");
        code = JSON.string(json, "code");
        message = JSON.string(json, "message");
        created = JSON.dateTime(json, "created");
    }
}
