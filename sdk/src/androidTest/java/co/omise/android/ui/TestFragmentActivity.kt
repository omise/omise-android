package co.omise.android.ui

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import co.omise.android.R

class TestFragmentActivity : OmiseActivity() {
    private var activityResult: Instrumentation.ActivityResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.OmiseTheme)
        super.onCreate(savedInstanceState)

        val frameLayout =
            FrameLayout(this).apply {
                id = R.id.payment_creator_container
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
            }
        setContentView(frameLayout)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResult = Instrumentation.ActivityResult(resultCode, data)
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.payment_creator_container, fragment)
            .commit()
    }
}
