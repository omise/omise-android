package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_PKEY
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_SOURCE_OBJECT
import co.omise.android.ui.PaymentCreatorActivity.Companion.REQUEST_CREDIT_CARD

class PaymentCreatorActivity : OmiseActivity() {

    private val pkey: String by lazy { intent.getStringExtra(EXTRA_PKEY) }
    private val amount: Long by lazy { intent.getLongExtra(EXTRA_AMOUNT, 0) }
    private val currency: String by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val capability: Capability by lazy { intent.getParcelableExtra<Capability>(EXTRA_CAPABILITY) }


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
    fun navigateToInternetBankingChooser(availableBanks: List<PaymentMethod>)
    fun navigateToInstallmentChooser()
    fun navigateToEContextForm()
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
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToCreditCardForm() {
        val intent = Intent(activity, CreditCardActivity::class.java).apply {
            putExtra(EXTRA_PKEY, pkey)
        }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun navigateToInternetBankingChooser(availableBanks: List<PaymentMethod>) {
        val fragment = InternetBankingChooserFragment.newInstance(availableBanks).apply {
            requester = this@PaymentCreatorNavigationImpl.requester
        }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToInstallmentChooser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToEContextForm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createSourceFinished(source: Source) {
        val intent = Intent().apply {
            putExtra(EXTRA_SOURCE_OBJECT, source)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }
}

interface PaymentCreatorRequester<T> {
    fun request(parameter: PaymentCreatorParameters, result: ((Result<T>) -> Unit)? = null)
    var listener: PaymentCreatorRequestListener?

    sealed class PaymentCreatorParameters(val sourceType: SourceType) {
        data class InternetBanking(val type: SourceType) : PaymentCreatorParameters(type)
        data class Installment(val bank: SourceType, val numberOfTerms: Int) : PaymentCreatorParameters(bank)
        data class EContext(val type: SourceType, val name: String, val email: String, val phoneNumber: String) : PaymentCreatorParameters(type)
        data class Billing(val type: SourceType) : PaymentCreatorParameters(type)
        data class Unknown(val type: SourceType) : PaymentCreatorParameters(type)
    }
}

interface PaymentCreatorRequestListener {
    fun onSourceCreated(result: Result<Source>)
}

private class PaymentCreatorRequesterImpl(
        private val client: Client,
        private val amount: Long,
        private val currency: String
) : PaymentCreatorRequester<Source> {

    override var listener: PaymentCreatorRequestListener? = null

    override fun request(parameter: PaymentCreatorRequester.PaymentCreatorParameters, result: ((Result<Source>) -> Unit)?) {
        val request = Source
                .CreateSourceRequestBuilder(amount, currency, parameter.sourceType)
                .build()
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
