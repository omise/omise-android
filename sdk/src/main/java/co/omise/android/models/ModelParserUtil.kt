package co.omise.android.models

import co.omise.android.api.TypedCall

object ModelParserUtil {

    fun parseModelFromJson(json: String, call: TypedCall): Model? {
        return if (call.clazz.isAssignableFrom(Token::class.java)) {
            Token(json)
        } else {
            null
        }
    }
}
