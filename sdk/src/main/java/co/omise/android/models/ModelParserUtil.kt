package co.omise.android.models

import co.omise.android.api.TypedCall

/**
 * Singleton class for parsing API response based on supplied return Class type
 */
object ModelParserUtil {

    fun parseModelFromJson(json: String, call: TypedCall): Model? {
        return ModelParserUtil.parseModelFromJson(json, call.clazz)
    }

    @JvmStatic
    fun parseModelFromJson(json: String, clazz: Class<*>): Model? {
        return when {
            clazz.isAssignableFrom(Token::class.java) -> Token(json)
            clazz.isAssignableFrom(Capability::class.java) -> Capability(json)
            clazz.isAssignableFrom(PaymentMethod::class.java) -> PaymentMethod(json)
            else -> null
        }
    }
}
