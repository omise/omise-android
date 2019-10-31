package co.omise.android.ui

import android.os.Bundle
import androidx.annotation.DrawableRes
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

/**
 * InstallmentTermChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Installment terms list for the user to choose from. User would be directed to this page
 * from [InstallmentChooserFragment] page.
 */
internal class InstallmentTermChooserFragment : OmiseListFragment<InstallmentTermChooserItem>() {
    var requester: PaymentCreatorRequester<Source>? = null
    private val installment: PaymentMethod? by lazy {
        arguments?.getParcelable<PaymentMethod>(EXTRA_INSTALLMENT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sourceType = (installment?.backendType as BackendType.Source).sourceType
        title = when (sourceType as SourceType.Installment) {
            SourceType.Installment.Bay -> InstallmentChooserItem.Bay
            SourceType.Installment.FirstChoice -> InstallmentChooserItem.FirstChoice
            SourceType.Installment.Bbl -> InstallmentChooserItem.Bbl
            SourceType.Installment.Ktc -> InstallmentChooserItem.Ktc
            SourceType.Installment.KBank -> InstallmentChooserItem.KBank
            is SourceType.Installment.Unknown -> InstallmentChooserItem.Unknown(sourceType.name.orEmpty())
        }.run {
            titleRes?.let { getString(it) } ?: title
        }
        setHasOptionsMenu(true)
    }

    override fun listItems(): List<InstallmentTermChooserItem> {
        return installment
                ?.installmentTerms
                .orEmpty()
                .map {
                    InstallmentTermChooserItem(
                            iconRes = R.drawable.payment_installment,
                            title = with(it) {
                                if (this > 1) {
                                    getString(R.string.payment_method_installment_term_months_title, this)
                                } else {
                                    getString(R.string.payment_method_installment_term_month_title, this)
                                }
                            },
                            installmentTerm = it,
                            indicatorIconRes = R.drawable.ic_redirect
                    )
                }
    }

    override fun onListItemClicked(item: InstallmentTermChooserItem) {
        val req = requester ?: return
        val sourceType = (installment?.backendType as? BackendType.Source)?.sourceType ?: return

        view?.let { setAllViewsEnabled(it, false) }
        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType)
                .installmentTerm(item.installmentTerm)
                .build()
        requester?.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    companion object {
        private const val EXTRA_INSTALLMENT = "InstallmentTermChooserFragment.installment"
        fun newInstance(installment: PaymentMethod) =
                InstallmentTermChooserFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(EXTRA_INSTALLMENT, installment)
                    }
                }
    }
}

internal data class InstallmentTermChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String,
        val installmentTerm: Int,
        @DrawableRes override val indicatorIconRes: Int
) : OmiseListItem
