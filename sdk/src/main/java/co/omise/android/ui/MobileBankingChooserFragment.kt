package co.omise.android.ui

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
internal class MobileBankingChooserFragment : OmiseListFragment<MobileBankingChooserItem>() {

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

    override fun onListItemClicked(item: MobileBankingChooserItem) {
        val req = requester ?: return

        view?.let { setAllViewsEnabled(it, false) }

        val sourceType = when (item) {
            MobileBankingChooserItem.Scb -> SourceType.MobileBanking.Scb
            is MobileBankingChooserItem.Unknown -> SourceType.Unknown(item.bankName)
        }


        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType).build()
        requester?.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    override fun listItems(): List<MobileBankingChooserItem> {
        return allowedBanks.map {
            when (it) {
                SourceType.MobileBanking.Scb -> MobileBankingChooserItem.Scb
                is SourceType.MobileBanking.Unknown -> MobileBankingChooserItem.Unknown(it.name.orEmpty())
            }
        }
    }

    companion object {
        private const val EXTRA_MOBILE_BANKING_METHODS = "MobileBankingChooserFragment.mobileBankingMethods"

        fun newInstance(availableBanks: List<PaymentMethod>) =
                MobileBankingChooserFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArray(EXTRA_MOBILE_BANKING_METHODS, availableBanks.toTypedArray())
                    }
                }
    }
}

sealed class MobileBankingChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int
) : OmiseListItem {
    
    object Scb : MobileBankingChooserItem(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_mobile_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    data class Unknown(val bankName: String) : MobileBankingChooserItem(
            iconRes = R.drawable.payment_banking,
            title = bankName,
            indicatorIconRes = R.drawable.ic_redirect
    )
}
