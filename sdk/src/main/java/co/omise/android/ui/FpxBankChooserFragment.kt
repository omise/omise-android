package co.omise.android.ui

import android.os.Bundle
import android.view.View
import co.omise.android.R
import co.omise.android.extensions.getParcelableArrayCompat
import co.omise.android.models.Bank
import co.omise.android.models.Source
import co.omise.android.models.SourceType

/**
 * FpxBankChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available FPX bank options list for the user to choose from.
 */
internal class FpxBankChooserFragment : OmiseListFragment<FpxResource>() {
    var requester: PaymentCreatorRequester<Source>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = getString(R.string.payment_method_fpx_title)
        noDataText.text = getString(R.string.banks_no_data)
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: FpxResource) {
        val req = requester ?: return
        val email = arguments?.getString(FPX_EMAIL).orEmpty()
        val bankCode = item.bankCode.orEmpty()

        view?.let { setAllViewsEnabled(it, false) }

        val request =
            Source.CreateSourceRequestBuilder(req.amount, req.currency, SourceType.Fpx())
                .email(email)
                .bank(bankCode)
                .build()

        view?.let { setAllViewsEnabled(it, false) }
        req.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    override fun listItems(): List<FpxResource> {
        val capabilityBanks = arguments?.getParcelableArrayCompat<Bank>(FPX_BANKS).orEmpty()

        return capabilityBanks.map {
            FpxResource(
                iconRes = FpxResource.getBankImageFromCode(it.code),
                title = it.name,
                bankCode = it.code,
                enabled = it.active,
            )
        }
    }

    companion object {
        private const val FPX_EMAIL = "FpxBankChooserFragment.email"
        private const val FPX_BANKS = "FpxBankChooserFragment.banks"

        fun newInstance(
            banks: List<Bank>?,
            email: String,
        ) = FpxBankChooserFragment().apply {
            arguments =
                Bundle().apply {
                    putParcelableArray(FPX_BANKS, banks?.toTypedArray())
                    putString(FPX_EMAIL, email)
                }
        }
    }
}
