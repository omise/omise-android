package co.omise.android.models

import co.omise.android.api.TypedCall

/**
 * Singleton class for parsing API response based on supplied return Class type
 */
object ModelParserUtil {

    fun parseModelFromJson(json: String, call: TypedCall): Model? {
        return if (call.clazz.isAssignableFrom(Token::class.java)) {
            Token(json)
        } else {
            null
        }
    }
}
