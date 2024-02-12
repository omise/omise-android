package co.omise.android.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * OmiseFragment is the base class for all other fragments in the SDK.
 */
abstract class OmiseFragment : Fragment() {
    var title: String? = null

    private val actionBar: ActionBar?
        get() = (activity as? AppCompatActivity)?.supportActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.addOnBackStackChangedListener {
            actionBar?.title = title
        }
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager?.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setAllViewsEnabled(
        view: View,
        isEnabled: Boolean,
    ) {
        view.isEnabled = isEnabled
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                val targetView = view.getChildAt(index)
                targetView.isEnabled = isEnabled
                if (targetView is ViewGroup) {
                    setAllViewsEnabled(targetView, isEnabled)
                }
            }
        }
    }
}
