package co.omise.android.utils

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.RequestBody
import okio.Buffer
import org.junit.Assert.assertEquals

fun assertRequestBodyEquals(
    expectedJson: String,
    actualRequestBody: RequestBody,
) {
    val buffer = Buffer()
    actualRequestBody.writeTo(buffer)
    val mapper = ObjectMapper()
    val actualJson = buffer.readUtf8()
    assertEquals(mapper.readTree(expectedJson), mapper.readTree(actualJson))
}
