package co.omise.android.ui

import android.os.Bundle
import android.view.WindowManager

/**
 * CreditCardActivity is the UI class for taking credit card information input from the user.
 */
class CreditCardActivity : OmiseActivity() {
    private lateinit var pKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getBooleanExtra(EXTRA_IS_SECURE, true)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        require(intent.hasExtra(EXTRA_PKEY)) { "Could not find ${::EXTRA_PKEY.name}." }
        pKey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }

    }
}
