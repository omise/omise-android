package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.backendType

/**
 * InstallmentTermChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Installment terms list for the user to choose from. User would be directed to this page
 * from [InstallmentChooserFragment] page.
 */
internal class InstallmentTermChooserFragment : OmiseListFragment<InstallmentTermResource>() {
    var requester: PaymentCreatorRequester<Source>? = null
    private val installment: PaymentMethod? by lazy {
        arguments?.getParcelable(EXTRA_INSTALLMENT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title =
            InstallmentResource.all
                .find { it.sourceType == (installment?.backendType as BackendType.Source).sourceType }
                ?.let { item -> item.titleRes?.let { getString(it) } ?: title }
        setHasOptionsMenu(true)
    }

    override fun listItems(): List<InstallmentTermResource> {
        return installment
            ?.installmentTerms
            .orEmpty()
            .map {
                InstallmentTermResource(
                    title =
                        with(it) {
                            if (this > 1) {
                                getString(R.string.payment_method_installment_term_months_title, this)
                            } else {
                                getString(R.string.payment_method_installment_term_month_title, this)
                            }
                        },
                    installmentTerm = it,
                )
            }
    }

    override fun onListItemClicked(item: InstallmentTermResource) {
        val req = requester ?: return
        val sourceType = (installment?.backendType as? BackendType.Source)?.sourceType ?: return

        view?.let { setAllViewsEnabled(it, false) }
        val request =
            Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType)
                .installmentTerm(item.installmentTerm)
                .zeroInterestInstallments(req.capability.zeroInterestInstallments)
                .build()
        requester?.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    companion object {
        private const val EXTRA_INSTALLMENT = "InstallmentTermChooserFragment.installment"

        fun newInstance(installment: PaymentMethod) =
            InstallmentTermChooserFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(EXTRA_INSTALLMENT, installment)
                    }
            }
    }
}
