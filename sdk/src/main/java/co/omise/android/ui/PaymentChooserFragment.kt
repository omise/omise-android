package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.*

/**
 * PaymentChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available payment method options list for the user to choose from.
 */
class PaymentChooserFragment : OmiseListFragment<PaymentChooserItem>() {

    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null
    val capability: Capability? by lazy { arguments?.getParcelable<Capability>(EXTRA_CAPABILITY) }

    override fun listItems(): List<PaymentChooserItem> {
        return capability?.let { getPaymentChoosersFrom(it) } ?: emptyList()
    }

    override fun onListItemClicked(item: PaymentChooserItem) {
        when (item) {
            PaymentChooserItem.CreditCard -> navigation?.navigateToCreditCardForm()
            PaymentChooserItem.Installments -> navigation?.navigateToInstallmentChooser(
                    capability
                            ?.paymentMethods
                            ?.filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.Installment }
                            .orEmpty()
            )
            PaymentChooserItem.InternetBanking -> navigation?.navigateToInternetBankingChooser(
                    capability
                            ?.paymentMethods
                            ?.filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.InternetBanking }
                            .orEmpty()
            )
            PaymentChooserItem.TescoLotus -> sendRequest(SourceType.BillPaymentTescoLotus)
            PaymentChooserItem.ConvenienceStore -> navigation?.navigateToEContextForm(SupportedEcontext.ConvenienceStore)
            PaymentChooserItem.PayEasy -> navigation?.navigateToEContextForm(SupportedEcontext.PayEasy)
            PaymentChooserItem.Netbanking -> navigation?.navigateToEContextForm(SupportedEcontext.Netbanking)
            PaymentChooserItem.Alipay -> sendRequest(SourceType.Alipay)
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
                            is SourceType.BillPaymentTescoLotus -> item.add(PaymentChooserItem.TescoLotus)
                            is SourceType.Econtext -> item.addAll(listOf(
                                    PaymentChooserItem.ConvenienceStore,
                                    PaymentChooserItem.PayEasy,
                                    PaymentChooserItem.Netbanking
                            ))
                            is SourceType.Alipay -> item.add(PaymentChooserItem.Alipay)
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

sealed class PaymentChooserItem(
        @DrawableRes override val iconRes: Int,
        @StringRes override val titleRes: Int?,
        @DrawableRes override val indicatorIconRes: Int
) : OmiseListItem {
    object CreditCard : PaymentChooserItem(
            iconRes = R.drawable.payment_card,
            titleRes = R.string.payment_method_credit_card_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object Installments : PaymentChooserItem(
            iconRes = R.drawable.payment_installment,
            titleRes = R.string.payment_method_installments_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object InternetBanking : PaymentChooserItem(
            iconRes = R.drawable.payment_banking,
            titleRes = R.string.payment_method_internet_banking_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object TescoLotus : PaymentChooserItem(
            iconRes = R.drawable.payment_tesco,
            titleRes = R.string.payment_method_tesco_lotus_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    object ConvenienceStore : PaymentChooserItem(
            iconRes = R.drawable.payment_conbini,
            titleRes = R.string.payment_method_convenience_store_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object PayEasy : PaymentChooserItem(
            iconRes = R.drawable.payment_payeasy,
            titleRes = R.string.payment_method_pay_easy_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object Netbanking : PaymentChooserItem(
            iconRes = R.drawable.payment_netbank,
            titleRes = R.string.payment_method_netbank_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object Alipay : PaymentChooserItem(
            iconRes = R.drawable.payment_alipay,
            titleRes = R.string.payment_method_alipay_title,
            indicatorIconRes = R.drawable.ic_redirect
    )
}
