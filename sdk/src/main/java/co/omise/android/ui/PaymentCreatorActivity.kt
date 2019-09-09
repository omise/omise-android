package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_PKEY

class PaymentCreatorActivity : OmiseActivity() {

    private val pkey: String by lazy { intent.getStringExtra(EXTRA_PKEY) }
    private val amount: Long by lazy { intent.getLongExtra(EXTRA_AMOUNT, 0) }
    private val currency: String by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val capability: Capability by lazy { intent.getParcelableExtra<Capability>(EXTRA_CAPABILITY) }

    private val navigation: PaymentCreatorNavigation by lazy {
        PaymentCreatorNavigationImpl(this, pkey, REQUEST_CREDIT_CARD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_creator)

        listOf(EXTRA_PKEY, EXTRA_AMOUNT, EXTRA_CURRENCY, EXTRA_CAPABILITY).forEach {
            require(intent.hasExtra(it)) { "Can not found $it." }
        }

        navigation.navigateToPaymentChooser(capability)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.payment_creator_container) is PaymentChooserFragment) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CREDIT_CARD && resultCode == Activity.RESULT_OK) {
            val token = data?.getParcelableExtra<Token>(EXTRA_TOKEN_OBJECT)
            val intent = Intent().apply {
                putExtra(EXTRA_TOKEN, token?.id)
                putExtra(EXTRA_TOKEN_OBJECT, token)
                putExtra(EXTRA_CARD_OBJECT, token?.card)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        private const val REQUEST_CREDIT_CARD = 100
    }
}

interface PaymentCreatorNavigation {
    fun navigateToPaymentChooser(capability: Capability)
    fun navigateToCreditCardForm()
    fun navigateToInternetBankingChooser()
    fun navigateToInstallmentChooser()
    fun navigateToEContextForm()
}

private class PaymentCreatorNavigationImpl(
        private val activity: PaymentCreatorActivity,
        private val pkey: String,
        private val requestCode: Int
) : PaymentCreatorNavigation {
    companion object {
        const val FRAGMENT_STACK = "PaymentCreatorNavigation.fragmentStack"
    }

    private val supportFragmentManager = activity.supportFragmentManager

    private fun addFragmentToBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.payment_creator_container, fragment)
                .addToBackStack(FRAGMENT_STACK)
                .commit()
    }

    override fun navigateToPaymentChooser(capability: Capability) {
        val fragment = PaymentChooserFragment.newInstance(capability).apply {
            navigation = this@PaymentCreatorNavigationImpl
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToCreditCardForm() {
        val intent = Intent(activity, CreditCardActivity::class.java).apply {
            putExtra(EXTRA_PKEY, pkey)
        }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun navigateToInternetBankingChooser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToInstallmentChooser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToEContextForm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
