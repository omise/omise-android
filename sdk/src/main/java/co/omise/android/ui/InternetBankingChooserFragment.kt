package co.omise.android.ui


import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.PaymentMethod
import co.omise.android.models.PaymentMethodType
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.method

internal class InternetBankingChooserFragment : OmiseListFragment<InternetBankingChooserItem>() {

    //    var paymentCreatorFlow: PaymentCreatorFlow? = null
    private val availableBanks: List<PaymentMethodType.InternetBanking> by lazy {
        val args = arguments ?: return@lazy emptyList<PaymentMethodType.InternetBanking>()
        val paymentMethods = args.getParcelableArray(EXTRA_INTERNET_BANKING_METHODS) as Array<PaymentMethod>
        return@lazy paymentMethods.filter { it.method is PaymentMethodType.InternetBanking }
                .map { it.method as PaymentMethodType.InternetBanking }
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
            InternetBankingChooserItem.Bbl -> SourceType.InternetBankingBbl
            InternetBankingChooserItem.Scb -> SourceType.InternetBankingScb
            InternetBankingChooserItem.Bay -> SourceType.InternetBankingBay
            InternetBankingChooserItem.Ktb -> SourceType.InternetBankingKtb
            is InternetBankingChooserItem.Unknown -> SourceType.Unknown(item.bankName)
        }

        requester?.request(PaymentCreatorRequester.PaymentCreatorParameters.InternetBanking(SourceType.InstallmentKBank)) {
            if (it.isSuccess) {

            } else {

            }
        }
    }

    override fun listItems(): List<InternetBankingChooserItem> {
        return availableBanks.map {
            when (it) {
                PaymentMethodType.InternetBanking.Bbl -> InternetBankingChooserItem.Bbl
                PaymentMethodType.InternetBanking.Scb -> InternetBankingChooserItem.Scb
                PaymentMethodType.InternetBanking.Bay -> InternetBankingChooserItem.Bay
                PaymentMethodType.InternetBanking.Ktb -> InternetBankingChooserItem.Ktb
                else -> InternetBankingChooserItem.Unknown(it.value)
            }
        }

//        return InternetBankingChooserItem.allElements
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
