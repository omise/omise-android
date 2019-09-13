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
        title = (installment?.backendType as? BackendType.Source)?.sourceType?.name.orEmpty()
        setHasOptionsMenu(true)
    }

    override fun listItems(): List<InstallmentTermChooserItem> {
        return installment?.installmentTerms.orEmpty().map {
            InstallmentTermChooserItem(R.drawable.payment_installment, it, R.drawable.ic_redirect)
        }
    }

    override fun onListItemClicked(item: InstallmentTermChooserItem) {
        val req = requester ?: return
        val sourceType = (installment?.backendType as? BackendType.Source)?.sourceType ?: return

        setUiEnabled(false)
        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType)
                .installmentTerm(item.installmentTerm)
                .build()
        requester?.request(request) {
            setUiEnabled(true)
        }
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
        val installmentTerm: Int,
        override val indicatorIcon: Int
) : OmiseListItem {
    override val title: String
        get() = "$installmentTerm months"
}
