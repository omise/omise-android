package co.omise.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import co.omise.android.SDKLog;

public class Card extends Model {
    public static final Parcelable.Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            try {
                return new Card(source.readString());
            } catch (JSONException e) {
                SDKLog.wtf("failed to deparcelize Card object", e);
                return null;
            }
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[0];
        }
    };

    public final String country;
    public final String city;
    public final String postalCode;
    public final String financing;
    public final String lastDigits;
    public final String brand;
    public final int expirationMonth;
    public final int expirationYear;
    public final String fingerprint;
    public final String name;
    public final boolean securityCodeCheck;

    public Card(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    public Card(JSONObject json) throws JSONException {
        super(json);
        country = JSON.string(json, "json");
        city = JSON.string(json, "city");
        postalCode = JSON.string(json, "postal_code");
        financing = JSON.string(json, "financing");
        lastDigits = JSON.string(json, "last_digits");
        brand = JSON.string(json, "brand");
        expirationMonth = JSON.integer(json, "expiration_month");
        expirationYear = JSON.integer(json, "expiration_year");
        fingerprint = JSON.string(json, "fingerprint");
        name = JSON.string(json, "name");
        securityCodeCheck = JSON.bool(json, "security_code_check");
    }
}
