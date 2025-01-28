package co.omise.android.ui

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

private fun typeNumberText(numberText: String): ViewAction =
    object : ViewAction {
        override fun getDescription(): String = "Type number text: $numberText"

        override fun getConstraints(): Matcher<View> =
            allOf(
                isDisplayed(),
                isAssignableFrom(
                    OmiseEditText::class.java,
                ),
            )

        override fun perform(
            uiController: UiController?,
            view: View?,
        ) {
            val editText = view as? OmiseEditText ?: return
            numberText.forEach { editText.append(it.toString()) }
        }
    }
