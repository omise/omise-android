package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.*

/**
 * DuitNowOBWBankChooserFragment is the UI class, extended from base [OmiseListFragment] to show available
 * DuitNow OBW bank options list for the user to choose from.
 */
internal class DuitNowOBWBankChooserFragment : OmiseListFragment<DuitNowOBWResource>() {

    var requester: PaymentCreatorRequester<Source>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.payment_method_duitnow_obw_title)
        noDataText.text = getString(R.string.banks_no_data)
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: DuitNowOBWResource) {
        val req = requester ?: return
        val bankCode = item.bankCode.orEmpty()

        view?.let { setAllViewsEnabled(it, false) }

        val request =
                Source.CreateSourceRequestBuilder(req.amount, req.currency, SourceType.DuitNowOBW)
                        .bank(bankCode)
                        .build()

        view?.let { setAllViewsEnabled(it, false) }
        req.request(request) { view?.let { setAllViewsEnabled(it, true) } }
    }

    override fun listItems(): List<DuitNowOBWResource> {
        val capabilityBanks =
                arguments?.getParcelableArray(DUITNOWOBW_BANKS).orEmpty() as Array<Bank>

        return capabilityBanks.map {
            DuitNowOBWResource(
                    iconRes = DuitNowOBWResource.getBankImageFromCode(it.code),
                    title = it.name,
                    bankCode = it.code,
                    enabled = it.active
            )
        }
    }

    companion object {
        private const val DUITNOWOBW_BANKS = "DuitNowOBWBankChooserFragment.banks"

        fun newInstance(banks: List<Bank>?) =
                DuitNowOBWBankChooserFragment().apply {
                    arguments =
                            Bundle().apply {
                                putParcelableArray(DUITNOWOBW_BANKS, banks?.toTypedArray())
                            }
                }
    }
}
