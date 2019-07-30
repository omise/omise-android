package co.omise.android;

import org.junit.Test;

import co.omise.android.api.Config;
import co.omise.android.api.Endpoint;

public class EndpointTest extends OmiseTest {
    private final String PKEY = "pkey_test_123";
    private final Config CONFIG = new Config("", PKEY);

    @Test
    public void testAll() {
        assertTrue(Endpoint.getAllEndpoints().contains(Endpoint.API));
        assertTrue(Endpoint.getAllEndpoints().contains(Endpoint.VAULT));
    }

    @Test
    public void testByHost() {
        for (Endpoint endpoint : Endpoint.getAllEndpoints()) {
            assertEquals(endpoint, Endpoint.getAllEndpointsByHost().get(endpoint.host()));
        }
    }

    @Test
    public void testProperties() {
        for (Endpoint endpoint : Endpoint.getAllEndpoints()) {
            assertNotNull(endpoint.scheme());
            assertNotNull(endpoint.host());
            assertNotNull(endpoint.certificateHash());
            assertNotNull(endpoint.authenticationKey(CONFIG));
        }
    }
}
