package co.omise.android;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import co.omise.android.models.Barcode;
import co.omise.android.models.CardBrand;
import co.omise.android.models.Model;
import co.omise.android.models.ModelTypeResolver;
import co.omise.android.models.PaymentMethod;
import co.omise.android.models.Serializer;

public class SerializationTest extends OmiseTest {

    @Test
    public void testModelSerializability() throws IOException {
        Serializer serializer = new Serializer();
        for (Map.Entry<String, Class<?>> testcase : new ModelTypeResolver().getKnownTypes().entrySet()) {
            if (testcase.getKey().equals("error")) continue;

            byte[] sampleBytes = getResourceBytes(objectJsonName(testcase.getValue()));
            Model instance = serializer.deserialize(new ByteArrayInputStream(sampleBytes), testcase.getValue());

            Map<String, Object> map = serializer.serializeToMap(instance);
            Map<String, Object> comparison = serializer.objectMapper().readValue(sampleBytes, new TypeReference<Map<String, Object>>() {
            });

            assertMapEquals(testcase.getKey(), comparison, map);
        }
    }

    private void assertMapEquals(String prefix, Map<String, Object> expectedMap, Map<String, Object> actualMap) {
        MapDifference<String, Object> differences = Maps.difference(expectedMap, actualMap);
        if (differences.entriesDiffering().size() == 0 && differences.entriesOnlyOnLeft().size() == 0) {
            return;
        }

        for (Map.Entry<String, Object> entry : differences.entriesOnlyOnLeft().entrySet()) {
            if (entry.getKey().equals("deleted")) {
                continue;
            }

            fail(prefix + "." + entry.getKey() + " is missing");
            return;
        }

        for (Map.Entry<String, MapDifference.ValueDifference<Object>> entry : differences.entriesDiffering().entrySet()) {
            Object expected = entry.getValue().leftValue();
            Object actual = entry.getValue().rightValue();

            // nested maps
            if (expected instanceof Map) {
                assertTrue(prefix + "." + entry.getKey() + " has mismatched type.", actual instanceof Map);
                assertMapEquals(prefix + "." + entry.getKey(),
                        (Map<String, Object>) expected,
                        (Map<String, Object>) actual);
                continue;
            }

            if (expected instanceof String && ((String) expected).endsWith("+00:00")) {
                expected = ((String) expected).replace("+00:00", "Z");
            }

            if (actual instanceof Long && expected instanceof Integer) {
                expected = (long) (Integer) expected;
            }

            if (expected == null) {
                assertNull(prefix + "." + entry.getKey(), actual);
            } else {
                assertNotNull(prefix + "." + entry.getKey(), actual);
            }

            assertEquals(prefix + "." + entry.getKey() + " has mismatched value.", expected.getClass(), actual.getClass());
            assertEquals(prefix + "." + entry.getKey() + " has mismatched value.", expected, actual);
        }
    }

    private String objectJsonName(Class klass) {
        if (Objects.equal(klass, CardBrand.class)) {
            return "/data/objects/card_brand_object.json";
        } else if (Objects.equal(klass, PaymentMethod.class)) {
            return "/data/objects/payment_method_object.json";
        } else if (Objects.equal(klass, Barcode.class)) {
            return "/data/objects/barcode_object.json";
        } else {
            return "/data/objects/" + klass.getSimpleName().toLowerCase() + "_object.json";
        }
    }
}
