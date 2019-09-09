package co.omise.android.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates


abstract class OmiseFragment : Fragment() {
    var title: String by Delegates.observable("", { _, _, newValue ->
        (activity as? AppCompatActivity)?.supportActionBar?.title = newValue
    })
}
