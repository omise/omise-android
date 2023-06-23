package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import co.omise.android.R
import co.omise.android.models.*

/**
 * PaymentChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available payment method options list for the user to choose from.
 */
internal class PaymentChooserFragment : OmiseListFragment<PaymentMethodResource>() {

    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null
    val capability: Capability by lazy { requireNotNull(arguments?.getParcelable(EXTRA_CAPABILITY)) { "Capability must not be null." } }

    override fun listItems(): List<PaymentMethodResource> {
        return capability.paymentMethodResources
    }

    override fun onListItemClicked(item: PaymentMethodResource) {
        val navigation = this.navigation ?: throw IllegalArgumentException("PaymentCreatorNavigation must not be null.")
        when (item) {
            PaymentMethodResource.CreditCard -> navigation.navigateToCreditCardForm()
            PaymentMethodResource.Installments -> capability.installmentMethods.let(navigation::navigateToInstallmentChooser)
            PaymentMethodResource.InternetBankings -> capability.internetBankingMethods.let(navigation::navigateToInternetBankingChooser)
            PaymentMethodResource.MobileBankings -> capability.mobileBankingMethods.let(navigation::navigateToMobileBankingChooser)
            PaymentMethodResource.ConvenienceStore -> navigation.navigateToEContextForm(SupportedEcontext.ConvenienceStore)
            PaymentMethodResource.PayEasy -> navigation.navigateToEContextForm(SupportedEcontext.PayEasy)
            PaymentMethodResource.Netbanking -> navigation.navigateToEContextForm(SupportedEcontext.Netbanking)
            PaymentMethodResource.TrueMoney -> navigation.navigateToTrueMoneyForm()
            PaymentMethodResource.TescoLotus,
            PaymentMethodResource.Alipay,
            PaymentMethodResource.PayNow,
            PaymentMethodResource.PromptPay,
            PaymentMethodResource.AlipayCn,
            PaymentMethodResource.AlipayHk,
            PaymentMethodResource.Dana,
            PaymentMethodResource.Gcash,
            PaymentMethodResource.Kakaopay,
            PaymentMethodResource.TouchNGo,
            PaymentMethodResource.RabbitLinepay,
            PaymentMethodResource.OcbcPao,
            PaymentMethodResource.Boost,
            PaymentMethodResource.ShopeePay,
            PaymentMethodResource.ShopeePayJumpApp,
            PaymentMethodResource.DuitNowQR,
            PaymentMethodResource.MaybankQR,
            PaymentMethodResource.GrabPay,
            PaymentMethodResource.PayPay,
            PaymentMethodResource.PointsCiti,
            PaymentMethodResource.GrabPay_RMS,
            PaymentMethodResource.TouchNGo_Alipay -> item.sourceType?.let(::sendRequest)
            PaymentMethodResource.Fpx -> navigation.navigateToFpxEmailForm()
            PaymentMethodResource.GooglePay -> navigation.navigateToGooglePayForm()
            PaymentMethodResource.DuitNowOBW -> navigation.navigateToDuitNowOBWBankChooser()
            PaymentMethodResource.Atome -> navigation.navigateToAtomeForm()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = getString(R.string.payment_chooser_title)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
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
