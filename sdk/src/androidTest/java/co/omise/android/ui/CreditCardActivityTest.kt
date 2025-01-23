package co.omise.android.ui

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launchActivityForResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.models.Capability
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

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
