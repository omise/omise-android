package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.models.*
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_PKEY
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_SOURCE_OBJECT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_payment_creator.*
import java.io.IOError

/**
 * PaymentCreatorActivity is the parent activity that controls the navigation between
 * all the payment method fragments.
 */
class PaymentCreatorActivity : OmiseActivity() {

    private val pkey: String by lazy { intent.getStringExtra(EXTRA_PKEY) }
    private val amount: Long by lazy { intent.getLongExtra(EXTRA_AMOUNT, 0) }
    private val currency: String by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val capability: Capability by lazy { intent.getParcelableExtra<Capability>(EXTRA_CAPABILITY) }
    private val snackbar: Snackbar by lazy { Snackbar.make(payment_creator_container, "", Snackbar.LENGTH_SHORT) }

    private val client: Client by lazy { Client(pkey) }

    private val requester: PaymentCreatorRequester<Source> by lazy {
        PaymentCreatorRequesterImpl(client, amount, currency)
    }

    @VisibleForTesting
    val navigation: PaymentCreatorNavigation by lazy {
        PaymentCreatorNavigationImpl(this, pkey, REQUEST_CREDIT_CARD, requester)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_creator)

        listOf(EXTRA_PKEY, EXTRA_AMOUNT, EXTRA_CURRENCY, EXTRA_CAPABILITY).forEach {
            require(intent.hasExtra(it)) { "Can not found $it." }
        }

        navigation.navigateToPaymentChooser(capability)

        requester.listener = object : PaymentCreatorRequestListener {
            override fun onSourceCreated(result: Result<Source>) {
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        navigation.createSourceFinished(it)
                    }
                } else {
                    val message = when (val error = result.exceptionOrNull()) {
                        is IOError -> getString(R.string.error_io, error.message)
                        is APIError -> error.getMessageFromResources(resources)
                        else -> getString(R.string.error_unknown, error?.message)
                    }
                    result.exceptionOrNull()?.let {
                        snackbar.setText(message.orEmpty())
                        snackbar.show()
                    }
                }
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            supportActionBar?.let {
                if (supportFragmentManager.findFragmentById(R.id.payment_creator_container) is PaymentChooserFragment) {
                    it.setDisplayHomeAsUpEnabled(false)
                    it.setHomeButtonEnabled(false)
                } else {
                    it.setDisplayHomeAsUpEnabled(true)
                    it.setHomeButtonEnabled(true)
                }
            }
        }
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
        const val REQUEST_CREDIT_CARD = 100
    }
}

interface PaymentCreatorNavigation {
    fun navigateToPaymentChooser(capability: Capability)
    fun navigateToCreditCardForm()
    fun navigateToInternetBankingChooser(allowedBanks: List<PaymentMethod>)
    fun navigateToInstallmentChooser(allowedInstalls: List<PaymentMethod>)
    fun navigateToInstallmentTermChooser(installment: PaymentMethod)
    fun navigateToEContextForm(eContext: SupportedEcontext)
    fun createSourceFinished(source: Source)
}

private class PaymentCreatorNavigationImpl(
        private val activity: PaymentCreatorActivity,
        private val pkey: String,
        private val requestCode: Int,
        private val requester: PaymentCreatorRequester<Source>
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
            requester = this@PaymentCreatorNavigationImpl.requester
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToCreditCardForm() {
        val intent = Intent(activity, CreditCardActivity::class.java).apply {
            putExtra(EXTRA_PKEY, pkey)
        }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun navigateToInternetBankingChooser(allowedBanks: List<PaymentMethod>) {
        val fragment = InternetBankingChooserFragment.newInstance(allowedBanks).apply {
            requester = this@PaymentCreatorNavigationImpl.requester
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToInstallmentChooser(allowedInstalls: List<PaymentMethod>) {
        val fragment = InstallmentChooserFragment.newInstance(allowedInstalls).apply {
            navigation = this@PaymentCreatorNavigationImpl
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToInstallmentTermChooser(installment: PaymentMethod) {
        val fragment = InstallmentTermChooserFragment.newInstance(installment).apply {
            requester = this@PaymentCreatorNavigationImpl.requester
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToEContextForm(eContext: SupportedEcontext) {
        val fragment = EContextFormFragment.newInstance(eContext).apply {
            requester = this@PaymentCreatorNavigationImpl.requester
        }
        addFragmentToBackStack(fragment)
    }

    override fun createSourceFinished(source: Source) {
        val intent = Intent().apply {
            putExtra(EXTRA_SOURCE_OBJECT, source)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }
}

interface PaymentCreatorRequester<T : Model> {
    val amount: Long
    val currency: String
    fun request(request: Request<T>, result: ((Result<T>) -> Unit)? = null)
    var listener: PaymentCreatorRequestListener?
}

interface PaymentCreatorRequestListener {
    fun onSourceCreated(result: Result<Source>)
}

private class PaymentCreatorRequesterImpl(
        private val client: Client,
        override val amount: Long,
        override val currency: String
) : PaymentCreatorRequester<Source> {

    override var listener: PaymentCreatorRequestListener? = null

    override fun request(request: Request<Source>, result: ((Result<Source>) -> Unit)?) {
        client.send(request, object : RequestListener<Source> {
            override fun onRequestSucceed(model: Source) {
                result?.invoke(Result.success(model))
                listener?.onSourceCreated(Result.success(model))
            }

            override fun onRequestFailed(throwable: Throwable) {
                result?.invoke(Result.failure(throwable))
                listener?.onSourceCreated(Result.failure(throwable))
            }
        })
    }
}
