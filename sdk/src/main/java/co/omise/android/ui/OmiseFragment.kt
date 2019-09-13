package co.omise.android.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


abstract class OmiseFragment : Fragment() {

    var title: String? = null

    protected val actionBar: ActionBar?
        get() = (activity as? AppCompatActivity)?.supportActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager?.addOnBackStackChangedListener {
            actionBar?.title = title
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
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
}
