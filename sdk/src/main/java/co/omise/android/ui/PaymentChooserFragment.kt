package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.SupportedEcontext
import co.omise.android.models.backendType
import co.omise.android.models.installmentMethods
import co.omise.android.models.internetBankingMethods
import co.omise.android.models.mobileBankingMethods
import java.lang.IllegalArgumentException

/**
 * PaymentChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available payment method options list for the user to choose from.
 */
class PaymentChooserFragment : OmiseListFragment<PaymentChooserItem>() {

    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null
    val capability: Capability by lazy { requireNotNull(arguments?.getParcelable(EXTRA_CAPABILITY)) { "Capability must not be null." } }

    override fun listItems(): List<PaymentChooserItem> {
        return getPaymentChoosersFrom(capability)
    }

    override fun onListItemClicked(item: PaymentChooserItem) {
        val navigation = this.navigation ?: throw IllegalArgumentException("PaymentCreatorNavigation must not be null.")
        when (item) {
            PaymentChooserItem.CreditCard -> navigation.navigateToCreditCardForm()
            PaymentChooserItem.Installments -> capability.installmentMethods.let(navigation::navigateToInstallmentChooser)
            PaymentChooserItem.InternetBanking -> capability.internetBankingMethods.let(navigation::navigateToInternetBankingChooser)
            PaymentChooserItem.MobileBanking -> capability.mobileBankingMethods.let(navigation::navigateToMobileBankingChooser)
            PaymentChooserItem.ConvenienceStore -> navigation.navigateToEContextForm(SupportedEcontext.ConvenienceStore)
            PaymentChooserItem.PayEasy -> navigation.navigateToEContextForm(SupportedEcontext.PayEasy)
            PaymentChooserItem.Netbanking -> navigation.navigateToEContextForm(SupportedEcontext.Netbanking)
            PaymentChooserItem.TrueMoney -> navigation.navigateToTrueMoneyForm()
            PaymentChooserItem.TescoLotus,
            PaymentChooserItem.Alipay,
            PaymentChooserItem.PayNow,
            PaymentChooserItem.PromptPay,
            PaymentChooserItem.PointsCiti -> item.sourceType?.let(::sendRequest)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = getString(R.string.payment_chooser_title)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_payment_chooser, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close_menu -> {
                activity?.let {
                    it.setResult(Activity.RESULT_CANCELED)
                    it.finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendRequest(sourceType: SourceType) {
        val requester = requester ?: return

        val request = Source.CreateSourceRequestBuilder(requester.amount, requester.currency, sourceType).build()
        view?.let { setAllViewsEnabled(it, false) }

        requester.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    private fun getPaymentChoosersFrom(capability: Capability): List<PaymentChooserItem> {
        val item = arrayListOf<PaymentChooserItem>()
        capability
                .paymentMethods
                .orEmpty()
                .forEach {
                    when (it.backendType) {
                        is BackendType.Token -> item.add(PaymentChooserItem.CreditCard)
                        is BackendType.Source -> when ((it.backendType as BackendType.Source).sourceType) {
                            is SourceType.Installment -> item.add(PaymentChooserItem.Installments)
                            is SourceType.InternetBanking -> item.add(PaymentChooserItem.InternetBanking)
                            is SourceType.MobileBanking -> item.add(PaymentChooserItem.MobileBanking)
                            is SourceType.BillPaymentTescoLotus -> item.add(PaymentChooserItem.TescoLotus)
                            is SourceType.Econtext -> item.addAll(listOf(
                                    PaymentChooserItem.ConvenienceStore,
                                    PaymentChooserItem.PayEasy,
                                    PaymentChooserItem.Netbanking
                            ))
                            is SourceType.Alipay -> item.add(PaymentChooserItem.Alipay)
                            is SourceType.PayNow -> item.add(PaymentChooserItem.PayNow)
                            is SourceType.PromptPay -> item.add(PaymentChooserItem.PromptPay)
                            is SourceType.PointsCiti -> item.add(PaymentChooserItem.PointsCiti)
                            is SourceType.TrueMoney -> item.add(PaymentChooserItem.TrueMoney)
                        }
                    }
                }
        return item.distinct()
    }

    companion object {
        private const val EXTRA_CAPABILITY = "PaymentChooserFragment.capability"

        fun newInstance(capability: Capability): PaymentChooserFragment {
            return PaymentChooserFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_CAPABILITY, capability)
                }
            }
        }
    }
}

