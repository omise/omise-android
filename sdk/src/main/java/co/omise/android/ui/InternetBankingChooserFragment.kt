package co.omise.android.ui

import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

/**
 * InternetBankingChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Internet Banking options list for the user to choose from.
 */
internal class InternetBankingChooserFragment : OmiseListFragment<InternetBankingResource>() {
    private val allowedBanks: List<SourceType.InternetBanking> by lazy {
        val args = arguments ?: return@lazy emptyList<SourceType.InternetBanking>()
        val paymentMethods = args.getParcelableArray(EXTRA_INTERNET_BANKING_METHODS) as Array<PaymentMethod>
        return@lazy paymentMethods
            .filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.InternetBanking }
            .map { (it.backendType as BackendType.Source).sourceType as SourceType.InternetBanking }
    }

    var requester: PaymentCreatorRequester<Source>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.internet_banking_chooser_title)
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: InternetBankingResource) {
        val req = requester ?: return

        view?.let { setAllViewsEnabled(it, false) }

        val sourceType = item.sourceType
        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType).build()
        requester?.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    override fun listItems(): List<InternetBankingResource> {
        return allowedBanks.internetBankingResources
    }

    companion object {
        private const val EXTRA_INTERNET_BANKING_METHODS = "InternetBankingChooserFragment.internetBankingMethods"

        fun newInstance(availableBanks: List<PaymentMethod>) =
            InternetBankingChooserFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelableArray(EXTRA_INTERNET_BANKING_METHODS, availableBanks.toTypedArray())
                    }
            }
    }
}
