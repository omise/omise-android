package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.method
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_payment_creator.payment_creator_container

class PaymentCreatorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SOURCE_OBJECT = "PaymentCreatorActivity.sourceObject"
        const val EXTRA_TOKEN_OBJECT = "PaymentCreatorActivity.tokenObject"
        const val EXTRA_PKEY = "PaymentCreatorActivity.pkey"
        const val EXTRA_AMOUNT = "PaymentCreatorActivity.amount"
        const val EXTRA_CURRENCY = "PaymentCreatorActivity.currency"
        const val EXTRA_CAPABILITY = "PaymentCreatorActivity.capability"
    }

    private val pkey: String by lazy { intent.getStringExtra(EXTRA_PKEY) }
    private val amount: Long by lazy { intent.getLongExtra(EXTRA_AMOUNT, 0) }
    private val currency: String by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val capability: Capability by lazy { intent.getParcelableExtra<Capability>(EXTRA_CAPABILITY) }

    private val rootView: View by lazy { payment_creator_container }
    private val snackbar: Snackbar by lazy { Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_creator)

        listOf(EXTRA_PKEY, EXTRA_AMOUNT, EXTRA_CURRENCY, EXTRA_CAPABILITY).forEach {
            if (!intent.hasExtra(it)) {
                throw IllegalAccessException("Can not found $it.")
            }
        }

        Log.d("PaymentCreator", "${capability.paymentMethods?.first()?.method}}")
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.payment_creator_container) is PaymentChooserFragment) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}

interface PaymentCreatorNavigation {
    fun navigateToPaymentMethodChooser()
    fun navigateToCreditCardForm()
    fun navigateToInternetBankingChooser()
    fun navigateToInstallmentChooser()
    fun navigateToEContextForm()
}

class PaymentCreatorNavigationImpl(activity: PaymentCreatorActivity) : PaymentCreatorNavigation {
    override fun navigateToPaymentMethodChooser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToCreditCardForm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
