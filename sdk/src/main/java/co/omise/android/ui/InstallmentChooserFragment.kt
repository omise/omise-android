package co.omise.android.ui


import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

internal class InstallmentChooserFragment : OmiseListFragment<InstallmentChooserItem>() {

    private val allowedInstallments: List<SourceType.Installment> by lazy {
        val args = arguments ?: return@lazy emptyList<SourceType.Installment>()
        val paymentMethods = args.getParcelableArray(EXTRA_INSTALLMENT_METHODS) as Array<PaymentMethod>
        return@lazy paymentMethods
                .filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.Installment }
                .map { (it.backendType as BackendType.Source).sourceType as SourceType.Installment }
    }
    var navigation: PaymentCreatorNavigation? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.installments_title)
    }

    override fun listItems(): List<InstallmentChooserItem> {
        return allowedInstallments.map {
            when (it) {
                SourceType.Installment.KBank -> InstallmentChooserItem.KBank
                SourceType.Installment.Bay -> InstallmentChooserItem.Bay
                SourceType.Installment.FirstChoice -> InstallmentChooserItem.FirstChoice
                SourceType.Installment.Bbl -> InstallmentChooserItem.Bbl
                SourceType.Installment.Ktc -> InstallmentChooserItem.Ktc
                is SourceType.Installment.Unknown -> InstallmentChooserItem.Unknown(it.name.orEmpty())
            }
        }
    }

    override fun onListItemClicked(item: InstallmentChooserItem) {
        val sourceType = when (item) {
            InstallmentChooserItem.Bbl -> SourceType.Installment.Bbl
            InstallmentChooserItem.KBank -> SourceType.Installment.KBank
            InstallmentChooserItem.Bay -> SourceType.Installment.Bay
            InstallmentChooserItem.FirstChoice -> SourceType.Installment.FirstChoice
            InstallmentChooserItem.Ktc -> SourceType.Installment.Ktc
            is InstallmentChooserItem.Unknown -> SourceType.Installment.Unknown(item.bankName)
        }
//        paymentCreatorActivity.showInstallmentTermChooser(installment)
    }

    companion object {
        private const val EXTRA_INSTALLMENT_METHODS = "InstallmentChooserFragment.installmentMethods"

        fun newInstance(availableBanks: List<PaymentMethod>) =
                InstallmentChooserFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArray(EXTRA_INSTALLMENT_METHODS, availableBanks.toTypedArray())
                    }
                }
    }
}

internal sealed class InstallmentChooserItem(
        override val icon: Int,
        override val title: String,
        override val indicatorIcon: Int
) : OmiseListItem {
    companion object
    object Bbl : InstallmentChooserItem(R.drawable.payment_bbl, "Bangkok Bank", R.drawable.ic_next)
    object KBank : InstallmentChooserItem(R.drawable.payment_kasikorn, "Kasikorn", R.drawable.ic_next)
    object Bay : InstallmentChooserItem(R.drawable.payment_bay, "krungsri", R.drawable.ic_next)
    object FirstChoice : InstallmentChooserItem(R.drawable.payment_first_choice, "krungsri First FirstChoice", R.drawable.ic_next)
    object Ktc : InstallmentChooserItem(R.drawable.payment_ktc, "KTC", R.drawable.ic_next)
    data class Unknown(val bankName: String) : InstallmentChooserItem(R.drawable.payment_installment, bankName, R.drawable.ic_next)
}
