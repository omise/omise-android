package co.omise.android.ui


import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

internal class InstallmentChooserFragment : OmiseListFragment<InstallmentChooserItem>() {

    private val paymentMethods: List<PaymentMethod> by lazy {
        val args = arguments ?: return@lazy emptyList<PaymentMethod>()
        return@lazy (args.getParcelableArray(EXTRA_INSTALLMENT_METHODS) as Array<PaymentMethod>).toList()
    }
    private val allowedInstallments: List<SourceType.Installment> by lazy {
        return@lazy paymentMethods.filter { it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.Installment }
                .map { (it.backendType as BackendType.Source).sourceType as SourceType.Installment }
    }
    var navigation: PaymentCreatorNavigation? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.installments_title)
        setHasOptionsMenu(true)
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
        val choseInstallment = paymentMethods.first { (it.backendType as BackendType.Source).sourceType == sourceType }
        navigation?.navigateToInstallmentTermChooser(choseInstallment)
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
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int
) : OmiseListItem {
    companion object
    object Bbl : InstallmentChooserItem(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_installment_bbl_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object KBank : InstallmentChooserItem(
            iconRes = R.drawable.payment_kasikorn,
            titleRes = R.string.payment_method_installment_kasikorn_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object Bay : InstallmentChooserItem(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_installment_bay_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object FirstChoice : InstallmentChooserItem(
            iconRes = R.drawable.payment_first_choice,
            titleRes = R.string.payment_method_installment_first_choice_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object Ktc : InstallmentChooserItem(
            iconRes = R.drawable.payment_ktc,
            titleRes = R.string.payment_method_installment_ktc_title,
            indicatorIconRes = R.drawable.ic_next
    )

    data class Unknown(val bankName: String) : InstallmentChooserItem(
            iconRes = R.drawable.payment_installment,
            title = bankName,
            indicatorIconRes = R.drawable.ic_next
    )
}
