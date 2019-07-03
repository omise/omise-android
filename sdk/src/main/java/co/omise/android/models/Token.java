package co.omise.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import co.omise.android.SDKLog;
import co.omise.android.api.RequestBuilder;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class Token extends Model {
    public final boolean used;
    public final Card card;

    public Token(String rawJson) throws JSONException {
        this(new JSONObject(rawJson));
    }

    private Token(JSONObject json) throws JSONException {
        super(json);
        card = json.has("card") ? new Card(json.getJSONObject("card")) : null;
        used = JSON.bool(json, "used");
    }

    public static class CreateTokenRequestBuilder extends RequestBuilder<Token> {
        String name;
        String number;
        int expirationMonth;
        int expirationYear;
        String securityCode;
        String city;
        String postalCode;

        public CreateTokenRequestBuilder(String name, String number, int expirationMonth, int expirationYear, String securityCode) {
            this.name = name;
            this.number = number;
            this.expirationMonth = expirationMonth;
            this.expirationYear = expirationYear;
            this.securityCode = securityCode;
        }

        @NotNull
        @Override
        protected HttpUrl path() {
            return HttpUrl.parse("https://vault.omise.co/tokens");
        }

        @Nullable
        @Override
        public RequestBody payload() {
            FormBody.Builder builder = new FormBody.Builder()
                    .add("card[name]", name)
                    .add("card[number]", number)
                    .add("card[expiration_month]", Integer.toString(expirationMonth))
                    .add("card[expiration_year]", Integer.toString(expirationYear))
                    .add("card[security_code]", securityCode);

            if (city != null && !city.isEmpty()) {
                builder.add("card[city]", city);
            }
            if (postalCode != null && !postalCode.isEmpty()) {
                builder.add("card[postal_code]", postalCode);
            }

            return builder.build();
        }

        @NotNull
        @Override
        public String method() {
            return RequestBuilder.POST;
        }

        @NotNull
        @Override
        protected Class<Token> type() {
            return Token.class;
        }

        public CreateTokenRequestBuilder city(String city) {
            this.city = city;
            return this;
        }

        public CreateTokenRequestBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }
    }

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
}
