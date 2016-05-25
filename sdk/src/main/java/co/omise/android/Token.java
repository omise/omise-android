package co.omise.android;

import org.json.JSONException;
import org.json.JSONObject;

public class Token extends Model {
    public final boolean used;
    public final Card card;

    public Token(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    public Token(JSONObject json) throws JSONException {
        super(json);
        card = json.has("card") ? new Card(json.getJSONObject("card")) : null;
        used = JSON.bool(json, "used");
    }
}
