package co.omise.android.models

import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer
import com.fasterxml.jackson.datatype.joda.ser.LocalDateSerializer
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class Serializer {
    /**
     * Returns the pre-configured [ObjectMapper] used for
     * serializing and deserializing Omise API objects.
     *
     * @return An [ObjectMapper] instance.
     */
    var objectMapper: ObjectMapper

    /**
     * Returns the pre-configured {@link DateTimeFormatter} used for
     * serializing and deserializing date and times for Omise API objects.
     *
     * @return A {@link DateTimeFormatter} instance.
     */
    var dateTimeFormatter: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    /**
     * Returns the pre-configured {@link DateTimeFormatter} used for
     * serializing and deserializing date for Omise API objects.
     *
     * @return A {@link DateTimeFormatter} instance.
     */
    var localDateFormatter: DateTimeFormatter = ISODateTimeFormat.date()

    init {
        objectMapper = ObjectMapper()
                .registerModule(JodaModule()
                        .addSerializer(DateTime::class.java, DateTimeSerializer()
                                .withFormat(JacksonJodaDateFormat(dateTimeFormatter), 0)
                        )
                        .addSerializer(LocalDate::class.java, LocalDateSerializer()
                                .withFormat(JacksonJodaDateFormat(localDateFormatter), 0)
                        )
                )
                .setDefaultPropertyInclusion(JsonInclude.Value.construct(
                        JsonInclude.Include.ALWAYS, JsonInclude.Include.NON_NULL)
                )
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    /**
     * Deserialize an instance of the given class from the input stream.
     *
     * @param input The [InputStream] that contains the data to deserialize.
     * @param klass The [Class] to deserialize the result into.
     * @param <T>   The type to deserialize the result into.
     * @return An instance of type T deserialized from the input stream.
     * @throws IOException on general I/O error.
    </T> */
    @Throws(IOException::class)
    fun <T : Model> deserialize(input: InputStream, klass: Class<*>): T {
        return objectMapper.readerFor(klass).readValue(input)
    }

    /**
     * Deserialize an instance of the given class from the input stream.
     *
     * @param input The [InputStream] that contains the data to deserialize.
     * @param klass The [Class] to deserialize the result into.
     * @param <T>   The type to deserialize the result into.
     * @return An instance of type T deserialized from the input stream.
     * @throws IOException on general I/O error.
    </T> */
    @Throws(IOException::class)
    fun <T : Error> deserialize(input: InputStream, klass: Class<T>): T {
        return objectMapper.readerFor(klass).readValue(input)
    }

    /**
     * Serialize the given model to a map with JSON-like structure.
     *
     * @param model The [Model] to serialize.
     * @param <T>   The type of the model to serialize.
     * @return The map containing the model's data.
    </T> */
    fun <T : Model> serializeToMap(model: T): Map<String, Any> {
        return objectMapper.convertValue(model, object : TypeReference<Map<String, Any>>() {})
    }

    /**
     * Serializes the given [RequestBuilder] object to the provided output stream.
     *
     * @param outputStream The [OutputStream] to serialize the parameter into.
     * @param builder      The [RequestBuilder] to serialize.
     * @param <T>          The type of the parameter object to serialize.
     * @throws IOException on general I/O error.
    </T> */
    @Throws(IOException::class)
    fun <T : RequestBuilder<*>> serializeRequestBuilder(outputStream: OutputStream, builder: T) {
        objectMapper.writerFor(builder.javaClass).writeValue(outputStream, builder)
    }

    fun objectMapper(): ObjectMapper {
        return objectMapper
    }
}
