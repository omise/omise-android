package co.omise.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Model implements Parcelable {
    private final JSONObject jsonObject;

    public final String id;
    public boolean livemode;
    public final String location;
    public final DateTime created;

    public Model(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    public Model(JSONObject json) throws JSONException {
        jsonObject = json;
        id = JSON.string(json, "id");
        livemode = JSON.bool(json, "livemode");
        location = JSON.string(json, "location");
        created = JSON.dateTime(json, "created");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jsonObject.toString());
    }
}
