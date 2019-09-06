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

        private fun getPaymentChoosersFrom(capability: Capability): List<PaymentChooserItem> =
                capability
                        .paymentMethods
                        .orEmpty()
                        .mapNotNull {
                            when (it.method) {
                                is PaymentMethodType.CreditCard -> PaymentChooserItem.CreditCard
                                is PaymentMethodType.Installment -> PaymentChooserItem.Installments
                                is PaymentMethodType.InternetBanking -> PaymentChooserItem.InternetBanking
                                else -> null
                            }
                        }
                        .distinct()
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
    object InternetBanking : PaymentChooserItem(R.drawable.payment_card, "Internet Banking", R.drawable.ic_next)
}
