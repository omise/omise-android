package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.backendType


internal class InstallmentTermChooserFragment : OmiseListFragment<InstallmentTermChooserItem>() {
    var requester: PaymentCreatorRequester<Source>? = null
    private val installment: PaymentMethod? by lazy {
        arguments?.getParcelable<PaymentMethod>(EXTRA_INSTALLMENT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = (installment?.backendType as BackendType.Source).sourceType.name.orEmpty()
        setHasOptionsMenu(true)
    }

    override fun listItems(): List<InstallmentTermChooserItem> {
        return installment?.installmentTerms.orEmpty().map {
            InstallmentTermChooserItem(
                    R.drawable.payment_installment,
                    "$it months",
                    R.drawable.ic_redirect
            )
        }
    }

    override fun onListItemClicked(option: InstallmentTermChooserItem) {
//        val sourceType = installment?.sourceType ?: return
//        paymentCreatorFlow?.request(PaymentCreatorParameter.Installment(sourceType, option.terms))
    }

    companion object {
        private const val EXTRA_INSTALLMENT = "InstallmentTermChooserFragment.installment"
        fun newInstance(installment: PaymentMethod) =
                InstallmentTermChooserFragment().apply {
                    val args = Bundle().apply {
                        putParcelable(EXTRA_INSTALLMENT, installment)
                    }
                    arguments = args
                }
    }
}

internal data class InstallmentTermChooserItem(
        override val icon: Int,
        override val title: String,
        override val indicatorIcon: Int
) : OmiseListItem
