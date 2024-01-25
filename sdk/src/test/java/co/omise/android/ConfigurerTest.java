package co.omise.android;

import org.junit.Test;

import co.omise.android.api.Config;
import co.omise.android.api.Configurer;
import okhttp3.Credentials;
import okhttp3.Request;

import static co.omise.android.ConfigTest.API_VERSION;
import static co.omise.android.ConfigTest.PKEY;

public class ConfigurerTest extends OmiseTest {

    @Test
    public void testUserAgent() {
        Request req = configure(new Request.Builder()
                .url("http://api.omise.co")
                .build());

        assertEquals(config().userAgent(), req.header("User-Agent"));
    }

    @Test
    public void testApiVersion() {
        Request req = configure(new Request.Builder()
                .url("https://api.omise.co")
                .build());

        assertEquals(API_VERSION, req.header("Omise-Version"));
    }

    @Test
    public void testVaultRequest() {
        Request req = configure(new Request.Builder()
                .url("https://vault.omise.co/tokens")
                .build());

        String authorization = req.header("Authorization");
        assertEquals(authorization, Credentials.basic(PKEY, "x"));
    }

    private Request configure(Request req) {
        return Configurer.configure(config(), req);
    }

    private Config config() {
        return ConfigTest.config();
    }
}
