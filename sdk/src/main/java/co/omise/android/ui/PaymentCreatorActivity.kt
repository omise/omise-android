package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.extensions.parcelable
import co.omise.android.models.APIError
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.Model
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SupportedEcontext
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_AMOUNT
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_CARD_BRANDS
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_CURRENCY
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_GOOGLEPAY_MERCHANT_ID
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_IS_SECURE
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_PKEY
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_SOURCE_OBJECT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_payment_creator.payment_creator_container
import java.io.IOError

/**
 * PaymentCreatorActivity is the parent activity that controls the navigation between
 * all the payment method fragments.
 */
class PaymentCreatorActivity : OmiseActivity() {
    private lateinit var pkey: String
    private var amount: Long = 0L
    private lateinit var currency: String
    private lateinit var capability: Capability
    private var cardBrands = arrayListOf<String>()
    private lateinit var googlepayMerchantId: String
    private var googlepayRequestBillingAddress: Boolean = false
    private var googlepayRequestPhoneNumber: Boolean = false
    private val snackbar: Snackbar by lazy { Snackbar.make(payment_creator_container, "", Snackbar.LENGTH_SHORT) }

    private val client: Client by lazy { Client(pkey) }

    private val requester: PaymentCreatorRequester<Source> by lazy {
        PaymentCreatorRequesterImpl(client, amount, currency, capability)
    }

