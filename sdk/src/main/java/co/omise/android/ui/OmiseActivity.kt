package co.omise.android.ui

import androidx.appcompat.app.AppCompatActivity


abstract class OmiseActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PKEY = "OmiseActivity.pkey"
        const val EXTRA_SOURCE_OBJECT = "OmiseActivity.sourceObject"
        const val EXTRA_TOKEN_OBJECT = "OmiseActivity.tokenObject"
        const val EXTRA_AMOUNT = "OmiseActivity.amount"
        const val EXTRA_CURRENCY = "OmiseActivity.currency"
        const val EXTRA_CAPABILITY = "OmiseActivity.capability"
    }
}
