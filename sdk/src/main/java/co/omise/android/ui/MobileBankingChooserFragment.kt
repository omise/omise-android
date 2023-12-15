package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

/**
 * MobileBankingChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Mobile Banking options list for the user to choose from.
 */
internal class MobileBankingChooserFragment : OmiseListFragment<MobileBankingResource>() {
    private val allowedBanks: List<SourceType.MobileBanking> by lazy {
        val args = arguments ?: return@lazy emptyList<SourceType.MobileBanking>()
        val paymentMethods = args.getParcelableArray(EXTRA_MOBILE_BANKING_METHODS) as Array<PaymentMethod>
        return@lazy paymentMethods
            .filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.MobileBanking }
            .map { (it.backendType as BackendType.Source).sourceType as SourceType.MobileBanking }
    }

    var requester: PaymentCreatorRequester<Source>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.mobile_banking_chooser_title)
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: MobileBankingResource) {
        val req = requester ?: return

        view?.let { setAllViewsEnabled(it, false) }

        val sourceType = item.sourceType
        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType).build()
        requester?.request(request) { view?.let { setAllViewsEnabled(it, true) } }
    }

    override fun listItems(): List<MobileBankingResource> {
        return allowedBanks.mobileBankingResources
    }

    companion object {
        private const val EXTRA_MOBILE_BANKING_METHODS = "MobileBankingChooserFragment.mobileBankingMethods"

        fun newInstance(availableBanks: List<PaymentMethod>) =
            MobileBankingChooserFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelableArray(EXTRA_MOBILE_BANKING_METHODS, availableBanks.toTypedArray())
                    }
            }
    }
}
