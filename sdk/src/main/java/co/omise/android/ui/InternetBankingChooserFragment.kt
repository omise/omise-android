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

internal class InternetBankingChooserFragment : OmiseListFragment<InternetBankingChooserItem>() {

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

    override fun onListItemClicked(item: InternetBankingChooserItem) {
        val req = requester ?: return

        view?.let { setAllViewsEnabled(it, false) }

        val sourceType = when (item) {
            InternetBankingChooserItem.Bbl -> SourceType.InternetBanking.Bbl
            InternetBankingChooserItem.Scb -> SourceType.InternetBanking.Scb
            InternetBankingChooserItem.Bay -> SourceType.InternetBanking.Bay
            InternetBankingChooserItem.Ktb -> SourceType.InternetBanking.Ktb
            is InternetBankingChooserItem.Unknown -> SourceType.Unknown(item.bankName)
        }

        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType).build()
        requester?.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    override fun listItems(): List<InternetBankingChooserItem> {
        return allowedBanks.map {
            when (it) {
                SourceType.InternetBanking.Bbl -> InternetBankingChooserItem.Bbl
                SourceType.InternetBanking.Scb -> InternetBankingChooserItem.Scb
                SourceType.InternetBanking.Bay -> InternetBankingChooserItem.Bay
                SourceType.InternetBanking.Ktb -> InternetBankingChooserItem.Ktb
                is SourceType.InternetBanking.Unknown -> InternetBankingChooserItem.Unknown(it.name.orEmpty())
            }
        }
    }

    companion object {
        private const val EXTRA_INTERNET_BANKING_METHODS = "InternetBankingChooserFragment.internetBankingMethods"

        fun newInstance(availableBanks: List<PaymentMethod>) =
                InternetBankingChooserFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArray(EXTRA_INTERNET_BANKING_METHODS, availableBanks.toTypedArray())
                    }
                }
    }
}

sealed class InternetBankingChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int
) : OmiseListItem {
    object Bbl : InternetBankingChooserItem(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_internet_banking_bbl_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    object Scb : InternetBankingChooserItem(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_internet_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    object Bay : InternetBankingChooserItem(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_internet_banking_bay_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    object Ktb : InternetBankingChooserItem(
            iconRes = R.drawable.payment_ktb,
            titleRes = R.string.payment_method_internet_banking_ktb_title,
            indicatorIconRes = R.drawable.ic_redirect
    )

    data class Unknown(val bankName: String) : InternetBankingChooserItem(
            iconRes = R.drawable.payment_banking,
            title = bankName,
            indicatorIconRes = R.drawable.ic_redirect
    )
}
