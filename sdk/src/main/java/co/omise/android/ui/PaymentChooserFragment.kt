package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

class PaymentChooserFragment : OmiseListFragment<PaymentChooserItem>() {

    var navigation: PaymentCreatorNavigation? = null
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
            PaymentChooserItem.TescoLotus -> TODO()
            PaymentChooserItem.ConvenienceStore -> TODO()
            PaymentChooserItem.PayEasy -> TODO()
            PaymentChooserItem.Netbanking -> TODO()
            PaymentChooserItem.Alipay -> TODO()
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
        override val icon: Int,
        override val title: String,
        override val indicatorIcon: Int
) : OmiseListItem {
    object CreditCard : PaymentChooserItem(R.drawable.payment_card, "Credit Card", R.drawable.ic_next)
    object Installments : PaymentChooserItem(R.drawable.payment_installment, "Installments", R.drawable.ic_next)
    object InternetBanking : PaymentChooserItem(R.drawable.payment_banking, "Internet Banking", R.drawable.ic_next)
    object TescoLotus : PaymentChooserItem(R.drawable.payment_tesco, "Tesco Lotus", R.drawable.ic_redirect)
    object ConvenienceStore : PaymentChooserItem(R.drawable.payment_conbini, "Convenience Store", R.drawable.ic_next)
    object PayEasy : PaymentChooserItem(R.drawable.payment_payeasy, "Pay-easy", R.drawable.ic_next)
    object Netbanking : PaymentChooserItem(R.drawable.payment_netbank, "Netbanking", R.drawable.ic_next)
    object Alipay : PaymentChooserItem(R.drawable.payment_alipay, "Alipay", R.drawable.ic_redirect)
}
