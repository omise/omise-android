package co.omise.android;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CreditCardEditTextTest {

    @Rule
    public ActivityTestRule<TestCreditCardEditTextActivity> rule = new ActivityTestRule<>(TestCreditCardEditTextActivity.class, true, true);

    @Test
    public void creditCard() throws InterruptedException {
        onView(withId(R.id.credit_card_edit))
                .perform(typeText("4242424242424242"), closeSoftKeyboard())
                .check(matches(withText("4242 4242 4242 4242")));
    }

    @Test
    public void enter5Digits() {
        onView(withId(R.id.credit_card_edit))
                .perform(typeText("42424"), closeSoftKeyboard())
                .check(matches(withText("4242 4")));
    }

    @Test
    public void delete() {
        onView(withId(R.id.credit_card_edit))
                .perform(typeText("42424"),
                        pressKey(KeyEvent.KEYCODE_DEL),
                        pressKey(KeyEvent.KEYCODE_DEL),
                        closeSoftKeyboard())
                .check(matches(withText("424")));
    }

    @Test
    public void typeOver19Characters() {
        onView(withId(R.id.credit_card_edit))
                .perform(typeText("42424242424242424242424"), closeSoftKeyboard())
                .check(matches(withText("4242 4242 4242 4242")));
    }
}
