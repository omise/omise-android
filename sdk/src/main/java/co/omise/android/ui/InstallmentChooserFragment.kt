package co.omise.android.ui


import android.os.Bundle
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.PaymentMethod
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

/**
 * InstallmentChooserFragment is the UI class, extended from base [OmiseListFragment] to show
 * available Installment options list for the user to choose from.
 */
internal class InstallmentChooserFragment : OmiseListFragment<InstallmentResource>() {

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

    override fun listItems(): List<InstallmentResource> {
        return allowedInstallments.installmentResources
    }

    override fun onListItemClicked(item: InstallmentResource) {
        val choseInstallment = paymentMethods.first { (it.backendType as BackendType.Source).sourceType == item.sourceType }
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
