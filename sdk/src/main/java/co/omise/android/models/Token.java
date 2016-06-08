package co.omise.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import co.omise.android.SDKLog;

public class Token extends Model {
    public static final Parcelable.Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel source) {
            try {
                return new Token(source.readString());
            } catch (JSONException e) {
                SDKLog.wtf("failed to deparcelize Token object", e);
                return null;
            }
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

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
