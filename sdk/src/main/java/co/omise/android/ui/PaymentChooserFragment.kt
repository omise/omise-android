package co.omise.android.ui

import androidx.fragment.app.Fragment

class PaymentChooserFragment : OmiseListFragment<PaymentChooserItem>() {
    var navigation: PaymentCreatorNavigation? = null

    override fun onListItemClicked(option: PaymentChooserItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

sealed class PaymentChooserItem : OmiseListItem {

}
