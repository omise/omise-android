package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.*

/**
 * FpxBankChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * availabl FPX bank options list for the user to choose from.
 */
internal class FpxBankChooserFragment : OmiseListFragment<FpxResource>() {

    var requester: PaymentCreatorRequester<Source>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.internet_banking_chooser_title)
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: FpxResource) {
        val req = requester ?: return
        val email = arguments?.getString(FPX_EMAIL).orEmpty()
        val banks = arguments?.getParcelableArray(FPX_BANKS).orEmpty()
        val bankCode = item.bankCode.orEmpty()

        view?.let { setAllViewsEnabled(it, false) }

        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, SourceType.Fpx)
                .email(email)
                .bank(bankCode)
                .build()

        view?.let { setAllViewsEnabled(it, false) }
        req.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    override fun listItems(): List<FpxResource> {
        val capabilityBanks = arguments?.getParcelableArray(FPX_BANKS).orEmpty() as Array<Bank>

        val allowedBanks = FpxResource.all
                .filter { bank -> capabilityBanks.any { it.code == bank.bankCode } }

        return allowedBanks
    }

    companion object {
        private const val FPX_EMAIL = "FpxBankChooserFragment.email"
        private const val FPX_BANKS = "FpxBankChooserFragment.banks"

        fun newInstance(banks: List<Bank>, email: String) =
                FpxBankChooserFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArray(FPX_BANKS, banks.toTypedArray())
                        putString(FPX_EMAIL, email)
                    }
                }
    }
}
