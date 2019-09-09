package co.omise.android.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


fun itemCount(count: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("with items count: ")
            description?.appendValue(count)
        }

        override fun matchesSafely(item: RecyclerView?): Boolean {
            val target = item ?: return false
            return target.childCount == count
        }
    }
}
