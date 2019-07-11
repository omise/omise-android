package co.omise.android.models

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

                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false) // TODO: Deprecate in vNext
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
    fun <T : Model> deserialize(input: InputStream, klass: Class<T>): T {
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
}
