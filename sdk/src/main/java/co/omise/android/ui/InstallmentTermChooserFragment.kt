package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import co.omise.android.R
import co.omise.android.extensions.getParcelableCompat
import co.omise.android.models.Amount
import co.omise.android.models.BackendType
import co.omise.android.models.CardHolderDataList
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.backendType
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_CARD_HOLDER_DATA
import co.omise.android.ui.PaymentCreatorActivity.Companion.REQUEST_CREDIT_CARD_WITH_SOURCE

/**
 * InstallmentTermChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Installment terms list for the user to choose from. User would be directed to this page
 * from [InstallmentChooserFragment] page.
 */
internal class InstallmentTermChooserFragment : OmiseListFragment<InstallmentTermResource>() {
    var requester: PaymentCreatorRequester<Source>? = null
    var cardHolderDataList: CardHolderDataList? = null
    private val installment: PaymentMethod? by lazy {
        arguments?.getParcelableCompat<PaymentMethod>(EXTRA_INSTALLMENT)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        title =
            InstallmentResource.all
                .find { it.sourceType == (installment?.backendType as BackendType.Source).sourceType }
                ?.let { item -> item.titleRes?.let { getString(it) } ?: title }
        setHasOptionsMenu(true)
    }

    override fun listItems(): List<InstallmentTermResource> {
        val currency = requester!!.currency
        val minimumInstallmentAmountPerType =
            mapOf(
                SourceType.Installment.Bay to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.BayWlb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.FirstChoice to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.FirstChoiceWlb to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.Bbl to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.BblWlb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.Mbb to Amount.fromLocalAmount(83.33, currency),
                SourceType.Installment.Ktc to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.KtcWlb to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.KBank to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.KBankWlb to Amount.fromLocalAmount(300.0, currency),
                SourceType.Installment.Scb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.ScbWlb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.Ttb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.TtbWlb to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.Uob to Amount.fromLocalAmount(500.0, currency),
                SourceType.Installment.UobWlb to Amount.fromLocalAmount(500.0, currency),
            )
        val interestRatePerType =
            mapOf(
                SourceType.Installment.Bay to 0.0074,
                SourceType.Installment.BayWlb to 0.0074,
                SourceType.Installment.FirstChoice to 0.0116,
                SourceType.Installment.FirstChoiceWlb to 0.0116,
                SourceType.Installment.Bbl to 0.0074,
                SourceType.Installment.BblWlb to 0.0074,
                SourceType.Installment.Mbb to 0.0,
                SourceType.Installment.Ktc to 0.0074,
                SourceType.Installment.KtcWlb to 0.0074,
                SourceType.Installment.KBank to 0.0065,
                SourceType.Installment.KBankWlb to 0.0065,
                SourceType.Installment.Scb to 0.0074,
                SourceType.Installment.ScbWlb to 0.0074,
                SourceType.Installment.Ttb to 0.008,
                SourceType.Installment.TtbWlb to 0.008,
                SourceType.Installment.Uob to 0.0064,
                SourceType.Installment.UobWlb to 0.0064,
            )
        val sourceType = (installment?.backendType as? BackendType.Source)?.sourceType!!
        // White label installments require token and source
        val requiresTokenRequest = sourceType.name!!.lowercase().contains("_wlb_")
        return installment
            ?.installmentTerms
            .orEmpty()
            .filter { term ->
                val minimumAmount = minimumInstallmentAmountPerType[sourceType]
                val req = requester
                val amount = req!!.amount
                val zeroInterestInstallments = req.capability.zeroInterestInstallments
                var interestAmount = 0.0
                if (!zeroInterestInstallments) {
                    val rate = interestRatePerType[sourceType] ?: 0.0
                    interestAmount = amount.toDouble() * rate
                }
                val installmentAmountPerMonth = (amount + interestAmount) / term
                minimumAmount == null || installmentAmountPerMonth >= minimumAmount.amount
            }
            .map { term ->
                InstallmentTermResource(
                    title =
                        with(term) {
                            if (this > 1) {
                                getString(R.string.payment_method_installment_term_months_title, this)
                            } else {
                                getString(R.string.payment_method_installment_term_month_title, this)
                            }
                        },
                    installmentTerm = term,
                    indicatorIconRes = if (requiresTokenRequest) R.drawable.ic_next else R.drawable.ic_redirect,
                )
            }
    }

    override fun onListItemClicked(item: InstallmentTermResource) {
        val req = requester ?: return
        val sourceType = (installment?.backendType as? BackendType.Source)?.sourceType!!
        // White label installments require token and source
        val requiresTokenRequest = sourceType.name!!.lowercase().contains("_wlb_")

        if (requiresTokenRequest) {
            // navigate to credit card screen
            val intent =
                Intent(activity, CreditCardActivity::class.java).apply {
                    putExtra(OmiseActivity.EXTRA_PKEY, activity?.intent?.getStringExtra(OmiseActivity.EXTRA_PKEY))
                    putExtra(
                        OmiseActivity.EXTRA_IS_SECURE,
                        activity?.intent?.getBooleanExtra(
                            OmiseActivity.EXTRA_IS_SECURE,
                            true,
                        ),
                    )
                    putExtra(OmiseActivity.EXTRA_SELECTED_INSTALLMENTS_TERM, item.installmentTerm)
                    putExtra(OmiseActivity.EXTRA_SELECTED_INSTALLMENTS_PAYMENT_METHOD, installment)
                    putExtra(OmiseActivity.EXTRA_CURRENCY, req.currency)
                    putExtra(OmiseActivity.EXTRA_CAPABILITY, req.capability)
                    putExtra(OmiseActivity.EXTRA_AMOUNT, req.amount)
                    putExtra(EXTRA_CARD_HOLDER_DATA, cardHolderDataList ?: CardHolderDataList(arrayListOf()))
                }
            activity?.startActivityForResult(intent, REQUEST_CREDIT_CARD_WITH_SOURCE)
        } else {
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
