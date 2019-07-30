package co.omise.android;

import com.google.common.io.ByteStreams;

import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;

public class OmiseTest extends Assert {
    public byte[] getResourceBytes(String path) throws IOException {
        InputStream stream = getClass().getResourceAsStream(path);
        return stream == null ? null : ByteStreams.toByteArray(stream);
    }
}
