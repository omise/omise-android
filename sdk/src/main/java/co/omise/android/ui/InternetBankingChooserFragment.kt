package co.omise.android.ui


import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

internal class InternetBankingChooserFragment : OmiseListFragment<InternetBankingChooserItem>() {

    //    var paymentCreatorFlow: PaymentCreatorFlow? = null
    private val availableBanks: List<SourceType.InternetBanking> by lazy {
        val args = arguments ?: return@lazy emptyList<SourceType.InternetBanking>()
        val paymentMethods = args.getParcelableArray(EXTRA_INTERNET_BANKING_METHODS) as Array<PaymentMethod>
        return@lazy paymentMethods.filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.InternetBanking }
                .map { (it.backendType as BackendType.Source).sourceType as SourceType.InternetBanking }
    }

    var requester: PaymentCreatorRequester<Source>? = null
    var listener: InternetBankingChooserFragmentListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = "Internet Banking"
        setHasOptionsMenu(true)
    }

    override fun onListItemClicked(item: InternetBankingChooserItem) {
        val sourceType = when (item) {
            InternetBankingChooserItem.Bbl -> SourceType.InternetBanking.Bbl
            InternetBankingChooserItem.Scb -> SourceType.InternetBanking.Scb
            InternetBankingChooserItem.Bay -> SourceType.InternetBanking.Bay
            InternetBankingChooserItem.Ktb -> SourceType.InternetBanking.Ktb
            is InternetBankingChooserItem.Unknown -> SourceType.Unknown(item.bankName)
        }

        val req = requester ?: return

        val request = Source.CreateSourceRequestBuilder(req.amount, req.currency, sourceType).build()
        requester?.request(request) {
            if (it.isSuccess) {

            } else {
            }
        }
    }

    override fun listItems(): List<InternetBankingChooserItem> {
        return availableBanks.map {
            when (it) {
                SourceType.InternetBanking.Bbl -> InternetBankingChooserItem.Bbl
                SourceType.InternetBanking.Scb -> InternetBankingChooserItem.Scb
                SourceType.InternetBanking.Bay -> InternetBankingChooserItem.Bay
                SourceType.InternetBanking.Ktb -> InternetBankingChooserItem.Ktb
                else -> InternetBankingChooserItem.Unknown(it.name.orEmpty())
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
        override val icon: Int,
        override val title: String,
        override val indicatorIcon: Int
) : OmiseListItem {
    companion object
    object Bbl : InternetBankingChooserItem(R.drawable.payment_bbl, "Bangkok Bank", R.drawable.ic_redirect)
    object Scb : InternetBankingChooserItem(R.drawable.payment_scb, "Siam Commercial Bank", R.drawable.ic_redirect)
    object Bay : InternetBankingChooserItem(R.drawable.payment_bay, "Bank of Ayudhya", R.drawable.ic_redirect)
    object Ktb : InternetBankingChooserItem(R.drawable.payment_ktb, "Krungthai Bank", R.drawable.ic_redirect)
    data class Unknown(val bankName: String) : InternetBankingChooserItem(R.drawable.payment_banking, bankName, R.drawable.ic_redirect)
}

val InternetBankingChooserItem.Companion.allElements: List<InternetBankingChooserItem>
    get() = listOf(
            InternetBankingChooserItem.Bbl,
            InternetBankingChooserItem.Scb,
            InternetBankingChooserItem.Bay,
            InternetBankingChooserItem.Ktb
    )

interface InternetBankingChooserFragmentListener {
    fun onCreateSourceCompleted(result: Result<Source>)
}
