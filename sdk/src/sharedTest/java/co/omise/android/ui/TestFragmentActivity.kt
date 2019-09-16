package co.omise.android.ui

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.omise.android.R


class TestFragmentActivity : AppCompatActivity() {
    var activityResult: Instrumentation.ActivityResult? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val frameLayout = FrameLayout(this).apply {
            id = android.R.id.content
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(frameLayout)
        setTheme(R.style.OmiseTheme)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResult = Instrumentation.ActivityResult(resultCode, data)
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .addToBackStack("test_fragment")
                .commit()
    }
}