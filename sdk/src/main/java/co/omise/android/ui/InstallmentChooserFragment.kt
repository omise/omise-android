package co.omise.android.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import co.omise.android.R
import co.omise.android.extensions.getParcelableArrayCompat
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
        return@lazy (args.getParcelableArrayCompat<PaymentMethod>(EXTRA_INSTALLMENT_METHODS)).toList()
    }
    private val requestedInstallmentAmount: Long by lazy {
        val args = arguments ?: return@lazy 0
        return@lazy (args.getLong(EXTRA_REQUESTED_INSTALLMENT_AMOUNT))
    }
    private val capabilityInstallmentAmount: Long by lazy {
        val args = arguments ?: return@lazy 0
        return@lazy (args.getLong(EXTRA_CAPABILITY_INSTALLMENT_AMOUNT))
    }
    private val allowedInstallments: List<SourceType.Installment> by lazy {
        return@lazy paymentMethods.filter {
            it.backendType is BackendType.Source && (it.backendType as BackendType.Source).sourceType is SourceType.Installment
        }
            .map { (it.backendType as BackendType.Source).sourceType as SourceType.Installment }
    }
    var navigation: PaymentCreatorNavigation? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        title = getString(R.string.installments_title)
        setHasOptionsMenu(true)
        if (requestedInstallmentAmount < capabilityInstallmentAmount) {
            addNoBanksSupportedMessage()
        }
    }

    override fun listItems(): List<InstallmentResource> {
        val filteredSources = allowedInstallments.installmentResources.toMutableList()
        val sourceNames = allowedInstallments.installmentResources.map { it.sourceType.name }

        allowedInstallments.installmentResources.forEach { source ->
            // check if the source is white label installment
            val sourceKey = source.sourceType.name!!
            val isWlbSource = sourceKey.contains("_wlb_")
            // if wlb then remove the source that is of the same bank but non wlb
            if (isWlbSource) {
                val containWlbAndNonWlb = sourceNames.contains(sourceKey.replace("_wlb_", "_"))
                if (containWlbAndNonWlb) {
                    // remove the non wlb source
                    filteredSources.removeAll { it.sourceType.name == source.sourceType.name!!.replace("_wlb_", "_") }
                }
            }
        }

        return filteredSources.map { resource ->
            resource.apply {
                enabled = requestedInstallmentAmount >= capabilityInstallmentAmount
            }
        }
    }

    private fun addNoBanksSupportedMessage() {
        val noBanksMessageLayOut = view?.findViewById<LinearLayout>(R.id.message_layout)
        noBanksMessageLayOut?.visibility = View.VISIBLE
    }

    override fun onListItemClicked(item: InstallmentResource) {
        val choseInstallment = paymentMethods.first { (it.backendType as BackendType.Source).sourceType == item.sourceType }
        navigation?.navigateToInstallmentTermChooser(choseInstallment)
    }

    companion object {
        private const val EXTRA_INSTALLMENT_METHODS = "InstallmentChooserFragment.installmentMethods"
        private const val EXTRA_REQUESTED_INSTALLMENT_AMOUNT = "InstallmentChooserFragment.requestedInstallmentAmount"
        private const val EXTRA_CAPABILITY_INSTALLMENT_AMOUNT = "InstallmentChooserFragment.capabilityInstallmentAmount"

        fun newInstance(
            availableBanks: List<PaymentMethod>,
            requestedInstallmentAmount: Long,
            capabilityInstallmentAmount: Long,
        ) = InstallmentChooserFragment().apply {
            arguments =
                Bundle().apply {
                    putParcelableArray(EXTRA_INSTALLMENT_METHODS, availableBanks.toTypedArray())
                    putLong(EXTRA_REQUESTED_INSTALLMENT_AMOUNT, requestedInstallmentAmount)
                    putLong(EXTRA_CAPABILITY_INSTALLMENT_AMOUNT, capabilityInstallmentAmount)
                }
        }
    }
}
