package co.omise.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import org.joda.time.DateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "object",
        visible = true
)

@JsonTypeIdResolver(ModelTypeResolver.class)
public abstract class Model implements Parcelable {

    public String id;
    @JsonProperty("livemode")
    public boolean livemode;
    private String location;
    @JsonProperty("created_at")
    private DateTime created;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean deleted;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(livemode ? 1 : 0);
        dest.writeString(location);
        dest.writeLong(created.getMillis());
        dest.writeInt(deleted ? 1 : 0);
    }
}
