package co.omise.android.ui


import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.SourceType

internal class InternetBankingChooserFragment : OmiseListFragment<InternetBankingChooserItem>() {

//    var paymentCreatorFlow: PaymentCreatorFlow? = null

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

//        paymentCreatorFlow?.request(PaymentCreatorParameter.InternetBanking(sourceType))
    }

    override fun listItems(): List<InternetBankingChooserItem> {
        return InternetBankingChooserItem.allElements
    }

    companion object {
        fun newInstance() = InternetBankingChooserFragment()
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
