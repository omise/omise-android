package co.omise.android.models

import co.omise.android.api.TypedCall

object ModelParserUtil {

    fun parseModelFromJson(json: String, call: TypedCall): Model? {
        return when(call.clazz){
            is Token -> Token(json)
            else -> null
        }
    }
}