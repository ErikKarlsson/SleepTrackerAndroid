package net.erikkarlsson.simplesleeptracker.espresso

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import android.view.View
import org.hamcrest.Matchers.not

object ViewAssertions {

    fun shouldDisplayView(@IdRes resId: Int) {
        onView(withId(resId)).check(matches(isDisplayed()))
    }

    fun shouldNotDisplayView(@IdRes resId: Int) {
        onView(withId(resId)).check(matches(not<View>(isDisplayed())))
    }

    fun shouldDisplayErrorText(@IdRes editTextResId: Int, text: String) {
        onView(withId(editTextResId))
            .check(matches(hasErrorText(text)))
    }
}
