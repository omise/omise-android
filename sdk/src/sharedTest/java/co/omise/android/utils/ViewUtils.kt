package co.omise.android.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher


fun focus(isFocus: Boolean = true): ViewAction =
        object : ViewAction {
            override fun getDescription(): String = "Set focus status to a View."

            override fun getConstraints(): Matcher<View> =
                    allOf(isDisplayed(), isDescendantOfA(isAssignableFrom(View::class.java)))

            override fun perform(uiController: UiController?, view: View?) {
                if (isFocus) {
                    view?.requestFocus()
                } else {
                    view?.clearFocus()
                }
                uiController?.loopMainThreadUntilIdle()
            }
        }
