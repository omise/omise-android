package co.omise.android.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry

/**
 *
 * Intercept the activity lifecycle and call the [onCreatedBlock] when the activity is created.
 */
fun interceptActivityLifecycle(onCreatedBlock: (Activity, Bundle?) -> Unit) {
    (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application).registerActivityLifecycleCallbacks(
        object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?,
            ) {
                onCreatedBlock(activity, savedInstanceState)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle,
            ) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        },
    )
}
