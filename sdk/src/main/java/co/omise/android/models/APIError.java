package co.omise.android.models;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class APIError extends Error {
    public final String location;
    public final String code;
    public final String message;
    public final DateTime created;

    public APIError(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    public APIError(JSONObject json) throws JSONException {
        super(JSON.string(json, "message"));
        location = JSON.string(json, "location");
        code = JSON.string(json, "code");
        message = JSON.string(json, "message");
        created = JSON.dateTime(json, "created");
    }
}
