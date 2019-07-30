package co.omise.android;

import android.os.Build;

import org.junit.Test;

import co.omise.android.api.Config;

import static org.junit.Assert.assertEquals;

public class ConfigTest {
    private static final String MODEL = Build.MODEL;
    private static final String PKG_VERSION = BuildConfig.VERSION_NAME;
    static final String API_VERSION = "new-shiny-version";
    private static final int ANDROID_VERSION = Build.VERSION.SDK_INT;
    static final String PKEY = "pkey_test_123";

    @Test
    public void testConfig() {
        Config config = config();
        assertEquals(API_VERSION, config.apiVersion());
        assertEquals(PKEY, config.publicKey());
    }

    @Test
    public void testUserAgent() {
        String builder = "OmiseAndroid/" +
                PKG_VERSION +
                " Android/" +
                ANDROID_VERSION +
                " Model/" +
                MODEL;
        assertEquals(builder, config().userAgent());
    }

    static Config config() {
        return new Config(API_VERSION, PKEY);
    }
}