    @VisibleForTesting
    val navigation: PaymentCreatorNavigation by lazy {
        PaymentCreatorNavigationImpl(
            this,
            pkey,
            amount,
            currency,
            cardBrands,
            googlepayMerchantId,
            googlepayRequestBillingAddress,
            googlepayRequestPhoneNumber,
            REQUEST_CREDIT_CARD,
            requester,
            capability,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(EXTRA_IS_SECURE, true)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_payment_creator)

        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.findFragmentById(R.id.payment_creator_container) is PaymentChooserFragment) {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        initialize()

        navigation.navigateToPaymentChooser(capability)

        requester.listener =
            object : PaymentCreatorRequestListener {
                override fun onSourceCreated(result: Result<Source>) {
                    if (result.isSuccess) {
                        result.getOrNull()?.let {
                            navigation.createSourceFinished(it)
                        }
                    } else {
                        val message =
                            when (val error = result.exceptionOrNull()) {
                                is IOError -> getString(R.string.error_io, error.message)
                                is APIError -> error.getMessageFromResources(resources)
                                else -> getString(R.string.error_unknown, error?.message)
                            }
                        result.exceptionOrNull()?.let {
                            snackbar.setText(message)
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

    // TODO: find a way to unit test ActivityResult launcher in order to be able to move from deprecated onActivityResult
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREDIT_CARD_WITH_SOURCE && resultCode == Activity.RESULT_OK) {
            val token = data?.getParcelableExtra<Token>(EXTRA_TOKEN_OBJECT)
            val source = data?.getParcelableExtra<Source>(EXTRA_SOURCE_OBJECT)
            val intent =
                Intent().apply {
                    putExtra(EXTRA_TOKEN, token?.id)
                    putExtra(EXTRA_TOKEN_OBJECT, token)
                    putExtra(EXTRA_CARD_OBJECT, token?.card)
                    putExtra(EXTRA_SOURCE_OBJECT, source)
                }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        if (requestCode == REQUEST_CREDIT_CARD && resultCode == Activity.RESULT_OK) {
            val token = data?.parcelable<Token>(EXTRA_TOKEN_OBJECT)
            val intent =
                Intent().apply {
                    putExtra(EXTRA_TOKEN, token?.id)
                    putExtra(EXTRA_TOKEN_OBJECT, token)
                    putExtra(EXTRA_CARD_OBJECT, token?.card)
                }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun initialize() {
        listOf(EXTRA_PKEY, EXTRA_AMOUNT, EXTRA_CURRENCY, EXTRA_CAPABILITY).forEach {
            require(intent.hasExtra(it)) { "Could not found $it." }
        }
        pkey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }
        amount = intent.getLongExtra(EXTRA_AMOUNT, 0)
        currency = requireNotNull(intent.getStringExtra(EXTRA_CURRENCY)) { "${::EXTRA_CURRENCY.name} must not be null." }
        capability = requireNotNull(intent.parcelable(EXTRA_CAPABILITY)) { "${::EXTRA_CAPABILITY.name} must not be null." }
        val fetchBrands: List<String>? = capability.paymentMethods?.find { it.name == "card" }?.cardBrands
        cardBrands = if (fetchBrands != null) fetchBrands as ArrayList<String> else arrayListOf()
        googlepayMerchantId = intent.getStringExtra(EXTRA_GOOGLEPAY_MERCHANT_ID) ?: "[GOOGLEPAY_MERCHANT_ID]"
        googlepayRequestBillingAddress = intent.getBooleanExtra(EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, false)
        googlepayRequestPhoneNumber = intent.getBooleanExtra(EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, false)
    }

    companion object {
        const val REQUEST_CREDIT_CARD = 100

        // Used for payment methods that require both token and source to be created and the
        // credit card activity is responsible for creating both source and token
        const val REQUEST_CREDIT_CARD_WITH_SOURCE = 101
    }
}

interface PaymentCreatorNavigation {
    fun navigateToPaymentChooser(capability: Capability)

    fun navigateToCreditCardForm()

    fun navigateToInternetBankingChooser(allowedBanks: List<PaymentMethod>)

    fun navigateToMobileBankingChooser(allowedBanks: List<PaymentMethod>)

    fun navigateToInstallmentChooser(allowedInstalls: List<PaymentMethod>)

    fun navigateToInstallmentTermChooser(installment: PaymentMethod)

    fun navigateToEContextForm(eContext: SupportedEcontext)

    fun navigateToAtomeForm()

    fun createSourceFinished(source: Source)

    fun navigateToTrueMoneyForm()

    fun navigateToFpxEmailForm()

    fun navigateToFpxBankChooser(
        banks: List<Bank>?,
        email: String,
    )

    fun navigateToGooglePayForm()

    fun navigateToDuitNowOBWBankChooser()
}

private class PaymentCreatorNavigationImpl(
    private val activity: PaymentCreatorActivity,
    private val pkey: String,
    private val amount: Long,
    private val currency: String,
    private var cardBrands: ArrayList<String>,
    private var googlepayMerchantId: String,
    private var googlepayRequestBillingAddress: Boolean,
    private var googlepayRequestPhoneNumber: Boolean,
    private val requestCode: Int,
    private val requester: PaymentCreatorRequester<Source>,
    private val capability: Capability,
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
        val fragment =
            PaymentChooserFragment.newInstance(capability).apply {
                navigation = this@PaymentCreatorNavigationImpl
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToCreditCardForm() {
        val intent =
            Intent(activity, CreditCardActivity::class.java).apply {
                putExtra(EXTRA_PKEY, pkey)
                putExtra(EXTRA_IS_SECURE, activity.intent.getBooleanExtra(EXTRA_IS_SECURE, true))
            }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun navigateToInternetBankingChooser(allowedBanks: List<PaymentMethod>) {
        val fragment =
            InternetBankingChooserFragment.newInstance(allowedBanks).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToMobileBankingChooser(allowedBanks: List<PaymentMethod>) {
        val fragment =
            MobileBankingChooserFragment.newInstance(allowedBanks).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToInstallmentChooser(allowedInstalls: List<PaymentMethod>) {
        val minInstallmentAmount = capability.limits?.installmentAmount?.min ?: 0

        val fragment =
            InstallmentChooserFragment.newInstance(allowedInstalls, amount, minInstallmentAmount).apply {
                navigation = this@PaymentCreatorNavigationImpl
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToInstallmentTermChooser(installment: PaymentMethod) {
        val fragment =
            InstallmentTermChooserFragment.newInstance(installment).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToEContextForm(eContext: SupportedEcontext) {
        val fragment =
            EContextFormFragment.newInstance(eContext).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToAtomeForm() {
        val fragment =
            AtomeFormFragment().apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun createSourceFinished(source: Source) {
        val intent =
            Intent().apply {
                putExtra(EXTRA_SOURCE_OBJECT, source)
            }
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }

    override fun navigateToTrueMoneyForm() {
        val fragment =
            TrueMoneyFormFragment().apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToFpxEmailForm() {
        val fragment =
            FpxEmailFormFragment().apply {
                navigation = this@PaymentCreatorNavigationImpl
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToFpxBankChooser(
        banks: List<Bank>?,
        email: String,
    ) {
        val fragment =
            FpxBankChooserFragment.newInstance(banks, email).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }
        addFragmentToBackStack(fragment)
    }

    override fun navigateToGooglePayForm() {
        val intent =
            Intent(activity, GooglePayActivity::class.java).apply {
                putExtra(EXTRA_PKEY, pkey)
                putExtra(EXTRA_AMOUNT, amount)
                putExtra(EXTRA_CURRENCY, currency)
                putStringArrayListExtra(EXTRA_CARD_BRANDS, cardBrands)
                putExtra(EXTRA_GOOGLEPAY_MERCHANT_ID, googlepayMerchantId)
                putExtra(EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, googlepayRequestBillingAddress)
                putExtra(EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, googlepayRequestPhoneNumber)
            }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun navigateToDuitNowOBWBankChooser() {
        /**
         *  DuitNow OBW didn't support capability api for banks list
         *  so need to define local banks list
         */
        val banks =
            listOf(
                Bank(name = "Affin Bank", code = "affin", active = true),
                Bank(name = "Alliance Bank (Personal)", code = "alliance", active = true),
                Bank(name = "AGRONet", code = "agro", active = true),
                Bank(name = "AmBank", code = "ambank", active = true),
                Bank(name = "Bank Islam", code = "islam", active = true),
                Bank(name = "Bank Muamalat", code = "muamalat", active = true),
                Bank(name = "Bank Rakyat", code = "rakyat", active = true),
                Bank(name = "BSN", code = "bsn", active = true),
                Bank(name = "CIMB Clicks", code = "cimb", active = true),
                Bank(name = "Hong Leong Bank", code = "hongleong", active = true),
                Bank(name = "HSBC Bank", code = "hsbc", active = true),
                Bank(name = "KFH", code = "kfh", active = true),
                Bank(name = "Maybank2U", code = "maybank2u", active = true),
                Bank(name = "OCBC Bank", code = "ocbc", active = true),
                Bank(name = "Public Bank", code = "public", active = true),
                Bank(name = "RHB Bank", code = "rhb", active = true),
                Bank(name = "Standard Chartered", code = "sc", active = true),
                Bank(name = "UOB Bank", code = "uob", active = true),
            )

        val fragment =
            DuitNowOBWBankChooserFragment.newInstance(banks).apply {
                requester = this@PaymentCreatorNavigationImpl.requester
            }

        addFragmentToBackStack(fragment)
    }
}

interface PaymentCreatorRequester<T : Model> {
    val amount: Long
    val currency: String
    val capability: Capability

    fun request(
        request: Request<T>,
        result: ((Result<T>) -> Unit)? = null,
    )

    var listener: PaymentCreatorRequestListener?
}

interface PaymentCreatorRequestListener {
    fun onSourceCreated(result: Result<Source>)
}

private class PaymentCreatorRequesterImpl(
    private val client: Client,
    override val amount: Long,
    override val currency: String,
    override val capability: Capability,
) : PaymentCreatorRequester<Source> {
    override var listener: PaymentCreatorRequestListener? = null

    override fun request(
        request: Request<Source>,
        result: ((Result<Source>) -> Unit)?,
    ) {
        client.send(
            request,
            object : RequestListener<Source> {
                override fun onRequestSucceed(model: Source) {
                    result?.invoke(Result.success(model))
                    listener?.onSourceCreated(Result.success(model))
                }

                override fun onRequestFailed(throwable: Throwable) {
                    result?.invoke(Result.failure(throwable))
                    listener?.onSourceCreated(Result.failure(throwable))
                }
            },
        )
    }
}
