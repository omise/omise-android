package co.omise.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethodType
import co.omise.android.models.method
import kotlinx.android.parcel.Parcelize

class PaymentChooserFragment : OmiseListFragment<PaymentChooserItem>() {

    var navigation: PaymentCreatorNavigation? = null

    override fun onListItemClicked(item: PaymentChooserItem) {
        when (item) {
            PaymentChooserItem.CreditCard -> navigation?.navigateToCreditCardForm()
            PaymentChooserItem.Installments -> TODO()
            PaymentChooserItem.InternetBanking -> TODO()
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

    companion object {
        private const val EXTRA_CAPABILITY = "PaymentChooserFragment.capability"

        fun newInstance(capability: Capability): PaymentChooserFragment {
            return PaymentChooserFragment().apply {
                val arguments = Bundle().apply {
                    putParcelable(EXTRA_CAPABILITY, capability)
                    putParcelableArray(EXTRA_LIST_ITEMS, getPaymentChoosersFrom(capability).toTypedArray())
                }
                this.arguments = arguments
            }
        }

        private fun getPaymentChoosersFrom(capability: Capability): List<PaymentChooserItem> {
            val item = arrayListOf<PaymentChooserItem>()
            capability
                    .paymentMethods
                    .orEmpty()
                    .forEach {
                        when (it.method) {
                            is PaymentMethodType.CreditCard -> item.add(PaymentChooserItem.CreditCard)
                            is PaymentMethodType.Installment -> item.add(PaymentChooserItem.Installments)
                            is PaymentMethodType.InternetBanking -> item.add(PaymentChooserItem.InternetBanking)
                            is PaymentMethodType.BillPaymentTescoLotus -> item.add(PaymentChooserItem.TescoLotus)
                            is PaymentMethodType.EContext -> item.addAll(listOf(
                                    PaymentChooserItem.ConvenienceStore,
                                    PaymentChooserItem.PayEasy,
                                    PaymentChooserItem.Netbanking
                            ))
                            is PaymentMethodType.Alipay -> item.add(PaymentChooserItem.Alipay)
                        }
                    }
            return item.distinct()
        }
    }
}

sealed class PaymentChooserItem(
        override val icon: Int,
        override val title: String,
        override val indicatorIcon: Int
) : OmiseListItem {
    @Parcelize
    object CreditCard : PaymentChooserItem(R.drawable.payment_card, "Credit Card", R.drawable.ic_next)

    @Parcelize
    object Installments : PaymentChooserItem(R.drawable.payment_installment, "Installments", R.drawable.ic_next)

    @Parcelize
    object InternetBanking : PaymentChooserItem(R.drawable.payment_banking, "Internet Banking", R.drawable.ic_next)

    @Parcelize
    object TescoLotus : PaymentChooserItem(R.drawable.payment_tesco, "Tesco Lotus", R.drawable.ic_redirect)

    @Parcelize
    object ConvenienceStore : PaymentChooserItem(R.drawable.payment_conbini, "Convenience Store", R.drawable.ic_next)

    @Parcelize
    object PayEasy : PaymentChooserItem(R.drawable.payment_payeasy, "Pay-easy", R.drawable.ic_next)

    @Parcelize
    object Netbanking : PaymentChooserItem(R.drawable.payment_netbank, "Netbanking", R.drawable.ic_next)

    @Parcelize
    object Alipay : PaymentChooserItem(R.drawable.payment_alipay, "Alipay", R.drawable.ic_redirect)
}
