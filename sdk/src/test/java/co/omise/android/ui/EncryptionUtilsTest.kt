import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.EncryptionUtils
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionUtilsTest {
    @Test
    fun testHash512() {
        val input = "test"
        val expectedHash =
            byteArrayOf(
                0xee.toByte(), 0x26.toByte(), 0xb0.toByte(), 0xdd.toByte(),
                0x4a.toByte(), 0xf7.toByte(), 0xe7.toByte(), 0x49.toByte(),
                0xaa.toByte(), 0x1a.toByte(), 0x8e.toByte(), 0xe3.toByte(),
                0xc1.toByte(), 0x0a.toByte(), 0xe9.toByte(), 0x92.toByte(),
                0x3f.toByte(), 0x61.toByte(), 0x89.toByte(), 0x80.toByte(),
                0x77.toByte(), 0x2e.toByte(), 0x47.toByte(), 0x3f.toByte(),
                0x88.toByte(), 0x19.toByte(), 0xa5.toByte(), 0xd4.toByte(),
                0x94.toByte(), 0x0e.toByte(), 0x0d.toByte(), 0xb2.toByte(),
                0x7a.toByte(), 0xc1.toByte(), 0x85.toByte(), 0xf8.toByte(),
                0xa0.toByte(), 0xe1.toByte(), 0xd5.toByte(), 0xf8.toByte(),
                0x4f.toByte(), 0x88.toByte(), 0xbc.toByte(), 0x88.toByte(),
                0x7f.toByte(), 0xd6.toByte(), 0x7b.toByte(), 0x14.toByte(),
                0x37.toByte(), 0x32.toByte(), 0xc3.toByte(), 0x04.toByte(),
                0xcc.toByte(), 0x5f.toByte(), 0xa9.toByte(), 0xad.toByte(),
                0x8e.toByte(), 0x6f.toByte(), 0x57.toByte(), 0xf5.toByte(),
                0x00.toByte(), 0x28.toByte(), 0xa8.toByte(), 0xff.toByte(),
            )

        val actualHash = EncryptionUtils.hash512(input)

        assertArrayEquals(expectedHash, actualHash)
    }

    @Test
    fun testAesDecrypt() {
        val plainApiKey = "testApiKey"
        val key = EncryptionUtils.hash512("testDecryptionKey").copyOf(32)
        val ciphertext = Base64.decode("H1f1iHaGbI/HNEnCaezDYqxdgahumi8Hf1I=", Base64.DEFAULT)
        val decrypted = EncryptionUtils.aesDecrypt(ciphertext, key)

        assertEquals(plainApiKey, String(decrypted, Charsets.UTF_8))
    }
}
