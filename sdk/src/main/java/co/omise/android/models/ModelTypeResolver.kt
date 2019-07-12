package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import java.util.*

class ModelTypeResolver : TypeIdResolverBase() {
    private var types: Map<String, Class<*>>? = null

    fun getKnownTypes(): Map<String, Class<*>> {
        if (types == null) {
            types = HashMap()
            (types as HashMap<String, Class<*>>)["card"] = Card::class.java
            (types as HashMap<String, Class<*>>)["token"] = Token::class.java
            (types as HashMap<String, Class<*>>)["capability"] = Capability::class.java
            (types as HashMap<String, Class<*>>)["payment_method"] = PaymentMethod::class.java
            (types as HashMap<String, Class<*>>)["error"] = APIError::class.java
        }
        return Collections.unmodifiableMap(types)
    }

    override fun idFromValue(value: Any): String {
        return idFromValueAndType(value, value::class.java)
    }

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>): String {
        return reverse(getKnownTypes())[suggestedType] ?: ""
    }

    override fun typeFromId(context: DatabindContext, id: String): JavaType? {
        val klass = getKnownTypes()[id] ?: return null
        return context.typeFactory.constructSimpleType(klass, arrayOf())
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.CUSTOM
    }

    private fun reverse(map: Map<String, Class<*>>): Map<Class<*>, String> {
        val reversedMap = HashMap<Class<*>, String>()
        for ((key, value) in map) {
            reversedMap[value] = key
        }
        return reversedMap
    }
}
