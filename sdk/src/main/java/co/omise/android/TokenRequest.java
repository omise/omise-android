package co.omise.android;

import okhttp3.FormBody;

/**
 * Encapsulates data for creating a new token. Set the relevant properties and use the {@link Client}
 * to send it to the Token API
 *
 * @see <a href="https://www.omise.co/tokens-api">Tokens API</a>.
 * @see TokenRequestListener
 */
public class TokenRequest {
    public static final String URL = "https://vault.omise.co/tokens";

    public String name;
    public String number;
    public int expirationMonth;
    public int expirationYear;
    public String securityCode;
    public String city;
    public String postalCode;

    protected FormBody buildFormBody() {
        FormBody.Builder builder = new FormBody.Builder()
                .add("card[name]", name)
                .add("card[number]", number)
                .add("card[expiration_month]", Integer.toString(expirationMonth))
                .add("card[expiration_year]", Integer.toString(expirationYear))
                .add("card[security_code]", securityCode);

        if (city != null && !city.isEmpty()) {
            builder = builder.add("card[city]", city);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            builder = builder.add("card[postal_code]", postalCode);
        }

        return builder.build();
    }
}
